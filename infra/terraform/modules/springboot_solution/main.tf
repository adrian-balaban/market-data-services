locals {
  registry = "docker-registry:5000"
}
resource "kubectl_manifest" "namespace" {
  yaml_body = <<YAML
apiVersion: v1
kind: Namespace
metadata:
  name: ${var.namespace}
YAML
}
resource "null_resource" "install_helm_repo_registry" {
  provisioner "local-exec" {
    command = "helm repo add gmelillo https://helm.melillo.me && helm repo update || true"
  }
  depends_on = [kubectl_manifest.namespace]
}
resource "terraform_data" "install_registry" {
  provisioner "local-exec" {
    command = "helm ${var.registry_helm_operation} docker-registry gmelillo/docker-registry -n kube-system"
  }
  depends_on = [null_resource.install_helm_repo_registry]
}
data "kubernetes_service" "registry_svc" {
  metadata {
    name      = "docker-registry"
    namespace = "kube-system"
  }
  depends_on = [
    terraform_data.install_registry
  ]
}
resource "terraform_data" "buildExternals" {
  provisioner "local-exec" {
    command = "cd ../../k8s/scripts && ./buildExternals.sh -tag ${var.tag} -registry localhost:5001 && cd ../../terraform/cluster"
  }
  depends_on = [terraform_data.install_registry]
}
resource "terraform_data" "buildSolution" {
  provisioner "local-exec" {
    command = "cd ../../k8s/scripts && ./buildSolution.sh -tag ${var.tag} -registry localhost:5001 && cd ../../terraform/cluster && sleep 5"
  }
  depends_on = [terraform_data.buildExternals]
}
resource "terraform_data" "deployKafka" {
  provisioner "local-exec" {
    command = "cd ../../k8s/scripts && ./deployKafka.sh -n ${var.namespace} && cd ../../terraform/cluster && kubectl wait pod --all -n ${var.namespace} --for=condition=ready --timeout=600s"
  }
  depends_on = [terraform_data.buildSolution]
}
resource "terraform_data" "deployFlink" {
  provisioner "local-exec" {
    command = "cd ../../k8s/scripts && ./deployFlink.sh -n ${var.namespace} && cd ../../terraform/cluster && kubectl wait pod --all -n ${var.namespace} --for=condition=ready --timeout=600s"
  }
  depends_on = [terraform_data.deployKafka]
}
resource "terraform_data" "deployExternals" {
  provisioner "local-exec" {
    command = "cd ../../k8s/scripts && ./deployExternals.sh -n ${var.namespace} -tag ${var.tag} -registry ${local.registry} && cd ../../terraform/cluster && kubectl wait pod --all -n ${var.namespace} --for=condition=ready --timeout=600s"
  }
  depends_on = [terraform_data.deployFlink]
}
resource "terraform_data" "deploySolution" {
  provisioner "local-exec" {
    command = "cd ../../k8s/scripts && ./deploySolution.sh -n ${var.namespace} -tag ${var.tag} -registry ${local.registry} && cd ../../terraform/cluster && kubectl wait pod --all -n ${var.namespace} --for=condition=ready --timeout=600s"
  }
  depends_on = [terraform_data.deployExternals]
}
resource "terraform_data" "set-default-namespace" {
  provisioner "local-exec" {
    command = "kubectl config set-context --current --namespace=${var.namespace}"
  }
  depends_on = [terraform_data.deploySolution]
}

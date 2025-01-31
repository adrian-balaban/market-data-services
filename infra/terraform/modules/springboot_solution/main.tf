resource "kubectl_manifest" "namespace" {
  yaml_body = <<YAML
apiVersion: v1
kind: Namespace
metadata:
  name: ${var.namespace}
YAML
}

resource "terraform_data" "create-secret-for-docker-registry" {
  provisioner "local-exec" {
    command = "kubectl create secret docker-registry docker-registry-secret --docker-server=docker.io --docker-username=adriannbalaban --docker-password=dckr_pat_54LuHTnvrOLeneiZzEwxyC6zqMw --docker-email=adrian.n.balanban@gmail.com -n ${var.namespace} || true && kubectl get secret docker-registry-secret -n ${var.namespace}"
  }
  depends_on = [kubectl_manifest.namespace]
}
# to add use of var.build & var.test
resource "terraform_data" "buildExternals" {
  count = var.build == "true" ? 1 : 0
  provisioner "local-exec" {
    command = "cd ../../k8s/scripts ../../k8s/scripts && ./buildExternals.sh -tag ${var.tag} -registry ${var.registry} && cd ../../terraform/cluster_${var.kind_or_minikube}"
  }
  depends_on = [terraform_data.create-secret-for-docker-registry]
}
# to add use of var.build & var.test
resource "terraform_data" "buildSolution" {
  count = var.build == "true" ? 1 : 0
  provisioner "local-exec" {
    command = "cd ../../k8s/scripts && ./buildSolution.sh -tag ${var.tag} -registry ${var.registry} && cd ../../terraform/cluster_${var.kind_or_minikube} && sleep 5"
  }
  depends_on = [terraform_data.buildExternals]
}
resource "terraform_data" "deployKafka" {
  provisioner "local-exec" {
    command = "cd ../../k8s/scripts && ./deployKafka.sh -n ${var.namespace} && cd ../../terraform/cluster_${var.kind_or_minikube} && kubectl wait pod --all -n ${var.namespace} --for=condition=ready --timeout=600s"
  }
  depends_on = [terraform_data.buildSolution]
}
resource "terraform_data" "deployFlink" {
  provisioner "local-exec" {
    command = "cd ../../k8s/scripts && ./deployFlink.sh -n ${var.namespace} && cd ../../terraform/cluster_${var.kind_or_minikube} && kubectl wait pod --all -n ${var.namespace} --for=condition=ready --timeout=600s"
  }
  depends_on = [terraform_data.deployKafka]
}
resource "terraform_data" "undeployExternals" {
  provisioner "local-exec" {
    command = "helm uninstall --namespace ${var.namespace} fx-market-externals || true && sleep 5"
  }
  depends_on = [terraform_data.deployFlink]
}
resource "terraform_data" "deployExternals" {
  provisioner "local-exec" {
    command = "cd ../../k8s/scripts && ./deployExternals.sh -n ${var.namespace} -tag ${var.tag} -registry ${var.registry} && cd ../../terraform/cluster_${var.kind_or_minikube} && kubectl wait pod --all -n ${var.namespace} --for=condition=ready --timeout=600s"
  }
  depends_on = [terraform_data.undeployExternals]
}
resource "terraform_data" "undeploySolution" {
  provisioner "local-exec" {
    command = "helm uninstall --namespace ${var.namespace} fx-market-services || true && sleep 5"
  }
  depends_on = [terraform_data.deployExternals]
}
resource "terraform_data" "deploySolution" {
  provisioner "local-exec" {
    command = "cd ../../k8s/scripts && ./deploySolution.sh -n ${var.namespace} -tag ${var.tag} -registry ${var.registry} && cd ../../terraform/cluster_${var.kind_or_minikube} && kubectl wait pod --all -n ${var.namespace} --for=condition=ready --timeout=600s"
  }
  depends_on = [terraform_data.undeploySolution]
}
resource "terraform_data" "set-default-namespace" {
  provisioner "local-exec" {
    command = "kubectl config set-context --current --namespace=${var.namespace}"
  }
  depends_on = [terraform_data.deploySolution]
}
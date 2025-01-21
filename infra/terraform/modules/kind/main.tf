/*resource "null_resource" "delete_kind_instance" {
  provisioner "local-exec" {
    command = "kind delete cluster" # --name kind-2-solutions"
  }
}
locals {
    arg_host = (var.host != "") ? " -host ${var.host}" : ""
}
resource "null_resource" "create_kind_instance" {
  provisioner "local-exec" {
    command = "../../k8s/kind/createKindClusterWithRegistry.sh ${local.arg_host}" #-name kind-2-solutions
  }
}
resource "kind_cluster" "kind" {
  name           = "kind-2-solutions"
  wait_for_ready = true
  kind_config = {
    containerd_config_patches = [
      <<-TOML
            [plugins."io.containerd.grpc.v1.cri".registry.mirrors."localhost:5000"]
                endpoint = ["http://kind-registry:5000"]
            TOML
    ]
  }
  depends_on = [
    null_resource.delete_kind_instance
  ]
}
resource "kubectl_manifest" "namespace" {
  yaml_body = <<YAML
apiVersion: v1
kind: Namespace
metadata:
  name: ${var.namespace_springboot_solution}
YAML
  depends_on = [kind_cluster.kind]
}

locals {
  #command = "helm repo add gmelillo https://helm.melillo.me && helm repo update || true"
  #command = "helm ${var.registry_helm_operation} docker-registry gmelillo/docker-registry -n ${var.namespace_springboot_solution} && sleep 10"
}

resource "null_resource" "install_helm_repo_registry" {
  provisioner "local-exec" {
    command = "helm repo add gmelillo https://helm.melillo.me || true && helm repo add twuni https://helm.twun.io || true&& helm repo update || true"
  }
  depends_on = [kubectl_manifest.namespace]
}
resource "terraform_data" "install_registry" {
  provisioner "local-exec" {
    #command = "helm ${var.registry_helm_operation} docker-registry gmelillo/docker-registry -n ${var.namespace_springboot_solution} && sleep 10"
    command = "helm ${var.registry_helm_operation} docker-registry twuni/docker-registry -n ${var.namespace_springboot_solution} && sleep 10"
  }
  depends_on = [null_resource.install_helm_repo_registry]
}
*/
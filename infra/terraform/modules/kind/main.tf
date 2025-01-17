resource "null_resource" "delete_kind_instance" {
  provisioner "local-exec" {
    command = "kind delete cluster --name ${var.cluster_name}"
  }
}
resource "kind_cluster" "kind" {
  name           = var.cluster_name
  wait_for_ready = true
  depends_on = [
    null_resource.delete_kind_instance
  ]
}

resource "terraform_data" "install_registry" {
  provisioner "local-exec" {
    command = "helm install docker-registry gmelillo/docker-registry -n kube-system"
    #command = "helm repo add gmelillo https://helm.melillo.me && helm repo update && helm install docker-registry gmelillo/docker-registry -n kube-system"
  }
  depends_on = [
    kind_cluster.kind
  ]
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
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

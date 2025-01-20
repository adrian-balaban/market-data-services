resource "null_resource" "delete_kind_instance" {
  provisioner "local-exec" {
    command = "kind delete cluster --name kind_with_both_solutions"
  }
}
resource "kind_cluster" "kind" {
  name           = "kind-2-solutions"
  wait_for_ready = true
  depends_on = [
    null_resource.delete_kind_instance
  ]
}


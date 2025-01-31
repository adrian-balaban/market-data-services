locals {
  #minikube_driver    = "virtualbox"
  minikube_cpus      = 5
  minikube_memory    = 20000
  minikube_disk_size = 25000
  minikube_nodes     = 1
  minikube_cni       = "bridge"
}

resource "null_resource" "delete_instance" {
  provisioner "local-exec" {
    command = "minikube delete -p ${var.cluster_name}"
  }
}
resource "minikube_cluster" "minikube" {
  vm           = true
  #driver       = local.minikube_driver
  cluster_name = var.cluster_name
  cpus         = local.minikube_cpus
  memory       = local.minikube_memory
  disk_size    = local.minikube_disk_size
  nodes        = local.minikube_nodes
  cni          = local.minikube_cni
  #insecure-registry =  "10.0.0.0/24"
  interactive  = true
  addons = [
    "dashboard",
    "storage-provisioner",
    "volumesnapshots",
    "csi-hostpath-driver",
    "default-storageclass",
    "metrics-server",
    "logviewer",
    "registry"
  ]
  depends_on = [
    null_resource.delete_instance
  ]
}

data "kubernetes_service" "minikube_plugin_registry_svc" {
  metadata {
    name      = "registry"
    namespace = "kube-system"
  }
  depends_on = [
    minikube_cluster.minikube
  ]
}

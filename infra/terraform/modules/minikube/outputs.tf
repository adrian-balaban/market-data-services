output "host" {
  value = minikube_cluster.minikube.host
}
output "client_certificate" {
  value = minikube_cluster.minikube.client_certificate
}
output "client_key" {
  value = minikube_cluster.minikube.client_key
}
output "cluster_ca_certificate" {
  value = minikube_cluster.minikube.cluster_ca_certificate
}
output "id" {
  value = minikube_cluster.minikube.id
}

# used for knative-camel-k solution
output "registry_svc_ip" {
  value = data.kubernetes_service.minikube_plugin_registry_svc.spec.0.cluster_ip
}


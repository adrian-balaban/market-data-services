output "host" {
  value = kind_cluster.kind.endpoint
}
output "client_certificate" {
  value = kind_cluster.kind.client_certificate
}
output "client_key" {
  value = kind_cluster.kind.client_key
}
output "cluster_ca_certificate" {
  value = kind_cluster.kind.cluster_ca_certificate
}
output "id" {
  value = kind_cluster.kind.id
}

# used for springboot solution, does not work for knative-camel-k
output "registry_svc_ip" {
  value = data.kubernetes_service.registry_svc.spec.0.cluster_ip
}


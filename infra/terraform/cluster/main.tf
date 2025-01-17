// for updating docs use: terraform-docs markdown . > Readme.md

module "kind_cluster" {
  source       = "../modules/kind"
  providers    = { kind = kind, kubernetes = kubernetes.kubernetes_kind, kubectl = kubectl.kubectl_kind }
  cluster_name = var.cluster_name_kind
}
module "kind_camel-k" {
  source                         = "../modules/knative-camel-k-yaml"
  providers                      = { kubernetes = kubernetes.kubernetes_kind, kubectl = kubectl.kubectl_kind, null = null.null_kind }
  namespace_camel_k_installation = var.namespace_camel_k_installation
  depends_on                     = [module.kind_cluster]
}
/*module "kafka-confluent-minimal" {
  source = "../modules/kafka-confluent-minimal"
  providers = { kubernetes = kubernetes.kubernetes_kind, kubectl = kubectl.kubectl_kind}
  depends_on = [module.kind_cluster]
}*/

/*module "minikube_cluster" {
  source = "../modules/minikube"
  providers = { minikube = minikube, kubernetes = kubernetes.kubernetes_minikube, kubectl = kubectl.kubectl_minikube }
  cluster_name = var.cluster_name_minikube
}
module "minikube_camel-k" {
  source = "../modules/knative-camel-k-yaml"
  providers = { kubernetes = kubernetes.kubernetes_minikube,kubectl = kubectl.kubectl_minikube }
  namespace_camel_k_installation = var.namespace_camel_k_installation
  registry_svc_ip = module.minikube_cluster.registry_svc_ip #minikube_plugin_registry_svc_ip
  depends_on = [module.minikube_cluster]
}
/*module "minikube_kafka-strimzi" {
  count = var.minikube ? 1 : 0
  source = "../modules/kafka-strimzi"
  providers = { kubernetes = kubernetes.kubernetes_minikube,kubectl = kubectl.kubectl_minikube }
  depends_on = [module.minikube_cluster]
}
module "kafka-confluent-minimal" {
  source = "../modules/kafka-confluent-minimal"
  providers = { kubernetes = kubernetes.kubernetes_minikube,kubectl = kubectl.kubectl_minikube }
  depends_on = [module.minikube_camel-k]
}*/

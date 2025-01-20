// for updating docs use: terraform-docs markdown . > Readme.md

module "kind_cluster" {
  source       = "../modules/kind"
}
module "kind_camel_k_solution" {
  source                         = "../modules/knative-camel-k-yaml"
  depends_on                     = [module.kind_cluster]
}
module "kind_springboot_solution" {
  source                         = "../modules/springboot_solution"
  tag                            = "0.0.1"
  registry                       = "docker-registry.kube-system:5000"
  depends_on                     = [module.kind_camel_k_solution]
}
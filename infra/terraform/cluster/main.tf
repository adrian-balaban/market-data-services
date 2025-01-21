module "kind_springboot_solution" {
  source                         = "../modules/springboot_solution"
  tag                            = var.tag
  build = var.build
  test = var.test
  namespace = var.namespace_springboot_solution
  registry_host = var.registry_host
  registry_port = var.registry_port
}
module "kind_camel_k_solution" {
  source = "../modules/knative-camel-k-yaml"
  depends_on = [module.kind_springboot_solution]
}
/*module "kind_cluster" {
  source       = "../modules/kind"
  #host = var.host != "" ? var.host : ""
}*/
module "kind_springboot_solution" {
  source                         = "../modules/springboot_solution"
  #tag                            = "0.0.1"
  #build = "true"
  /*namespace = var.namespace_springboot_solution
  registry_host = module.kind_cluster.registry_host
  registry_port = module.kind_cluster.registry_port*/
  #depends_on                     = [module.kind_cluster]
}
/*module "kind_camel_k_solution" {
  source = "../modules/knative-camel-k-yaml"
  depends_on = [module.kind_springboot_solution]
}*/
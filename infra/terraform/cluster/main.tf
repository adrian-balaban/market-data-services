module "springboot_solution_dev" {
  source                         = "../modules/springboot_solution"
  tag                            = var.tag
  build = var.build
  publish_libs_to_maven_repo = "false"
  test = var.test
  namespace = var.namespaces_springboot_solution["dev"]
  registry_host = var.registry_host
  registry_port = var.registry_port
}
module "springboot_solution_test" {
  count = var.deploy_test ? 1 : 0
  source                         = "../modules/springboot_solution"
  tag                            = var.tag
  build = false
  publish_libs_to_maven_repo = "false"
  test = var.test
  namespace = var.namespaces_springboot_solution["test"]
  registry_host = var.registry_host
  registry_port = var.registry_port
  depends_on = [module.springboot_solution_dev]
}
module "knative" {
  source = "../modules/knative-yaml"
  depends_on = [module.springboot_solution_test]
}
module "camel_k_solution_dev" {
  count = 1
  source = "../modules/camel-k-solution"
  namespace = var.namespaces_camel_k_solution["dev"]
  depends_on = [module.knative, module.springboot_solution_dev]
}
module "camel_k_solution_test" {
  count = var.deploy_test ? 1 : 0
  source = "../modules/camel-k-solution"
  namespace = var.namespaces_camel_k_solution["test"]
  depends_on = [module.knative, module.springboot_solution_test]
}
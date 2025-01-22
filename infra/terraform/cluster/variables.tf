variable "deploy_test" {
  type    = bool
  default = false
}
variable "build" {
  type        = string
  default     = "false"
  description = "can be \"true\" or \"false\""
}
variable "publish_libs_to_maven_repo" {
  type        = string
  default     = false
  description = "can be \"true\" or \"false\""
}
variable "test" {
  type        = string
  default     = "false"
  description = "can be \"true\" or \"false\""
}
variable "tag" {
  type    = string
  default = "0.0.1"
}
# host_for_docker_registry"
variable "registry_host" {
  type    = string
  default = "localhost"
}
# port_for_docker_registry"
variable "registry_port" {
  type    = string
  default = "5001"
}

variable "environments" {
  type    = list(string)
  default = ["dev", "test"]
}
variable "namespace_maven_repository" {
  type    = string
  default = "reposiline"
}
variable "namespaces_springboot_solution" {
  type = map(string)
  default = {
    "dev"  = "fxmarket"
    "test" = "test"
  }
}
variable "namespaces_camel_k_solution" {
  type = map(string)
  default = {
    "dev"  = "camel-k-dev"
    "test" = "camel-k-test"
  }
}


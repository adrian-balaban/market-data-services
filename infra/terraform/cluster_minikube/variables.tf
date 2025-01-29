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
  default = "0.3.0"
}
variable "registry_host" {
  type    = string
  default = "192.168.192.96"
}
variable "registry_port" {
  type    = string
  default = "5001"
}
variable "environments" {
  type    = list(string)
  default = ["dev", "test"]
}
variable "namespaces_springboot_solution" {
  type = map(string)
  default = {
    "dev"  = "adrian-fxmarket"
    "test" = "adrian-test"
  }
}
variable "namespaces_camel_k_solution" {
  type = map(string)
  default = {
    "dev"  = "adrian-camel-k-dev"
    "test" = "adrian-camel-k-test"
  }
}

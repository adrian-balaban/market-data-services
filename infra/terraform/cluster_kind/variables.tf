variable "deploy_test" {
  type    = bool
  default = false
}
variable "build" {
  type        = string
  default     = "true"
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
variable "registry" {
  type    = string
  default = "192.168.192.96:5001" # localhost:5001 does not work !
}
variable "environments" {
  type    = list(string)
  default = ["dev", "test"]
}
variable "namespaces_springboot_solution" {
  type = map(string)
  default = {
    "dev"  = "fxmarket"
    "test" = "test"
  }
}
/*variable "namespaces_camel_k_solution" {
  type = map(string)
  default = {
    "dev"  = "camel-k-dev"
    "test" = "camel-k-test"
  }
}*/

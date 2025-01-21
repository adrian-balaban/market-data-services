variable "namespace" {
  type = string
  default = "fxmarket"
}
variable "build" {
  type = string
  default = true
  description = "can be \"true\" or \"false\""
}
variable "test" {
  type = string
  default = "false"
  description = "can be \"true\" or \"false\""
}
variable "tag" {
  type = string
  default = "0.0.1"
}
# host_for_docker_registry"
variable "host" {
  type = string
  default = "localhost"
}
variable "registry_port" {
  type = string
  default = "5001"
}

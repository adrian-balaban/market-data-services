variable "namespace" {
  type = string
  default = "fxmarket"
}
variable "build" {
  type = bool
  default = true
}
variable "test" {
  type = bool
  default = false
}
variable "tag" {
  type = string
  default = "0.0.1"
}
variable "registry_helm_operation" {
  type = string
  default = "install"
  description = "can be upgrade or install"
}

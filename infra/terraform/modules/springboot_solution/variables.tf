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
variable "registry" {
  type = string
  default = "docker-registry.kube-system:5001"
}


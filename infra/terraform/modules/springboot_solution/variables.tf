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
variable "registry" {
  type = string
  default = "docker.io/adriannbalaban" #localhost:5001"
}
variable "kind_or_minikube" {
  type = string
  default = "kind"
}
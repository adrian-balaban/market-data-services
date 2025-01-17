/*variable "minikube" {
  type        = bool
  default     = false
}
variable "kind" {
  type        = bool
  default     = true
}*/
variable "cluster_name_minikube" {
  type    = string
  default = "minikube"
}
variable "cluster_name_kind" {
  type    = string
  default = "kind"
}
variable "namespace_camel_k_installation" {
  type    = string
  default = "knative-eventing"
}

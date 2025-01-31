variable "namespace" {
    type = string
}
variable "helm_operation" {
    type = string
    default = "install"
    description = "can be upgrade or install"
}
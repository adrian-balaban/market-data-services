terraform {
  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "2.35.1"
    }
    null = {
      source  = "hashicorp/null"
      version = "3.2.2"
    }
    kubectl = {
      source  = "gavinbunney/kubectl"
      version = "1.18.0"
    }
    minikube = {
      source  = "scott-the-programmer/minikube"
      version = "0.4.4"
    }
    kind = {
      source  = "tehcyx/kind"
      version = "0.7.0"
    }
  }
}

provider "kind" {}
provider "null" {}
provider "kubernetes" {}
provider "kubectl" {}
provider "minikube" {}

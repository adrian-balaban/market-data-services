terraform {
  required_providers {
    kubernetes = {
      source = "hashicorp/kubernetes"
    }
    null = {
      source = "hashicorp/null"
    }
    kubectl = {
      source = "gavinbunney/kubectl"
    }
    minikube = {
      source = "scott-the-programmer/minikube"
    }
  }
}


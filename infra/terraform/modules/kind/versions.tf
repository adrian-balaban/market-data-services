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
    kind = {
      source = "tehcyx/kind"
    }
  }
}

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

provider "kubernetes" {
  host                   = module.minikube_cluster.host
  client_certificate     = module.minikube_cluster.client_certificate
  client_key             = module.minikube_cluster.client_key
  cluster_ca_certificate = module.minikube_cluster.cluster_ca_certificate
}
provider "kubectl" {
  host                   = module.minikube_cluster.host
  client_certificate     = module.minikube_cluster.client_certificate
  client_key             = module.minikube_cluster.client_key
  cluster_ca_certificate = module.minikube_cluster.cluster_ca_certificate
  load_config_file       = false
}
provider "kind" {}
provider "null" {}
provider "minikube" {}

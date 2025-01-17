# Kubernetes Deployment with terraform for Knative + Camel-K solution

1. ## Prerequisite
   1. ### Tools
      - Kubectl (tested on v1.31.3)
      - Helm (tested on v3.16.3)
      - Terraform
         - Install terraform
         - Install terraform-docs
         - Install kamel
         ```bash
         brew install terraform terraform-docs kamel 
         ```
        - Add these aliases:
         ```bash
           alias tf=terraform
           alias tfi="tf init"
           alias tfc="tf apply -auto-approve"
           alias tfd="tf destroy -auto-approve"
           alias tfr="rm -rf .terraform* terraform.tfstate*"
           alias tfx="kubectl config set-context --current --namespace=knative-eventing"
           alias tfg="kubectl get pod,deployments,ksvc"
           alias tfw="watch kubectl get pod,deployments,ksvc"
         ```
      - KNative
        - CLI, as in https://knative.dev/docs/client/install-kn
         ```bash
         brew install knative/client/kn
         ```
        - (Optional) Plugins, as in https://knative.dev/docs/client/kn-plugins/
        - Visual Studio extensions :
          - Knative from RedHat
          - Extension Pack for Apache Camel by Red Hat
          - Kubernetes by Microsoft
      - If Local Cluster 
        - Kind : install kind
        - Minikube : install Oracle Virtualbox and Minikube ( I tested only with minikube virtualbox driver)
   2. ### Cluster connection 
      - `kubectl get nodes` returns properly list of nodes.
      1. Local
         1. Minikube - the cluster is instantiated by terraform using `minikube` provider, driver: "virtualbox".
            All the settings of the used minikube cluster are in `modules/minikube/main.tf` file, here:
            ```
              locals {
                 minikube_driver    = "virtualbox"
                 minikube_cpus      = 5
                 minikube_memory    = 20000
                 minikube_disk_size = 25000
                 minikube_nodes     = 1
                 minikube_cni       = "bridge"
             }
            ```
            TODO: add the uncomments to be done on actual code.  
         2. Kind 
            ```
            cd kind
            ./createKindClusterWithRegistry.sh
            ```
      2. Remote 
         - Ensure you have `~./.kube/config` properly set
   3. ### Docker Image Registry Connection  
      1. Local 
         1. Minikube - uses minikube plugin registry
         2. Kind - uses Docker Hub personal registry
            address: "docker.io"
            organization: "adriannbalaban"
            secret: "docker-registry-secret"
            For details on how is created the secret, please check in `modules/knative-camel-k-yaml/main.tf` here below:
              ```
              resource "terraform_data" "create-secret-for-docker-registry" {
                provisioner "local-exec" {
                  command = "kubectl create secret docker-registry docker-registry-secret --docker-server=docker.io --docker-username=adriannbalaban --docker-password=dckr_pat_54LuHTnvrOLeneiZzEwxyC6zqMw --docker-email=adrian.n.balanban@gmail.com -n ${var.namespace_camel_k_installation} && kubectl get secret docker-registry-secret -n ${var.namespace_camel_k_installation}"
                }
                ...
              }
              ```
              Camel-K imposes Docker standard registries, so it is not possible to use Kind registry.
              I've tested other solutions and this the only one that works.
   4. ## Deployment
      1. Minikube: launch in 'cluster' directory these commands, to start the cluster and deploy Knative+Camel-K demo:
      ```bash
        terraform init
        terraform apply -auto-approve
      ````
      2. Kind: launch in 'cluster' directory these commands, to deploy Knative+Camel-K demo on already running Kind cluster:
      ```bash
        terraform init
        terraform apply -auto-approve
      ````
   5. Destroy the cluster:
      ```bash
          terraform destroy -auto-approve # or tfd (alias already set) 
      ````
   6. The Readme.md files for each terraform directory were created with:
        ```bash
        terraform-docs markdown . > README.md
        ```

### Some helpful commands
``` bash
tfc&&tfi&&tfc # reinitializes terrafrom and applies the changes
terraform graph | dot -Tsvg > graph.svg # creates a graph of the resources
kubectl config set-context --current --namespace=knative-eventing # or tfx (alias already set)
```
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
        - Minikube : install Oracle Virtualbox and Minikube (because I have tested only with minikube virtualbox driver)

        - Launch Kind with k8s/kind/create....sh script

   2. ### Cluster connection 
          Ensure you have `~./.kube/config` properly set
   3. ### Docker Image Registry Connection
         Are used different registries for the two solutions:
         - Springboot solution uses docker registry installed on the host and not on the cluster.
         - Knative + Camel-K solution uses Docker Hub registry.
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
              Camel-K imposes Docker standard registries, so it was not possible to use Kind registry.
              I've tested other solutions and this was the only one that worked.

      4. ### Deployment
         By default is used Kind cluster created with script k8s/kind/createKindClusterWithRegistry.sh.
         Launch in 'terraform/cluster' directory these commands, to start the cluster and deploy the 2 solutions.
         Using terraform variables some parameters can be changed, like in the example below, to skip the build:
         ```bash
            rm -rf .terraform* terraform.tfstate* && terraform init && export TF_VAR_build=false && terraform apply -auto-approve"
         ```
         Or using aliases:
         ```bash
            tfr && tfi && export TF_VAR_build=false && tfc
         ```

         Springboot solution is deployed by default in fxmarket namespace.
         Knative + Camel-K solution is deployed in knative-eventing namespace.
      
          Terraform variables that can be used to change the default behaviour: 
         | Variable name                   | Default value | TF_VAR environment variable        |
         |---------------------------------|---------------|------------------------------------|
         |namespace_springboot_solution    | fxmarket      |TF_VAR_namespace_springboot_solution|
         |build                            | false         |TF_VAR_build                        |
         |test                             | false         |TF_VAR_test                         |
         |tag                              | 0.0.1         |TF_VAR_tag                          |
         |registry_host                    | localhost     |TF_VAR_registry_host                |
         |registry_port                    | 5001          |TF_VAR_registry_port                |
         |cluster_name (to be implemented) | kind          |TF_VAR_cluster_name                 |

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
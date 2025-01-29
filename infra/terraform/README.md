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
        - go into directory cluster-kind: cd infra/terraform/cluster_kind

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
git commit -m "merge with master" .idea/gradle.xml .idea/misc.xml .idea/modules.xml fx-market-services/fx-market-connector/src/main/java/com/fx/market/fxmarketconnector/mappers/FxRateProtoMapper.java fx-market-services/fx-market-processor/build.gradle fx-market-services/fx-market-processor/src/main/java/com/fx/market/fxmarketprocessor/controller/FxMarketRealTimeRatesController.java fx-market-services/fx-market-processor/src/main/java/com/fx/market/fxmarketprocessor/service/FxRate.java -> fx-market-services/fx-market-processor/src/main/java/com/fx/market/fxmarketprocessor/models/FxRate.java fx-market-services/fx-market-processor/src/main/java/com/fx/market/fxmarketprocessor/service/FxRateSerde.java -> fx-market-services/fx-market-processor/src/main/java/com/fx/market/fxmarketprocessor/models/FxRateSerde.java fx-market-services/fx-market-processor/src/main/java/com/fx/market/fxmarketprocessor/repository/FxRatesKafkaStreamsRepository.java fx-market-services/fx-market-processor/src/main/java/com/fx/market/fxmarketprocessor/repository/GenericKafkaStreamsRepository.java fx-market-services/fx-market-processor/src/main/java/com/fx/market/fxmarketprocessor/repository/client/FxMarketProcessorClient.java fx-market-services/fx-market-processor/src/main/java/com/fx/market/fxmarketprocessor/service/FxMarketProcessorService.java fx-market-services/fx-market-processor/src/main/java/com/fx/market/fxmarketprocessor/service/FxMarketRealTimeRatesService.java fx-market-services/fx-market-processor/src/main/java/com/fx/market/fxmarketprocessor/topology/FxMarketProcessorTopologyService.java fx-market-services/fx-market-processor/src/main/java/com/fx/market/fxmarketprocessor/vendor/kafka/KafkaStreamsConfig.java fx-market-services/fx-market-processor/src/main/resources/application.yaml fx-market-services/fx-market-processor/src/test/java/com/fx/market/fxmarketprocessor/FxMarketProcessorServiceApplicationTests.java -> fx-market-services/fx-market-processor/src/test/java/com/fx/market/fxmarketprocessor/FxMarketProcessorTopologyServiceApplicationTests.java fx-market-services/libs/model-fx-proto/src/main/proto/fxMarketMessage.proto infra/argo/argoProject.yaml infra/argo/deployAllWithArgo.sh infra/argo/externals/application.yaml infra/argo/externals/env/test/externals.yaml infra/argo/solution/application.yaml infra/argo/solution/env/test/fx-market-services.yaml infra/k8s/helm/services/templates/deployment.yaml infra/k8s/helm/services/templates/service.yaml infra/k8s/helm/services/templates/statefulSet.yaml infra/k8s/helm/services/values-externals.yaml infra/k8s/helm/services/values-fxmarket.yaml infra/k8s/start-port-forwarding.sh infra/terraform/camel-k/crypto-trump/CautiousInvestorAdapterSink.java infra/terraform/camel-k/crypto-trump/CautiousInvestorService.java infra/terraform/camel-k/crypto-trump/Diagram.png infra/terraform/camel-k/crypto-trump/Predictor.java infra/terraform/camel-k/crypto-trump/SillyInvestor.java infra/terraform/camel-k/crypto-trump/market-source.yaml infra/terraform/camel-k/crypto-trump/readme.md qa/README.md qa/build.gradle qa/images/test_image.png qa/images/test_image2.png qa/images/test_image3.png qa/src/test/java/application.yml qa/src/test/java/helpers/kafka/KafkaTestConsumer.java qa/src/test/java/k8s/k6_job.yaml qa/src/test/java/model/RateResponse.java qa/src/test/java/model/RatesRequest.java qa/src/test/java/performance/DeltaCalculationTest.java qa/src/test/java/performance/README.md qa/src/test/java/performance/delete_kafka_records.sh qa/src/test/java/performance/peakLoadTest.js qa/src/test/java/stepdefinitions/fxmarket/execute/BloombergExecuteSteps.java qa/src/test/java/stepdefinitions/fxmarket/prepare/BloombergPrepareSteps.java qa/src/test/java/stepdefinitions/fxmarket/validate/ProcessorValidateSteps.java qa/src/test/java/testvisa/KafkaTestConfig.java qa/src/test/resources/application-test.properties qa/src/test/resources/features/sample-domain/KafkaTest.feature qa/src/test/resources/img.png vendors/market-data-stub/index.js vendors/market-data-stub/index_test.js
## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_aws"></a> [aws](#requirement\_aws) | ~> 4.0 |
| <a name="requirement_kind"></a> [kind](#requirement\_kind) | 0.7.0 |
| <a name="requirement_kubectl"></a> [kubectl](#requirement\_kubectl) | 1.18.0 |
| <a name="requirement_kubernetes"></a> [kubernetes](#requirement\_kubernetes) | 2.35.1 |
| <a name="requirement_minikube"></a> [minikube](#requirement\_minikube) | 0.4.4 |

## Providers

No providers.

## Modules

| Name | Source | Version |
|------|--------|---------|
| <a name="module_kind_camel-k"></a> [kind\_camel-k](#module\_kind\_camel-k) | ../modules/knative-camel-k-yaml | n/a |
| <a name="module_kind_cluster"></a> [kind\_cluster](#module\_kind\_cluster) | ../modules/kind | n/a |
| <a name="module_kind_kafka-strimzi"></a> [kind\_kafka-strimzi](#module\_kind\_kafka-strimzi) | ../modules/kafka-strimzi | n/a |

## Resources

No resources.

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_cluster_name_kind"></a> [cluster\_name\_kind](#input\_cluster\_name\_kind) | n/a | `string` | `"kind"` | no |
| <a name="input_cluster_name_minikube"></a> [cluster\_name\_minikube](#input\_cluster\_name\_minikube) | n/a | `string` | `"minikube"` | no |
| <a name="input_minikube_or_kind"></a> [minikube\_or\_kind](#input\_minikube\_or\_kind) | n/a | `bool` | `false` | no |
| <a name="input_namespace_camel_k_installation"></a> [namespace\_camel\_k\_installation](#input\_namespace\_camel\_k\_installation) | n/a | `string` | `"camel-k"` | no |
| <a name="input_namespace_kafka_strimzi_installation"></a> [namespace\_kafka\_strimzi\_installation](#input\_namespace\_kafka\_strimzi\_installation) | n/a | `string` | `"kafka"` | no |

## Outputs

No outputs.

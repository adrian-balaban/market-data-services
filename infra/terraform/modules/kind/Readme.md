## Requirements

No requirements.

## Providers

| Name | Version |
|------|---------|
| <a name="provider_kind"></a> [kind](#provider\_kind) | n/a |
| <a name="provider_kubernetes"></a> [kubernetes](#provider\_kubernetes) | n/a |
| <a name="provider_null"></a> [null](#provider\_null) | n/a |
| <a name="provider_terraform"></a> [terraform](#provider\_terraform) | n/a |

## Modules

No modules.

## Resources

| Name | Type |
|------|------|
| [kind_cluster.kind](https://registry.terraform.io/providers/tehcyx/kind/latest/docs/resources/cluster) | resource |
| [null_resource.delete_kind_instance](https://registry.terraform.io/providers/hashicorp/null/latest/docs/resources/resource) | resource |
| [terraform_data.install_registry](https://registry.terraform.io/providers/hashicorp/terraform/latest/docs/resources/data) | resource |
| [kubernetes_service.registry_svc](https://registry.terraform.io/providers/hashicorp/kubernetes/latest/docs/data-sources/service) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_cluster_name"></a> [cluster\_name](#input\_cluster\_name) | n/a | `string` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_client_certificate"></a> [client\_certificate](#output\_client\_certificate) | n/a |
| <a name="output_client_key"></a> [client\_key](#output\_client\_key) | n/a |
| <a name="output_cluster_ca_certificate"></a> [cluster\_ca\_certificate](#output\_cluster\_ca\_certificate) | n/a |
| <a name="output_host"></a> [host](#output\_host) | n/a |
| <a name="output_id"></a> [id](#output\_id) | n/a |
| <a name="output_registry_svc_ip"></a> [registry\_svc\_ip](#output\_registry\_svc\_ip) | n/a |

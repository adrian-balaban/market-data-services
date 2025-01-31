locals {
  serving_version                           = "v1.16.0"
  serving_kurier_version                    = "v1.16.0"
  eventing_version                          = "v1.16.3"
  eventing_channel_broker_in_memory_version = "v1.16.3"
  namespace_camel_k_installation            = "knative-eventing"
}
resource "terraform_data" "knserving" {
  provisioner "local-exec" {
    command = "kubectl apply -f https://github.com/knative/serving/releases/download/knative-${local.serving_version}/serving-crds.yaml && kubectl apply -f https://github.com/knative/serving/releases/download/knative-${local.serving_version}/serving-core.yaml  && kubectl wait pod --all -n knative-serving --for=condition=ready --timeout=600s"
  }
}

# Use Kourier by default as the IngressClass for Knative Serving
# Fetch the External IP address or CNAME
resource "terraform_data" "kourier" {
  provisioner "local-exec" {
    command = "kubectl apply -f https://github.com/knative/net-kourier/releases/download/knative-${local.serving_kurier_version}/kourier.yaml && kubectl patch configmap/config-network --namespace knative-serving --type merge --patch '{\"data\":{\"ingress-class\":\"kourier.ingress.networking.knative.dev\"}}' && kubectl --namespace kourier-system get service kourier && kubectl wait pod --all -n knative-serving --for=condition=ready --timeout=600s "
  }
  depends_on = [
    terraform_data.knserving
  ]
}

resource "terraform_data" "dnssslip_io" {
  provisioner "local-exec" {
    command = "kubectl apply -f https://github.com/knative/serving/releases/download/knative-${local.serving_version}/serving-default-domain.yaml && kubectl wait pod --all -n knative-serving --for=condition=ready --timeout=600s "
  }
  depends_on = [
    terraform_data.kourier
  ]
}

resource "terraform_data" "hpa_autoscaling" {
  provisioner "local-exec" {
    command = "kubectl apply -f https://github.com/knative/serving/releases/download/knative-${local.serving_version}/serving-hpa.yaml && kubectl wait pod --all -n knative-serving --for=condition=ready --timeout=600s "
  }
  depends_on = [
    terraform_data.dnssslip_io
  ]
}

resource "terraform_data" "serving_verify" {
  provisioner "local-exec" {
    command = "kubectl wait pod --all -n knative-serving --for=condition=ready --timeout=600s"
  }
  depends_on = [
    terraform_data.hpa_autoscaling
  ]
}

resource "terraform_data" "eventing" {
  provisioner "local-exec" {
    command = "kubectl apply -f https://github.com/knative/eventing/releases/download/knative-${local.eventing_version}/eventing-crds.yaml && kubectl apply -f https://github.com/knative/eventing/releases/download/knative-${local.eventing_version}/eventing-core.yaml && kubectl wait pod --all -n knative-eventing --for=condition=ready --timeout=600s"
  }
  depends_on = [
    terraform_data.serving_verify
  ]
}

# install a default Channel (messaging) layer: in-memory...
resource "terraform_data" "channel_in_memory" {
  provisioner "local-exec" {
    command = "kubectl apply -f https://github.com/knative/eventing/releases/download/knative-${local.eventing_channel_broker_in_memory_version}/in-memory-channel.yaml  && kubectl wait pod --all -n knative-eventing --for=condition=ready --timeout=600s"

  }
  depends_on = [
    terraform_data.eventing
  ]
}

# install a Broker layer MT-Channel-based
resource "terraform_data" "broker_in_memory" {
  provisioner "local-exec" {
    command = "kubectl apply -f https://github.com/knative/eventing/releases/download/knative-${local.eventing_channel_broker_in_memory_version}/mt-channel-broker.yaml && kubectl wait pod --all -n knative-eventing --for=condition=ready --timeout=600s"

  }
  depends_on = [
    terraform_data.channel_in_memory
  ]
}

resource "kubectl_manifest" "configmap_imc_channel" {
  yaml_body = <<YAML
apiVersion: v1
kind: ConfigMap
metadata:
  name: imc-channel
  namespace: knative-eventing
data:
  channel-template-spec: |
    apiVersion: messaging.knative.dev/v1
    kind: InMemoryChannel
YAML
  depends_on = [
    terraform_data.broker_in_memory
  ]
}

resource "kubectl_manifest" "configmap_config_br_defaults" {
  yaml_body = <<YAML
apiVersion: v1
kind: ConfigMap
metadata:
  name: config-br-defaults
  namespace: knative-eventing
data:
  # Configures the default for any Broker that does not specify a spec.config or Broker class. # This is the cluster-wide default broker channel.
  default-br-config: |
    clusterDefault:
      brokerClass: MTChannelBasedBroker
      apiVersion: v1
      kind: ConfigMap
      name: imc-channel
      namespace: knative-eventing
YAML
  depends_on = [
    kubectl_manifest.configmap_imc_channel
  ]
}

resource "kubectl_manifest" "configure_broker_default" {
  yaml_body = <<YAML
apiVersion: eventing.knative.dev/v1
kind: Broker
metadata:
  annotations:
    eventing.knative.dev/broker.class: MTChannelBasedBroker
  name: default
  namespace: knative-eventing
YAML
  depends_on = [
    kubectl_manifest.configmap_config_br_defaults
  ]
}

resource "terraform_data" "broker_verify" {
  provisioner "local-exec" {
    command = "kubectl wait broker --all --for=condition=ready --timeout=600s -n knative-eventing" #-n ${local.namespace_camel_k_installation}"
  }
  depends_on = [kubectl_manifest.configure_broker_default]
}

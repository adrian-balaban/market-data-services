locals {
  serving_version                           = "v1.16.0"
  serving_kurier_version                    = "v1.16.0"
  eventing_version                          = "v1.16.3"
  eventing_channel_broker_in_memory_version = "v1.16.3"
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
    command = "kubectl wait broker --all --for=condition=ready --timeout=600s -n ${var.namespace_camel_k_installation}"
  }
  depends_on = [kubectl_manifest.configure_broker_default]
}

resource "terraform_data" "camel_k" {
  provisioner "local-exec" {
    command = "helm repo add camel-k https://apache.github.io/camel-k/charts/ && helm repo update && helm install camel-k camel-k/camel-k -n ${var.namespace_camel_k_installation} --set imagePullSecrets.name=docker-registry.kube-system && kubectl config set-context --current --namespace=${var.namespace_camel_k_installation}"
  }
  depends_on = [terraform_data.broker_verify]
}

resource "terraform_data" "camel_k_verify" {
  provisioner "local-exec" {
    command = "kubectl wait pod --all --for=condition=ready --timeout=600s -n ${var.namespace_camel_k_installation}"
  }
  depends_on = [terraform_data.camel_k]
}

resource "terraform_data" "create-secret-for-docker-registry" {
  provisioner "local-exec" {
    command = "kubectl create secret docker-registry docker-registry-secret --docker-server=docker.io --docker-username=adriannbalaban --docker-password=dckr_pat_54LuHTnvrOLeneiZzEwxyC6zqMw --docker-email=adrian.n.balanban@gmail.com -n ${var.namespace_camel_k_installation} && kubectl get secret docker-registry-secret -n ${var.namespace_camel_k_installation}"
  }
  depends_on = [terraform_data.camel_k_verify]
}

resource "kubectl_manifest" "camel_k_integration_platform" {
  yaml_body  = <<YAML
apiVersion: camel.apache.org/v1
kind: IntegrationPlatform
metadata:
  labels:
    app: camel-k
  namespace: ${var.namespace_camel_k_installation}
  name: camel-k
spec:
  build:
    registry:
      #address: "${var.registry_svc_ip}:5000"
      #insecure: true
      address: "docker.io"
      organization: "adriannbalaban"
      secret: "docker-registry-secret"

YAML
  depends_on = [terraform_data.create-secret-for-docker-registry]
}

resource "null_resource" "set_default_namespace" {
  provisioner "local-exec" {
    command = "kubectl config set-context --current --namespace=${var.namespace_camel_k_installation}"
  }
  depends_on = [
    kubectl_manifest.camel_k_integration_platform
  ]
}

resource "null_resource" "kamel_run_market_source" {
  provisioner "local-exec" {
    command = "echo kamel run --dev market-source.yaml -n ${var.namespace_camel_k_installation}"
  }
  depends_on = [
    null_resource.set_default_namespace
  ]
}

resource "kubectl_manifest" "fx_market_externals_deployment" {
  yaml_body  = <<YAML
apiVersion: apps/v1
kind: Deployment
metadata:
  name: fx-market-data-stub
  namespace: ${var.namespace_camel_k_installation}
  labels:
    app: fx-market-data-stub
    release: fx-market-externals
    version: 0.0.1
spec:
  replicas:
  selector:
    matchLabels:
      app: fx-market-data-stub
      release: fx-market-externals
  template:
    metadata:
      labels:
        app: fx-market-data-stub
        release: fx-market-externals
        version: 0.0.1
    spec:
      containers:
        - name: fx-market-data-stub
          image: "docker.io/adriannbalaban/market-data-stub:0.0.1"
          imagePullPolicy:
          env:

          ports:
            - name: http
              containerPort: 3080
              protocol: TCP
          resources:
            limits:
              cpu: 1
              memory: 1024Mi
            requests:
              cpu: 500m
              memory: 512Mi
          volumeMounts:
      topologySpreadConstraints:
      - maxSkew: 6
        topologyKey: kubernetes.io/hostname
        whenUnsatisfiable: DoNotSchedule
        labelSelector:
          matchLabels:
            release: fx-market-externals
YAML
  depends_on = [null_resource.kamel_run_market_source]
}

resource "kubectl_manifest" "fx_market_externals_stub" {
  yaml_body  = <<YAML
apiVersion: v1
kind: Service
metadata:
  name: fx-market-data-stub-svc
  namespace: ${var.namespace_camel_k_installation}
  labels:
    release: fx-market-externals
spec:
  type: ClusterIP
  ports:
    - port: 3080
      targetPort: 3080
      protocol: TCP
      name: http
  selector:
    app: fx-market-data-stub
YAML
  depends_on = [kubectl_manifest.fx_market_externals_deployment]
}

resource "terraform_data" "sse_connector" {
  provisioner "local-exec" {
    command = "kamel run ../camel-k/FxMarketConnector.java  -n ${var.namespace_camel_k_installation}"
  }
  depends_on = [kubectl_manifest.fx_market_externals_stub]
}
resource "terraform_data" "eurusd_extractor" {
  provisioner "local-exec" {
    command = "kamel run ../camel-k/FxMarketExtractorEurUsd.java  -n ${var.namespace_camel_k_installation}"
  }
  depends_on = [terraform_data.sse_connector]
}
resource "terraform_data" "printer_of_events" {
  provisioner "local-exec" {
    command = "kamel run ../camel-k/FxMarketLogEurUsd.java  -n ${var.namespace_camel_k_installation}"
  }
  depends_on = [terraform_data.eurusd_extractor]
}

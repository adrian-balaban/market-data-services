resource "kubectl_manifest" "namespace" {
  yaml_body = <<YAML
apiVersion: v1
kind: Namespace
metadata:
  name: ${var.namespace}
YAML
}
resource "null_resource" "camel_k_install_helm_repo" {
  provisioner "local-exec" {
    command = "helm repo add camel-k https://apache.github.io/camel-k/charts/"
  }
  depends_on = [kubectl_manifest.namespace]
}
resource "terraform_data" "camel_k" {
  provisioner "local-exec" {
    command = "helm repo update && helm ${var.helm_operation} camel-k camel-k/camel-k -n ${var.namespace} --set imagePullSecrets.name=docker-registry.kube-system && kubectl config set-context --current --namespace=${var.namespace}"
  }
  depends_on = [null_resource.camel_k_install_helm_repo]
}

resource "terraform_data" "camel_k_verify" {
  provisioner "local-exec" {
    command = "kubectl wait pod --all --for=condition=ready --timeout=600s -n ${var.namespace}"
  }
  depends_on = [terraform_data.camel_k]
}

resource "terraform_data" "create-secret-for-docker-registry" {
  provisioner "local-exec" {
    command = "kubectl create secret docker-registry docker-registry-secret --docker-server=docker.io --docker-username=adriannbalaban --docker-password=dckr_pat_54LuHTnvrOLeneiZzEwxyC6zqMw --docker-email=adrian.n.balanban@gmail.com -n ${var.namespace} || true && kubectl get secret docker-registry-secret -n ${var.namespace}"
  }
  depends_on = [terraform_data.camel_k_verify]
}
# for minikube use:
# address: "${var.registry_svc_ip}:5000"
# insecure: true
resource "kubectl_manifest" "camel_k_integration_platform" {
  yaml_body  = <<YAML
apiVersion: camel.apache.org/v1
kind: IntegrationPlatform
metadata:
  labels:
    app: camel-k
  namespace: ${var.namespace}
  name: camel-k
spec:
  build:
    registry:
      address: "docker.io"
      organization: "adriannbalaban"
      secret: "docker-registry-secret"
YAML
  depends_on = [terraform_data.create-secret-for-docker-registry]
}

resource "null_resource" "set_default_namespace" {
  provisioner "local-exec" {
    command = "kubectl config set-context --current --namespace=${var.namespace}"
  }
  depends_on = [
    kubectl_manifest.camel_k_integration_platform
  ]
}
/*resource "null_resource" "kamel_run_market_source" {
  provisioner "local-exec" {
    command = "echo kamel run --dev market-source.yaml -n ${var.namespace}"
  }
  depends_on = [
    null_resource.set_default_namespace
  ]
}
*/
resource "terraform_data" "sse_connector_to_kafka" {
  provisioner "local-exec" {
    command = "kamel run ../camel-k/FxMarketConnectorSinkToKafka.java  -n ${var.namespace}"
  }
  depends_on = [null_resource.set_default_namespace]
}
resource "terraform_data" "sse_connector" {
  provisioner "local-exec" {
    command = "kamel run ../camel-k/FxMarketConnector.java  -n ${var.namespace}"
  }
  depends_on = [terraform_data.sse_connector_to_kafka]
}
resource "terraform_data" "eurusd_extractor" {
  provisioner "local-exec" {
    command = "kamel run ../camel-k/FxMarketExtractorEurUsd.java  -n ${var.namespace}"
  }
  depends_on = [terraform_data.sse_connector]
}
resource "terraform_data" "eurusd_output_events_eurusd" {
  provisioner "local-exec" {
    command = "kamel run ../camel-k/FxMarketOutputStream.java  -n ${var.namespace}"
  }
  depends_on = [terraform_data.eurusd_extractor]
}
resource "terraform_data" "FxMarketConnectorSinkToKafka" {
  provisioner "local-exec" {
    command = "kamel run ../camel-k/FxMarketConnectorSinkToKafka.java  -n ${var.namespace}"
  }
  depends_on = [terraform_data.eurusd_extractor]
}
resource "terraform_data" "kamel_get_integration_status" {
  provisioner "local-exec" {
    command = "kamel get -n ${var.namespace}"
  }
  depends_on = [terraform_data.eurusd_output_events_eurusd]
}


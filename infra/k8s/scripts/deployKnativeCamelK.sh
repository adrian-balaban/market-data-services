#!/usr/bin/env bash

trap "exit 1" TERM
export TOP_PID=$$
set -e # exit immediately if any command within the script returns a non-zero exit status
set -o xtrace

SEPARATOR="==================================="

NAMESPACE="camel-k"
REGISTRY_NAME=registry-1.docker.io
REPOSITORY_NAME=bitnamicharts
KNATIVE_VERSION=v1.15.2

###############################################################

kubectl get namespace ${NAMESPACE} || kubectl create namespace ${NAMESPACE}

###############################################################

echo "$SEPARATOR"
echo "DEPLOYING Knative - START"
echo "$SEPARATOR"

echo Knative Serving

kubectl apply -f https://github.com/knative/serving/releases/download/knative-v1.15.2/serving-crds.yaml
kubectl apply -f https://github.com/knative/serving/releases/download/knative-v1.15.2/serving-core.yaml

echo Install a networking layer
echo Install the Knative Kourier controller by running the command:
kubectl apply -f https://github.com/knative/net-kourier/releases/download/knative-v1.15.1/kourier.yaml

echo Configure Knative Serving to use Kourier by default by running the command:
kubectl patch configmap/config-network \
  --namespace knative-serving \
  --type merge \
  --patch '{"data":{"ingress-class":"kourier.ingress.networking.knative.dev"}}'

echo Fetch the External IP address or CNAME by running the command:
kubectl --namespace kourier-system get service kourier

kubectl wait pod --all -n knative-serving --for=condition=ready --timeout=600s

echo Configure DNS¶
echo You can configure DNS to prevent the need to run curl commands with a host header.
echo The following tabs expand to show instructions for configuring DNS. Follow the procedure for the DNS of your choice:
echo Magic DNS sslip.io
echo Knative provides a Kubernetes Job called default-domain that configures Knative Serving to use sslip.io as the default DNS suffix.
kubectl apply -f https://github.com/knative/serving/releases/download/knative-v1.15.2/serving-default-domain.yaml

echo Install the components needed to support HPA-class autoscaling by running the command:
kubectl apply -f https://github.com/knative/serving/releases/download/knative-v1.15.2/serving-hpa.yaml


echo Knative Eventing
kubectl apply -f https://github.com/knative/eventing/releases/download/knative-v1.15.2/eventing-crds.yaml
kubectl apply -f https://github.com/knative/eventing/releases/download/knative-v1.15.2/eventing-core.yaml

kubectl wait pod --all -n knative-eventing --for=condition=ready --timeout=600s


echo As showed in the console, in another terminal start minikube tunnel --profile minikube
echo The tunnel must continue to run in a terminal window any time you are using your Knative quickstart environment
echo The tunnel command is required because it allows your cluster to access Knative ingress service as a LoadBalancer from your host computer

echo Optional: Install a default Channel/messaging layer
echo Install the Kafka controller by running the following command:
kubectl apply -f https://github.com/knative-extensions/eventing-kafka-broker/releases/download/knative-v1.15.2/eventing-kafka-controller.yaml

echo Install the KafkaChannel data plane by running the following command:
#To fix
#kubectl apply -f https://github.com/knative-extensions/eventing-kafka-broker/releases/download/knative-v1.15.2/eventing-kafka-channel.yaml



echo Optional: Install a Broker layer¶

echo The following tabs expand to show instructions for installing the Broker layer. Follow the procedure for the Broker of your choice:
echo Apache Kafka Broker
echo The following commands install the Apache Kafka Broker and run event routing in a system namespace. The knative-eventing namespace is used by default.
kubectl apply -f https://github.com/knative-extensions/eventing-kafka-broker/releases/download/knative-v1.15.2/eventing-kafka-controller.yaml
echo Install the Kafka Broker data plane by running the following command:
kubectl apply -f https://github.com/knative-extensions/eventing-kafka-broker/releases/download/knative-v1.15.2/eventing-kafka-broker.yaml




echo Install optional Eventing extensions¶

echo The following tabs expand to show instructions for installing each Eventing extension.
echo Apache Kafka Sink
echo Install the Kafka controller by running the command:
kubectl apply -f https://github.com/knative-extensions/eventing-kafka-broker/releases/download/knative-v1.15.2/eventing-kafka-controller.yaml
echo Install the Kafka Sink data plane by running the command:
#To fix
#kubectl apply -f https://github.com/knative-extensions/eventing-kafka-broker/releases/download/knative-v1.15.2/eventing-kafka-sink.yaml

echo Github Source
echo A single-tenant GitHub source creates one Knative service per GitHub source.
echo A multi-tenant GitHub source only creates one Knative Service, which handles all GitHub sources in the cluster. This source does not support logging or tracing configuration.
echo     To install a single-tenant GitHub source run the command:
kubectl apply -f https://github.com/knative-extensions/eventing-github/releases/download/knative-v1.15.0/github.yaml
echo To install a multi-tenant GitHub source run the command:
echo kubectl apply -f https://github.com/knative-extensions/eventing-github/releases/download/knative-v1.15.2/mt-github.yaml

echo Apache Kafka Source
echo Install the Apache Kafka Source by running the command:
kubectl apply -f https://github.com/knative-extensions/eventing-kafka-broker/releases/download/knative-v1.15.2/eventing-kafka-source.yaml

kubectl wait pod --all -n knative-eventing --for=condition=ready --timeout=600s

echo install default in-memory broker
kn broker create default

echo "$SEPARATOR"
echo "DEPLOYING Knative - END"
echo "$SEPARATOR"

###############################################################

echo "$SEPARATOR"
echo "DEPLOYING Camel-k - START"
echo "$SEPARATOR"

helm repo add camel-k https://apache.github.io/camel-k/charts/
if [[ $? != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0
helm repo update
if [[ $? != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0
helm install camel-k camel-k/camel-k -n camel-k --set imagePullSecrets.name=docker-registry #operator.global=true
if [[ $? != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0

#echo More info on https://camel.apache.org/camel-k/next/installation/integrationplatform.html
kubectl apply -f ../helm/camel-k/integration-platform.yaml -n camel-k
#kamel install --registry docker.io --registry-auth-username adriannbalaban --registry-auth-password dckr_pat_54LuHTnvrOLeneiZzEwxyC6zqMw
echo wait until all is up and running
#kubectl get itp -n camel-k -w


echo "$SEPARATOR"
echo "DEPLOYING Camel-k - END"
echo "$SEPARATOR"


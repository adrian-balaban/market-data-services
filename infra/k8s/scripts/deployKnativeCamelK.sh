#!/usr/bin/env bash

trap "exit 1" TERM
export TOP_PID=$$

SEPARATOR="==================================="

NAMESPACE="camel-k"
REGISTRY_NAME=registry-1.docker.io
REPOSITORY_NAME=bitnamicharts
KNATIVE_VERSION=v1.15.3

###############################################################

kubectl get namespace ${NAMESPACE} || kubectl create namespace ${NAMESPACE}

###############################################################

echo "$SEPARATOR"
echo "DEPLOYING Knative - START"
echo "$SEPARATOR"

kubectl apply -f https://github.com/knative/operator/releases/download/knative-$KNATIVE_VERSION/operator.yaml
kubectl apply -f - <<EOF
apiVersion: v1
kind: Namespace
metadata:
  name: knative-serving
---
apiVersion: operator.knative.dev/v1beta1
kind: KnativeServing
metadata:
  name: knative-serving
  namespace: knative-serving
---#Kourier
apiVersion: operator.knative.dev/v1beta1
kind: KnativeServing
metadata:
  name: knative-serving
  namespace: knative-serving
spec:
  # ...
  ingress:
    kourier:
      enabled: true
  config:
    network:
      ingress-class: "kourier.ingress.networking.knative.dev"
EOF
kubectl wait KnativeServing knative-serving -n knative-serving --for=condition=Ready
kubectl apply -f https://github.com/knative/serving/releases/download/knative-$KNATIVE_VERSION/serving-default-domain.yaml
kubectl apply -f - <<EOF
apiVersion: v1
kind: Namespace
metadata:
  name: knative-eventing
---
apiVersion: operator.knative.dev/v1beta1
kind: KnativeEventing
metadata:
  name: knative-eventing
  namespace: knative-eventing
EOF
kubectl wait KnativeEventing knative-eventing -n knative-eventing --for=condition=Ready

echo Install Knative kafka broker - https://knative.dev/docs/eventing/brokers/broker-types/kafka-broker/#create-a-kafka-broker
kubectl apply --filename https://github.com/knative-extensions/eventing-kafka-broker/releases/download/knative-$KNATIVE_VERSION/eventing-kafka-controller.yaml
echo Install the Kafka Broker data plane
kubectl apply --filename https://github.com/knative-extensions/eventing-kafka-broker/releases/download/knative-$KNATIVE_VERSION/eventing-kafka-broker.yaml
echo Verify that kafka-controller, kafka-broker-receiver and kafka-broker-dispatcher are running
kubectl wait pod --all -n knative-eventing --for=condition=ready


echo Create a Knative Kafka Broker
kubectl apply -f - <<EOF
apiVersion: eventing.knative.dev/v1
kind: Broker
metadata:
  annotations:
    # case-sensitive
    eventing.knative.dev/broker.class: Kafka
    # Optional annotation to point to an externally managed kafka topic:
    # kafka.eventing.knative.dev/external.topic: <topic-name>
  name: default
  namespace: camel-k
spec:
  # Configuration specific to this broker.
  config:
    apiVersion: v1
    kind: ConfigMap
    name: kafka-broker-config
    namespace: knative-eventing
  # Optional dead letter sink, you can specify either:
  #  - deadLetterSink.ref, which is a reference to a Callable
  #  - deadLetterSink.uri, which is an absolute URI to a Callable (It can potentially be out of the Kubernetes cluster)
  #delivery:
  #  deadLetterSink:
  #    ref:
  #      apiVersion: serving.knative.dev/v1
  #      kind: Service
  #      name: dlq-service
EOF


echo This ConfigMap is installed in the Knative Eventing SYSTEM_NAMESPACE in the cluster. You can edit the global configuration depending on your needs. You can also override these settings on a per broker base, by referencing a different ConfigMap on a different namespace or with a different name on your Kafka Broker\s spec.config field.
echo The default.topic.replication.factor value must be less than or equal to the number of Kafka broker instances in your cluster. For example, if you only have one Kafka broker, the default.topic.replication.factor value should not be more than 1.
echo Knative supports the full set of topic config options that your version of Kafka supports. To set any of these, you need to add a key to the configmap with the default.topic.config. prefix. For example, to set the retention.ms value you would modify the ConfigMap to look like the following:

kubectl apply -f - <<EOF
apiVersion: v1
kind: ConfigMap
metadata:
  name: kafka-broker-config
  namespace: knative-eventing
data:
  # Number of topic partitions
  default.topic.partitions: "10"
  # Replication factor of topic messages.
  default.topic.replication.factor: "1"
  # A comma separated list of bootstrap servers. (It can be in or out the k8s cluster)
  bootstrap.servers: "my-cluster-kafka-bootstrap.kafka:9092"
  # Here is our retention.ms config
  default.topic.config.retention.ms: "3600"
EOF

echo Set default broker implementation
echo To set the Kafka broker as the default implementation for all brokers in the Knative deployment, you can apply global settings by modifying the config-br-defaults ConfigMap in the knative-eventing namespace.
echo This allows you to avoid configuring individual or per-namespace settings for each broker, such as metadata.annotations.eventing.knative.dev/broker.class or spec.config.
echo The following YAML is an example of a config-br-defaults ConfigMap using Kafka broker as the default implementation.
kubectl apply -f - <<EOF
apiVersion: v1
kind: ConfigMap
metadata:
  name: config-br-defaults
  namespace: knative-eventing
data:
  default-br-config: |
    clusterDefault:
      brokerClass: Kafka
      apiVersion: v1
      kind: ConfigMap
      name: kafka-broker-config
      namespace: knative-eventing
    namespaceDefaults:
      namespace1:
        brokerClass: Kafka
        apiVersion: v1
        kind: ConfigMap
        name: kafka-broker-config
        namespace: knative-eventing
      namespace2:
        brokerClass: Kafka
        apiVersion: v1
        kind: ConfigMap
        name: kafka-broker-config
        namespace: knative-eventing
EOF

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
kubectl delete -f ../helm/camel-k/integration-platform.yaml -n camel-k
kubectl apply -f ../helm/camel-k/integration-platform.yaml -n camel-k
echo wait until all is up and running
kubectl get itp -n camel-k -w


echo "$SEPARATOR"
echo "DEPLOYING Camel-k - END"
echo "$SEPARATOR"


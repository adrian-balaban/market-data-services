#!/usr/bin/env bash

trap "exit 1" TERM
export TOP_PID=$$
set -e # exit immediately if any command within the script returns a non-zero exit status
set -o xtrace

SEPARATOR="==================================="

NAMESPACE="kafka"

###############################################################

kubectl get namespace ${NAMESPACE} || kubectl create namespace ${NAMESPACE}

###############################################################
echo "$SEPARATOR"
echo "DEPLOYING KAFKA - START"
echo "$SEPARATOR"

echo Apply the Strimzi install files, including ClusterRoles, ClusterRoleBindings and some Custom Resource Definitions CRDs. The CRDs define the schemas used for the custom resources CRs, such as Kafka, KafkaTopic and so on you will be using to manage Kafka clusters, topics and users.
kubectl create -f 'https://strimzi.io/install/latest?namespace=kafka' -n kafka

echo The YAML files for ClusterRoles and ClusterRoleBindings downloaded from strimzi.io contain a default namespace of myproject. The query parameter namespace=kafka updates these files to use kafka instead. By specifying -n kafka when running kubectl create, the definitions and configurations without a namespace reference are also installed in the kafka namespace. If there is a mismatch between namespaces, then the Strimzi cluster operator will not have the necessary permissions to perform its operations.
echo Follow the deployment of the Strimzi cluster operator:

kubectl wait pod --all -n kafka --for=condition=ready --timeout=600s

echo You can also follow the operator\’s log:
echo kubectl logs deployment/strimzi-cluster-operator -n kafka -f

echo Create an Apache Kafka cluster
echo Create a new Kafka custom resource to get a single node Apache Kafka cluster:
echo Apply the Kafka Cluster CR file
kubectl apply -f https://strimzi.io/examples/latest/kafka/kraft/kafka-single-node.yaml -n kafka


echo "$SEPARATOR"
echo "DEPLOYING KAFKA - END"
echo "$SEPARATOR"
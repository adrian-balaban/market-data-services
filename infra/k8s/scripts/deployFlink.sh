#!/usr/bin/env bash

separator="==================================="
NAMESPACE=fxmarket
REGISTRY_NAME=registry-1.docker.io
REPOSITORY_NAME=bitnamicharts
###############################################################

kubectl get namespace ${NAMESPACE} || kubectl create namespace ${NAMESPACE}

###############################################################
echo "$separator"
echo "DEPLOYING FLINK - START"
echo "$separator"

echo https://artifacthub.io/packages/helm/bitnami/flink
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update
helm install fx-flink oci://${REGISTRY_NAME}/${REPOSITORY_NAME}/flink --version 1.3.16 --namespace ${NAMESPACE}

echo "$separator"
echo "DEPLOYING FLINK - END"
echo "$separator"
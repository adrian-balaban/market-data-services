#!/usr/bin/env bash

separator="==================================="

echo "$separator"
echo "PREREQ:"
echo " - K8s Configured cluster connection (~/.kube/config) | KIND OR MINIKUBE OR Remote Cluster"
echo " - Kubectl (e.q. v1.31) - kubectl version"
echo " - helm (e.q. v3.16.3) - helm version"
sleep 3

######################### GLOBAL VARS #########################
### PORT Policy
### 2xxx services
### 3xxx stubs/external services
### 4xxx tools,

NAMESPACE=fxmarket

kubectl get namespace ${NAMESPACE} || kubectl create namespace ${NAMESPACE}

###############################################################
echo "$separator"
echo "DEPLOYING FLINK"
echo "$separator"

REGISTRY_NAME=registry-1.docker.io
REPOSITORY_NAME=bitnamicharts

echo https://artifacthub.io/packages/helm/bitnami/flink
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update
helm install fx-flink oci://${REGISTRY_NAME}/${REPOSITORY_NAME}/flink --version 1.3.16 --namespace ${NAMESPACE}


###############################################################
echo "$separator"
echo "DEPLOYING KAFKA"
echo "$separator"

helm repo add confluentinc https://packages.confluent.io/helm
helm repo update
helm upgrade        --install confluent-operator \
                    confluentinc/confluent-for-kubernetes \
                    --namespace ${NAMESPACE}

sed -i "s/___CHANGE_ME_NAMESPACE___/${NAMESPACE}/g" helm/kafka/confluent-platform-kraft.yaml ## Set proper namespace
kubectl apply -f helm/kafka/confluent-platform-kraft.yaml
sed -i "s/${NAMESPACE}/___CHANGE_ME_NAMESPACE___/g" helm/kafka/confluent-platform-kraft.yaml ## Revert


###############################################################
echo "$separator"
echo "DEPLOYING SERVICES"
echo "$separator"


helm upgrade --install \
    --namespace fxmarket \
    --set global.tag=fxmarket-0.0.1 \
    -f ./helm/services/values-common.yaml \
    -f ./helm/services/values-fxmarket.yaml fx-market-services \
    ./helm/services


helm upgrade --install \
    --namespace fxmarket \
    --set global.tag=fxmarket-0.0.1 \
    -f ./helm/services/values-externals.yaml fx-market-externals \
    ./helm/services

###############################################################
echo "$separator"
echo "END & SUMMARY"
echo "$separator"
echo "URLS:"
echo "FLINK - jobmanager:         fx-flink-jobmanager.default.svc.cluster.local:6123"
echo "FLINK - taskmanager:        fx-flink-taskmanager.default.svc.cluster.local:6122"



kubectl config set-context --current --namespace=${NAMESPACE}


###############################################################
#### CHEATSHEET
###############################################################

#### RENDER CHART
#helm template --namespace fxmarket \
#    --set global.tag=fxmarket-0.0.1 \
#    -f ./helm/services/values-common.yaml \
#    -f ./helm/services/values-fxmarket.yaml \
#    ./helm/services
#
#helm upgrade --install \
#    --namespace ${NAMESPACE} \
#    --set global.tag=fxmarket-0.0.1 \
#    -f ./helm/services/values-common.yaml \
#    -f ./helm/services/values-fxmarket.yaml fx-market-services \
#    ./helm/services \
#    --dry-run
#
#helm upgrade --install \
#    --namespace fxmarket \
#    --set global.tag=fxmarket-0.0.1 \
#    -f ./helm/services/values-common.yaml \
#    -f ./helm/services/values-fxmarket.yaml fx-market-services \
#    ./helm/services \

#!/usr/bin/env bash

separator="==================================="
NAMESPACE=fxmarket
###############################################################

kubectl get namespace ${NAMESPACE} || kubectl create namespace ${NAMESPACE}

###############################################################
echo "$separator"
echo "DEPLOYING KAFKA - START"
echo "$separator"

helm repo add confluentinc https://packages.confluent.io/helm
helm repo update
helm upgrade        --install confluent-operator \
                    confluentinc/confluent-for-kubernetes \
                    --namespace ${NAMESPACE}

sed -i "s/___CHANGE_ME_NAMESPACE___/${NAMESPACE}/g" ../helm/kafka/confluent-platform-singlenode.yaml ## Set proper namespace
kubectl apply -f ../helm/kafka/confluent-platform-singlenode.yaml
sed -i "s/${NAMESPACE}/___CHANGE_ME_NAMESPACE___/g" ../helm/kafka/confluent-platform-singlenode.yaml ## Revert

echo "$separator"
echo "DEPLOYING KAFKA - END"
echo "$separator"
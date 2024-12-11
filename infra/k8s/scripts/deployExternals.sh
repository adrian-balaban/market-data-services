#!/usr/bin/env bash

separator="==================================="
NAMESPACE=fxmarket
###############################################################

kubectl get namespace ${NAMESPACE} || kubectl create namespace ${NAMESPACE}

###############################################################
echo "$separator"
echo "DEPLOYING EXTERNALS - START"
echo "$separator"

helm upgrade --install \
    --namespace fxmarket \
    --set global.tag=fxmarket-0.0.1 \
    -f ../helm/services/values-externals.yaml fx-market-externals \
    ../helm/services


echo "$separator"
echo "DEPLOYING EXTERNALS - END"
echo "$separator"
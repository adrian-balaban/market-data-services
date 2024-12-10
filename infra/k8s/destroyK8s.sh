#!/usr/bin/env bash

separator="==============="
#NAMESPACE=fxmarket
NAMESPACE=fxmarket
kubectl delete namespace -n ${NAMESPACE}

helm uninstall confluent-operator -n ${NAMESPACE}
helm uninstall fx-flink -n ${NAMESPACE}

helm uninstall fx-market-services -n ${NAMESPACE}
helm uninstall fx-market-externals -n ${NAMESPACE}

kubectl delete all --all -n ${NAMESPACE}


kubectl delete namespace -n ${NAMESPACE}
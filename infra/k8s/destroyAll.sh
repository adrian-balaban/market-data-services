#!/usr/bin/env bash

separator="==============="
#NAMESPACE=fxmarket
NAMESPACE=fxmarket

print_usage() {
  echo "Usage:"
  echo "-n <namespace>                <- to specify namespace"
}

# Parse command-line options
while [[ "$#" -gt 0 ]]; do
  case "$1" in
    -n) NAMESPACE="$2"; shift ;;
    *) print_usage; exit 1 ;;
  esac
  shift
done

echo "$SEPARATOR"
echo "NAMESPACE: ${NAMESPACE}" # default if not provided
echo "$SEPARATOR"
sleep 5

helm uninstall fx-flink -n ${NAMESPACE}

helm uninstall confluent-operator -n ${NAMESPACE}

helm uninstall fx-market-services -n ${NAMESPACE}

helm uninstall fx-market-externals -n ${NAMESPACE}

kubectl delete -n ${NAMESPACE} -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml

kubectl delete all --all -n ${NAMESPACE}

kubectl delete namespace -n ${NAMESPACE}
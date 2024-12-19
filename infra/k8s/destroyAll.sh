#!/usr/bin/env bash

set -e # exit immediately if any command within the script returns a non-zero exit status
set -o xtrace

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

kubectl delete all --all -n ${NAMESPACE}

kubectl delete namespace -n ${NAMESPACE}
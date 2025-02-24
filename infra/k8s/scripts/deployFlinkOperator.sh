#!/usr/bin/env bash

trap "exit 1" TERM
export TOP_PID=$$

SEPARATOR="==================================="

NAMESPACE="fxmarket" # default if not provided
FLINK_OPERATOR_NAMESPACE="flinkoperator" # default if not provided
REGISTRY_NAME=registry-1.docker.io
REPOSITORY_NAME=bitnamicharts

print_usage() {
  echo "Usage:"
  echo "-n <namespace>                <- to specify namespace - DISABLED - AS OPERATOR NEEDS TO BE INSTALLED GLOBALLY"
}

# Parse command-line options
while [[ "$#" -gt 0 ]]; do
  case "$1" in
    -n) NAMESPACE="$2"; shift ;;
    *) print_usage; exit 1 ;;
  esac
  shift
done

###############################################################

kubectl get namespace ${NAMESPACE} || kubectl create namespace ${NAMESPACE}

###############################################################
echo "$SEPARATOR"
echo "DEPLOYING FLINK OPERATOR - START"
echo "$SEPARATOR"

CERT_RESOURCE_NAME="cert-manager"
CERT_NAMESPACE="cert-manager"
if kubectl get deployment "${CERT_RESOURCE_NAME}" -n "${CERT_NAMESPACE}" >/dev/null 2>&1; then
  echo "Resource ${CERT_RESOURCE_NAME} already exists."
else
  echo "Creating resource ${CERT_RESOURCE_NAME}."
  kubectl create -f https://github.com/jetstack/cert-manager/releases/download/v1.8.2/cert-manager.yaml
fi

helm repo add flink-operator-repo https://downloads.apache.org/flink/flink-kubernetes-operator-1.10.0/
if [[ $? != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0
helm repo update
if [[ $? != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0
helm upgrade --install  flink-kubernetes-operator flink-operator-repo/flink-kubernetes-operator --namespace ${FLINK_OPERATOR_NAMESPACE} --create-namespace
if [[ $? != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0


echo "$SEPARATOR"
echo "DEPLOYING FLINK OPERATOR - END"
echo "$SEPARATOR"
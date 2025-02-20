#!/usr/bin/env bash

trap "exit 1" TERM
export TOP_PID=$$

SEPARATOR="==================================="

NAMESPACE="fxmarket" # default if not provided
REGISTRY_NAME=registry-1.docker.io
REPOSITORY_NAME=bitnamicharts

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

###############################################################

kubectl get namespace ${NAMESPACE} || kubectl create namespace ${NAMESPACE}

###############################################################
echo "$SEPARATOR"
echo "DEPLOYING FLINK - START"
echo "$SEPARATOR"

kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.17.0/cert-manager.yaml

kubectl create -f https://github.com/jetstack/cert-manager/releases/download/v1.8.2/cert-manager.yaml
helm repo add flink-operator-repo-1.9.0 https://downloads.apache.org/flink/flink-kubernetes-operator-1.9.0/
helm uninstall flink-kubernetes-operator-1.9.0 -n ${NAMESPACE}
helm install flink-kubernetes-operator-1.9.0 flink-operator-repo/flink-kubernetes-operator -f flink-values.yaml -n ${NAMESPACE} --create-namespace


#helm repo add bitnami https://charts.bitnami.com/bitnami
#if [[ $? != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0
#helm repo update
#if [[ $? != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0
#helm install fx-flink oci://${REGISTRY_NAME}/${REPOSITORY_NAME}/flink --version 1.4.3 --namespace ${NAMESPACE}
#if [[ $? != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0

echo "$SEPARATOR"
echo "DEPLOYING FLINK - END"
echo "$SEPARATOR"
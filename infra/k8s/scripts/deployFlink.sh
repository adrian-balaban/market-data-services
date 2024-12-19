#!/usr/bin/env bash

trap "exit 1" TERM
export TOP_PID=$$
set -e # exit immediately if any command within the script returns a non-zero exit status
set -o xtrace

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

helm repo add bitnami https://charts.bitnami.com/bitnami
if [[ $? != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0
helm repo update
if [[ $? != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0
helm install fx-flink oci://${REGISTRY_NAME}/${REPOSITORY_NAME}/flink --version 1.3.16 --namespace ${NAMESPACE}
if [[ $? != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0

echo "$SEPARATOR"
echo "DEPLOYING FLINK - END"
echo "$SEPARATOR"
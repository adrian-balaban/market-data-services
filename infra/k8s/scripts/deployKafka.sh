#!/usr/bin/env bash

trap "exit 1" TERM
export TOP_PID=$$
set -e # exit immediately if any command within the script returns a non-zero exit status
set -o xtrace

SEPARATOR="==================================="

NAMESPACE="fxmarket" # default if not provided

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
echo "DEPLOYING KAFKA - START"
echo "$SEPARATOR"

helm repo add confluentinc https://packages.confluent.io/helm
helm repo update
helm upgrade        --install confluent-operator \
                    confluentinc/confluent-for-kubernetes \
                    --namespace ${NAMESPACE}

if [[ $? != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0

sed -i "s/___CHANGE_ME_NAMESPACE___/${NAMESPACE}/g" ../helm/kafka/confluent-platform-singlenode.yaml ## Set proper namespace
kubectl apply -f ../helm/kafka/confluent-platform-singlenode.yaml
return_status_code=$? # Save to check later after sed revert
sed -i "s/${NAMESPACE}/___CHANGE_ME_NAMESPACE___/g" ../helm/kafka/confluent-platform-singlenode.yaml ## Revert

if [[ $return_status_code != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0

echo "$SEPARATOR"
echo "DEPLOYING KAFKA - END"
echo "$SEPARATOR"
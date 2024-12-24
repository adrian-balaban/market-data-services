#!/usr/bin/env bash

trap "exit 1" TERM
export TOP_PID=$$
set -e # exit immediately if any command within the script returns a non-zero exit status
set -o xtrace

SEPARATOR="==================================="

DOCKER_REGISTRY="docker.io" # default if not provided
NAMESPACE="fxmarket" # default if not provided
TAG='0.0.1' # default if not provided

print_usage() {
  echo "Usage:"
  echo "-n <namespace>                <- to specify namespace"
  echo "-tag <docker_tag>             <- to specify docker tag for services"
  echo "-registry <DOCKER_REGISTRY>   <- to specify docker registry"
}

# Parse command-line options
while [[ "$#" -gt 0 ]]; do
  case "$1" in
    -n) NAMESPACE="$2"; shift ;;
    -tag) TAG="$2"; shift ;;
    -registry) DOCKER_REGISTRY="$2"; shift ;;
    *) print_usage; exit 1 ;;
  esac
  shift
done
###############################################################

kubectl get namespace ${NAMESPACE} || kubectl create namespace ${NAMESPACE}

###############################################################
echo "$SEPARATOR"
echo "DEPLOYING SOLUTION - START"
echo "$SEPARATOR"

helm upgrade --install \
    --namespace ${NAMESPACE} \
    --set tag="${TAG}" \
    --set apps.fxmarketconnector.image.repository="${DOCKER_REGISTRY}/fx-market-services/fx-market-connector" \
    --set apps.fxmarketcamelconnector.image.repository="${DOCKER_REGISTRY}/fx-market-services/fx-market-camel-connector" \
    --set apps.fxmarketprocessor.image.repository="${DOCKER_REGISTRY}/fx-market-services/fx-market-processor" \
    --set apps.fxmarketcamelprocessor.image.repository="${DOCKER_REGISTRY}/fx-market-services/fx-market-camel-processor" \
    --set apps.flinkorchestrator.image.repository="${DOCKER_REGISTRY}/fx-market-services/flink-orchestrator" \
    -f ../helm/services/values-common.yaml \
    -f ../helm/services/values-fxmarket.yaml fx-market-services \
    ../helm/services

if [[ $? != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0

echo "$SEPARATOR"
echo "DEPLOYING SOLUTION - END"
echo "$SEPARATOR"
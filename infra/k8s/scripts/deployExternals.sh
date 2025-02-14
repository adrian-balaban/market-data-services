#!/usr/bin/env bash

trap "exit 1" TERM
export TOP_PID=$$

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
echo "DEPLOYING EXTERNALS - START"
echo "$SEPARATOR"

helm uninstall --namespace ${NAMESPACE} fx-market-externals
sleep 5

helm upgrade --install \
    --namespace ${NAMESPACE} \
    --set tag="${TAG}" \
    --set apps.fxmarketdatastub.image.repository="${DOCKER_REGISTRY}/fx-market-externals/market-data-stub" \
    -f ../helm/services/values-externals.yaml fx-market-externals \
    ../helm/services

if [[ $? != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0

echo "$SEPARATOR"
echo "DEPLOYING EXTERNALS - END"
echo "$SEPARATOR"
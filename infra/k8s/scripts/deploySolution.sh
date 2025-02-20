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
echo "DEPLOYING SOLUTION - START"
echo "$SEPARATOR"

helm uninstall --namespace ${NAMESPACE} fx-market-services
sleep 5

helm upgrade --install \
    --namespace ${NAMESPACE} \
    --set tag="${TAG}"\
    --set apps.fxmarketconnector.image.repository="${DOCKER_REGISTRY}/fx-market-services/fx-market-connector" \
    --set apps.fxmarketprocessor.image.repository="${DOCKER_REGISTRY}/fx-market-services/fx-market-processor" \
    --set apps.flinkorchestrator.image.repository="${DOCKER_REGISTRY}/fx-market-services/flink-orchestrator" \
    --set apps.fxredisadapter.image.repository="${DOCKER_REGISTRY}/fx-market-services/fx-market-redis-adapter" \
    -f ../helm/services/values-common.yaml \
    -f ../helm/services/values-fxmarket.yaml fx-market-services \
    ../helm/services

# TO FIX this message at deployment of the chart:
# or better deploy with Flink Oprattor as requested by Rob
# using https://karlchris.github.io/data-engineering/projects/flink-k8s/#steps-to-deploy-flink-kubernetes-operator
# WARNING: There are "resources" sections in the chart not set. Using "resourcesPreset" is not recommended for production. For production installations, please set the following values according to your workload needs:
#            - jobmanager.resources
#            - taskmanager.resources
#   +info https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/

if [[ $? != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0

#kubectl apply -f flink-deployment-websocket-connector.yaml

echo "$SEPARATOR"
echo "DEPLOYING SOLUTION - END"
echo "$SEPARATOR"
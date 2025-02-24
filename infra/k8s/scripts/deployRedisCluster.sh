#!/usr/bin/env bash

trap "exit 1" TERM
export TOP_PID=$$

SEPARATOR="==================================="

NAMESPACE="fxmarket" # default if not provided
REGISTRY_NAME=registry-1.docker.io
REPOSITORY_NAME=bitnamicharts

REDIS_DEFAULT_PASSWORD=default

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
echo "DEPLOYING REDIS - START"
echo "$SEPARATOR"

helm repo add bitnami https://charts.bitnami.com/bitnami
if [[ $? != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0
helm repo update
if [[ $? != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0

helm upgrade --install fx oci://${REGISTRY_NAME}/${REPOSITORY_NAME}/redis-cluster \
    --version 11.4.3 \
    --set "persistence.enabled=false,password=${REDIS_DEFAULT_PASSWORD},cluster.replicas=1,cluster.nodes=6,cluster.update.addNodes=true,persistentVolumeClaimRetentionPolicy.whenDeleted=Delete,volumePermissions.resourcesPreset=micro" \
    --timeout 600s \
    --namespace ${NAMESPACE} 

#REDIS_PASSWORD=$(kubectl get secret --namespace ${NAMESPACE}  fx-redis-cluster -o jsonpath="{.data.redis-password}" | base64 -d)

if [[ $? != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0

echo "$SEPARATOR"
echo "DEPLOYING REDIS - END"
echo "$SEPARATOR"
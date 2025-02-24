#!/usr/bin/env bash

set -e # exit immediately if any command within the script returns a non-zero exit status

SEPARATOR="==================================="
DOCKER_REGISTRY="docker.io" # default if not provided
TAG='0.0.1' # default if not provided


print_usage() {
  echo "Usage:"
  echo "-tag <docker_tag>             <- to specify docker tag for services"
  echo "-registry <DOCKER_REGISTRY>   <- to specify docker registry"
}

# Parse command-line options
while [[ "$#" -gt 0 ]]; do
  case "$1" in
    -tag) TAG="$2"; shift ;;
    -registry) DOCKER_REGISTRY="$2"; shift ;;
    *) print_usage; exit 1 ;;
  esac
  shift
done

echo "$SEPARATOR"
echo "BUILDING SERVICES - START"
echo "$SEPARATOR"
sleep 2

pushd ../../services/docker/fx-market-connector && ./build-image.sh -tag ${TAG} -registry ${DOCKER_REGISTRY} -profile k8s && popd
pushd ../../services/docker/fx-market-processor && ./build-image.sh -tag ${TAG} -registry ${DOCKER_REGISTRY} -profile k8s && popd
pushd ../../services/docker/flink-orchestrator && ./build-image.sh -tag ${TAG} -registry ${DOCKER_REGISTRY} -profile k8s && popd
pushd ../../services/docker/fx-market-redis-adapter && ./build-image.sh -tag ${TAG} -registry ${DOCKER_REGISTRY} -profile k8s && popd
pushd ../../services/docker/fx-market-flink-processor && ./build-image.sh -tag ${TAG} -registry ${DOCKER_REGISTRY} && popd

echo "$SEPARATOR"
echo "BUILDING SERVICES - END"
echo "$SEPARATOR"
sleep 2

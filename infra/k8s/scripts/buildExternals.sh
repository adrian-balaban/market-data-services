#!/usr/bin/env bash

set -e # exit immediately if any command within the script returns a non-zero exit status
set -o xtrace

SEPARATOR="==================================="
TEST_MODE='false' # default if not provided
DOCKER_REGISTRY="docker.io" # default if not provided
TAG='0.0.1' # default if not provided


print_usage() {
  echo "Usage:"
  echo "-test <true|false>            <- to build with test mode - default: false"
  echo "-tag <docker_tag>             <- to specify docker tag for services"
  echo "-registry <DOCKER_REGISTRY>   <- to specify docker registry"
}

# Parse command-line options
while [[ "$#" -gt 0 ]]; do
  case "$1" in
    -test) TEST_MODE="$2"; shift ;;
    -tag) TAG="$2"; shift ;;
    -registry) DOCKER_REGISTRY="$2"; shift ;;
    *) print_usage; exit 1 ;;
  esac
  shift
done


echo "$SEPARATOR"
echo "BUILDING STUBS - START"
echo "$SEPARATOR"
echo "TAG: ${TAG}" # default if not provided
echo "TEST_MODE: ${TEST_MODE}" # default if not provided
echo "DOCKER_REGISTRY: ${DOCKER_REGISTRY}" # default if not provided
echo "$SEPARATOR"
sleep 2

DOCKER_IMAGE_NAME=fx-market-externals/market-data-stub:${TAG}

pushd ../../../vendors/market-data-stub &&
  docker build --build-arg TEST_MODE_ARG=$TEST_MODE -t ${DOCKER_IMAGE_NAME} . --load &&
  docker tag ${DOCKER_IMAGE_NAME} ${DOCKER_REGISTRY}/${DOCKER_IMAGE_NAME}  &&
  docker push ${DOCKER_REGISTRY}/${DOCKER_IMAGE_NAME}
popd

echo "$SEPARATOR"
echo "BUILDING STUBS - END"
echo "$SEPARATOR"
#!/usr/bin/env bash

set -e # exit immediately if any command within the script returns a non-zero exit status

SEPARATOR="==================================="
DOCKER_REGISTRY="192.168.192.96:5001" # default if not provided
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
echo "BUILDING FLINK JAR AS SIDECAR - START"
echo "$SEPARATOR"
echo "TAG: ${TAG}" # default if not provided
echo "DOCKER_REGISTRY: ${DOCKER_REGISTRY}" # default if not provided
echo "$SEPARATOR"
sleep 2

DOCKER_IMAGE_NAME=${DOCKER_REGISTRY}/fx-market-flink-jobs/processor:${TAG}


pushd ../../../../fx-market-services
  ./gradlew fx-market-flink-processor:clean && \
  ./gradlew fx-market-flink-processor:shadowJar -Pversion=${TAG}
popd

cp -r ../../../../fx-market-services/flink-jobs/fx-market-flink-processor/build/libs/ .  # tmp dir here

docker build -t ${DOCKER_IMAGE_NAME} . --load &&
docker push ${DOCKER_IMAGE_NAME}

rm -rf ./libs # remove tmp dir here

echo "$SEPARATOR"
echo "BUILDING FLINK JAR AS SIDECAR - END"
echo "$SEPARATOR"
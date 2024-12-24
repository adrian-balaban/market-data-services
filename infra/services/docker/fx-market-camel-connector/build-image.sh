#!/usr/bin/env bash
set -e # exit immediately if any command within the script returns a non-zero exit status

DOCKER_REGISTRY="docker.io" # default if not provided
TAG='0.0.1' # default if not provided
SPRING_PROFILE='' # default if not provided

print_usage() {
  echo "Usage:"
  echo "-tag <docker_tag>             <- to specify docker tag for services"
  echo "-registry <DOCKER_REGISTRY>   <- to specify docker registry"
  echo "-profile <SPRING_PROFILE>     <- to specify spring properties profile"
}

# Parse command-line options
while [[ "$#" -gt 0 ]]; do
  case "$1" in
    -tag) TAG="$2"; shift ;;
    -registry) DOCKER_REGISTRY="$2"; shift ;;
    -profile) SPRING_PROFILE="$2"; shift ;;
    *) print_usage; exit 1 ;;
  esac
  shift
done

echo "Building OSI Image - fx-market-camel-connector - Start"

pushd ../../../../fx-market-services

./gradlew -stacktrace fx-market-camel-connector:clean && \
./gradlew -stacktrace fx-market-camel-connector:bootBuildImage -Pversion=${TAG} -Pregistry=${DOCKER_REGISTRY} -Pprofile=${SPRING_PROFILE}

popd


echo "Building OSI Image - fx-market-camel-connector - Finish"

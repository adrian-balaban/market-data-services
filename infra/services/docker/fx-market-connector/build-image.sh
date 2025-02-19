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

echo "Building OSI Image - fx-market-connector - Start"

pushd ../../../../fx-market-services

./gradlew fx-market-connector:clean
if [[ $? != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0
./gradlew fx-market-connector:bootBuildImage -Pversion=${TAG} -Pregistry=${DOCKER_REGISTRY} -Pprofile=${SPRING_PROFILE}
if [[ $? != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0

popd


echo "Building OSI Image - fx-market-connector - Finish"

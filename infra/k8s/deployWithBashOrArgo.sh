#!/usr/bin/env bash

trap "exit 1" TERM
export TOP_PID=$$

SEPARATOR="==================================="

BUILD_PHASE='true' # default if not provided
TEST_MODE='false' # default if not provided
DOCKER_REGISTRY="localhost:5001" # default if not provided
NAMESPACE="fxmarket" # default if not provided
TAG='1.0.0' # default if not provided
ENV='dev' # default if not provided
BRANCH='master' # default if not provided
WITH_BASH='true' # default if not provided

print_usage() {
  echo "Usage:"
  echo "-with_bash <true|false>       <- to specify which script to use - default: bash"
  echo "-branch <branch_name>         <- to specify branch name to build - default: master"
  echo "-env <env_name>               <- to specify env name for and take relevant manifests - default: env"
  echo "-build <true|false>           <- to build with test mode - default: true"
  echo "-test <true|false>            <- to build with test mode - default: false"
  echo "-n <namespace>                <- to specify namespace"
  echo "-tag <docker_tag>             <- to specify docker tag for services"
  echo "-registry <DOCKER_REGISTRY>   <- to specify docker registry"
}

# Parse command-line options
while [[ "$#" -gt 0 ]]; do
  case "$1" in
    -with_bash) with_bash="$2"; shift ;;
    -branch) BRANCH="$2"; shift ;;
    -env) ENV="$2"; shift ;;
    -test) TEST_MODE="$2"; shift ;;
    -build) BUILD_PHASE="$2"; shift ;;
    -n) NAMESPACE="$2"; shift ;;
    -tag) TAG="$2"; shift ;;
    -registry) DOCKER_REGISTRY="$2"; shift ;;
    *) print_usage; exit 1 ;;
  esac
  shift
done

echo "$SEPARATOR"
echo "WITH_BASH: ${WITH_BASH}"
echo "BRANCH: ${BRANCH}"
echo "ENV: ${ENV}"
echo "TAG: ${TAG}"
echo "NAMESPACE: ${NAMESPACE}"
echo "TEST_MODE: ${TEST_MODE}"
echo "BUILD_PHASE: ${BUILD_PHASE}"
echo "DOCKER_REGISTRY: ${DOCKER_REGISTRY}"
echo "$SEPARATOR"
sleep 5

if [ "$WITH_BASH" == "true" ]; then
  echo "Executing with bash script..."
  echo "$SEPARATOR"
  echo "WITH_BASH - START"
  ./deployAll.sh -build ${BUILD_PHASE} -test ${TEST_MODE} -n ${NAMESPACE} -tag ${TAG} -registry ${DOCKER_REGISTRY}
  echo "$SEPARATOR"
else
  echo "Executing with argoCD script..."
  echo "$SEPARATOR"
  echo "WITH_ARGO - START"
   pushd ../argo
      ./deployWithArgo.sh -branch ${BRANCH} -env ${ENV} -test ${TEST_MODE} -build ${BUILD_PHASE} -n ${NAMESPACE} -tag ${TAG} -registry ${DOCKER_REGISTRY}
      if [[ $? != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0
  popd
  echo "$SEPARATOR"
fi

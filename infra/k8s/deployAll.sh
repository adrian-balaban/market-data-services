#!/usr/bin/env bash

trap "exit 1" TERM
export TOP_PID=$$

SEPARATOR="==================================="

BUILD_PHASE='true' # default if not provided
TEST_MODE='false' # default if not provided
DOCKER_REGISTRY="localhost:5001" # default if not provided
NAMESPACE="fxmarket" # default if not provided
TAG='0.1.0' # default if not provided

print_usage() {
  echo "Usage:"
  echo "-build <true|false>           <- to build with test mode - default: true"
  echo "-test <true|false>            <- to build with test mode - default: false"
  echo "-n <namespace>                <- to specify namespace"
  echo "-tag <docker_tag>             <- to specify docker tag for services"
  echo "-registry <DOCKER_REGISTRY>   <- to specify docker registry"
}

is_pod_running() {
  local pod_name=$1
  local status=$(kubectl get pod "$pod_name" -n $NAMESPACE -o jsonpath="{.status.phase}")
  local ready=$(kubectl get pod "$pod_name" -n $NAMESPACE -o jsonpath="{.status.containerStatuses[0].ready}")
  local reason=$(kubectl get pod "$pod_name" -n $NAMESPACE -o jsonpath="{.status.containerStatuses[0].state.waiting.reason}")

  ## Fail fast; add possible errors and kill process to stop waiting
  if [[ $reason == "ImagePullBackOff" ]];
    then echo "ImagePullBackOff detected.
                Please investigate why image is not present in registry.
                Verify if image has been build and proper image TAG provided!" && kill -s TERM $TOP_PID
  fi

  if [[ $ready == "true" && $status == "Running" ]];
    then return 0
    else return 1
  fi
}

wait_for_pod() {
  local pod_name=$(kubectl get pods --sort-by=.metadata.creationTimestamp -n=$NAMESPACE -o jsonpath='{.items[*].metadata.name}' | tr ' ' '\n' | grep $1 | head -n 1)
  if [ -z "${pod_name// }" ]; ## check if blank string
    then echo "Waiting 10 seconds for pod $1 to appear ..."
    sleep 10
    wait_for_pod "$1"
    return 0 # once recursive method finished don't go further
  fi

  echo "Waiting for pod $pod_name in namespace $NAMESPACE to be running..."
  while ! is_pod_running "$pod_name" "$NAMESPACE"; do
    echo "Waiting for pod $pod_name in namespace $NAMESPACE to be running... is not yet running ... Checking again in 10 seconds..."
    sleep 10
  done

  echo "Waiting for pod $pod_name in namespace $NAMESPACE to be running... is now running! Continue ..."
}

# Parse command-line options
while [[ "$#" -gt 0 ]]; do
  case "$1" in
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
echo "TAG: ${TAG}" # default if not provided
echo "NAMESPACE: ${NAMESPACE}" # default if not provided
echo "TEST_MODE: ${TEST_MODE}" # default if not provided
echo "BUILD_PHASE: ${BUILD_PHASE}" # default if not provided
echo "DOCKER_REGISTRY: ${DOCKER_REGISTRY}" # default if not provided
echo "$SEPARATOR"
sleep 5

# Check if BUILD_PHASE is true
if [ "$BUILD_PHASE" == "true" ]; then
  echo "Executing build phase..."
  echo "$SEPARATOR"
  echo "BUILD PHASE - START"
  echo "$SEPARATOR"
  pushd ./scripts
    ./buildExternals.sh -test ${TEST_MODE} -tag ${TAG} -registry ${DOCKER_REGISTRY}
    if [[ $? != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0

    ./buildSolution.sh -tag ${TAG} -registry ${DOCKER_REGISTRY}
    if [[ $? != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0
  popd
  echo "$SEPARATOR"
  echo "BUILD PHASE - END"
  echo "$SEPARATOR"
else
  echo "$SEPARATOR"
  echo "BUILD PHASE - SKIPPING"
  echo "$SEPARATOR"
fi


sleep 5

echo "$SEPARATOR"
echo "DEPLOY PHASE - START"
echo "$SEPARATOR"

### Deploy firstly all independent components
pushd ./scripts

  ./deployKafka.sh -n ${NAMESPACE}
  if [[ $? != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0

  ./deployFlink.sh -n ${NAMESPACE}
  if [[ $? != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0

  ./deployRedisCluster.sh -n ${NAMESPACE}
  if [[ $? != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0

  ./deployExternals.sh -tag ${TAG} -registry ${DOCKER_REGISTRY} -n ${NAMESPACE}
  if [[ $? != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0

popd

sleep 5

wait_for_pod "flink-jobmanager"
wait_for_pod "zookeeper-0"
wait_for_pod "kafka-0"
#wait_for_pod "fx-redis-cluster-0"
wait_for_pod "fx-market-data-stub"

pushd ./scripts && ./deploySolution.sh -tag ${TAG} -registry ${DOCKER_REGISTRY} -n ${NAMESPACE} && popd

wait_for_pod "fx-market-connector"
wait_for_pod "flink-orchestrator"
wait_for_pod "fx-market-processor-set-0"
wait_for_pod "fx-market-processor-set-1"
wait_for_pod "fx-market-processor-set-2"

echo "$SEPARATOR"
echo "DEPLOY PHASE - END"
echo "$SEPARATOR"

sleep 120

kubectl config set-context --current --namespace=${NAMESPACE}

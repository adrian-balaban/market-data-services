#!/usr/bin/env bash

separator="==================================="
NAMESPACE=fxmarket


is_pod_running() {
  local pod_name=$1
  local status=$(kubectl get pod "$pod_name" -n "$NAMESPACE" -o jsonpath="{.status.phase}")

  if [ "$status" == "Running" ];
    then return 0
    else return 1
  fi
}


# Main function to wait for the pod to be running
wait_for_pod() {
  local pod_name=$1
  local namespace=$NAMESPACE

  echo "Waiting for pod $pod_name in namespace $namespace to be running..."
  while ! is_pod_running "$pod_name" "$namespace"; do
    echo "Pod $pod_name is not yet running. Checking again in 15 seconds..."
    sleep 15
  done

  echo "Pod $pod_name is now running! Continue"
}
wait_for_pod "kafka-0"

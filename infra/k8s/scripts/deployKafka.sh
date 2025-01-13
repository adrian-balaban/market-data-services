#!/usr/bin/env bash

trap "exit 1" TERM
export TOP_PID=$$

SEPARATOR="==================================="

NAMESPACE="fxmarket" # default if not provided

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
echo "SEARCHING FOR KAFKA NODEPORT - START"
echo "$SEPARATOR"

NEW_NODE_PORT=30000;
current_highest_node_port=$(kubectl get services --all-namespaces | grep NodePort | awk '{print $6}' | awk -F'[:/]' '{print $2}' | sort -n | tail -n 1)
# Check if the current_port is empty
if [ -z "$current_highest_node_port" ]; then
  echo "Setting NodePort to $NEW_NODE_PORT"
else
  # Increment the port by 1
  NEW_NODE_PORT=$((current_highest_node_port + 1))
  echo "Setting NodePort to $NEW_NODE_PORT"
fi

echo "$SEPARATOR"
echo "SEARCHING FOR KAFKA NODEPORT - END. NEW KAFKA NODE PORT: $NEW_NODE_PORT"
echo "$SEPARATOR"
###############################################################
echo "$SEPARATOR"
echo "DEPLOYING KAFKA - START"
echo "$SEPARATOR"

helm repo add confluentinc https://packages.confluent.io/helm
helm repo update
helm upgrade        --install confluent-operator \
                    confluentinc/confluent-for-kubernetes \
                    --namespace ${NAMESPACE}

if [[ $? != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0

sed -i "s/___CHANGE_ME_NAMESPACE___/${NAMESPACE}/g" ../helm/kafka/confluent-platform-singlenode-minimal.yaml ## Set proper namespace
sed -i "s/___CHANGE_ME_KAFKA_NODEPORT___/${NEW_NODE_PORT}/g" ../helm/kafka/confluent-platform-singlenode-minimal.yaml ## Set proper namespace
kubectl apply -f ../helm/kafka/confluent-platform-singlenode-minimal.yaml
return_status_code=$? # Save to check later after sed revert
sed -i "s/${NAMESPACE}/___CHANGE_ME_NAMESPACE___/g" ../helm/kafka/confluent-platform-singlenode-minimal.yaml ## Revert
sed -i "s/${NEW_NODE_PORT}/___CHANGE_ME_KAFKA_NODEPORT___/g" ../helm/kafka/confluent-platform-singlenode-minimal.yaml ## Revert

if [[ $return_status_code != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0

echo "$SEPARATOR"
echo "DEPLOYING KAFKA - END"
echo "$SEPARATOR"
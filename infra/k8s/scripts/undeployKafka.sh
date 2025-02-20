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

set -v

echo "$SEPARATOR"
echo "UNDEPLOYING KAFKA - START"
echo "$SEPARATOR"

sed -i "s/___CHANGE_ME_NAMESPACE___/${NAMESPACE}/g" ../helm/kafka/confluent-platform-singlenode-minimal.yaml ## Set proper namespace
kubectl delete -f ../helm/kafka/confluent-platform-singlenode-minimal.yaml
sed -i "s/${NAMESPACE}/___CHANGE_ME_NAMESPACE___/g" ../helm/kafka/confluent-platform-singlenode-minimal.yaml ## Revert
helm uninstall confluent-operator --namespace ${NAMESPACE}

echo "$SEPARATOR"
echo "UNDEPLOYING KAFKA - END"
echo "$SEPARATOR"
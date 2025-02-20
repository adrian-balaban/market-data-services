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

helm uninstall confluent-operator --namespace ${NAMESPACE}
sleep 5

echo "$SEPARATOR"
echo "UNDEPLOYING KAFKA - END"
echo "$SEPARATOR"
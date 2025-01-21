#!/usr/bin/env bash

NAMESPACE="fxmarket" # default if not provided

# Parse command-line options
while [[ "$#" -gt 0 ]]; do
  case "$1" in
    -n) NAMESPACE="$2"; shift ;;
    *) print_usage; exit 1 ;;
  esac
  shift
done

#SERVICES
# registry
kubectl port-forward -n ${NAMESPACE} svc/docker-registry 5001:5000 &

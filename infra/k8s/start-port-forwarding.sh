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

kubectl port-forward -n ${NAMESPACE} svc/kafka 9092:9092 &
kubectl port-forward -n ${NAMESPACE} svc/controlcenter 9021:9021 &
kubectl port-forward -n ${NAMESPACE} svc/fx-flink-jobmanager 8081:8081 &
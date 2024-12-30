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

#TOOLS
#kubectl port-forward -n ${NAMESPACE} svc/kafka 9092:9092 &
kubectl port-forward -n ${NAMESPACE} svc/controlcenter 9021:9021 &
kubectl port-forward -n ${NAMESPACE} svc/fx-flink-jobmanager 8081:8081 &

#STUBS
kubectl port-forward -n ${NAMESPACE} svc/fx-market-data-stub-svc 3080:8081 &

#SERVICES (Add only if needed i.e. by e2e tests)

KAFKA_PORT=`kubectl get service/kafka-nodeport-service -n $NAMESPACE -o jsonpath="{..ports[0].nodePort}"`
echo "$KAFKA_PORT"
kubectl port-forward -n ${NAMESPACE} service/kafka-nodeport-service $KAFKA_PORT:9092 &
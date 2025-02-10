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

###############################################################ARGO CD
kubectl port-forward -n ${NAMESPACE} svc/argocd-server 38080:443 &
PASSWD=$(kubectl get secrets/argocd-initial-admin-secret -n ${NAMESPACE}  --template={{.data.password}} | base64 -d)
echo "ARGOCD USERNAME: admin"
echo "ARGOCD PASSWORD: ${PASSWD}"
###############################################################

#TOOLS
kubectl port-forward -n ${NAMESPACE} svc/kafka 9093:9092 &
kubectl port-forward -n ${NAMESPACE} svc/controlcenter 9021:9021 &
kubectl port-forward -n ${NAMESPACE} svc/fx-flink-jobmanager 8081:8081 &
kubectl port-forward -n ${NAMESPACE} svc/fx-redis-cluster 6379:6379 &

#STUBS
kubectl port-forward -n ${NAMESPACE} svc/fx-market-data-stub-svc 3080:3080 &

#SERVICES (Add only if needed i.e. by e2e tests)
kubectl port-forward -n ${NAMESPACE} svc/fx-market-processor-svc 4080:8080 &
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
kubectl port-forward -n ${NAMESPACE} svc/kafka 9094:9092 &
kubectl port-forward -n ${NAMESPACE} svc/fx-flink-jobmanager 8082:8081 &

#STUBS
kubectl port-forward -n ${NAMESPACE} svc/fx-market-data-stub-svc 3082:3080 &
kubectl port-forward -n ${NAMESPACE} svc/fx-market-data-stub-ws-svc 3083:3081 &

#SERVICES (Add only if needed i.e. by e2e tests)
kubectl port-forward -n ${NAMESPACE} svc/fx-market-processor-svc 4081:8080 &

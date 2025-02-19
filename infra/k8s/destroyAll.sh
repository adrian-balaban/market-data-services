#!/usr/bin/env bash

set -v

separator="==============="
#NAMESPACE=fxmarket
NAMESPACE=fxmarket

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

echo "$SEPARATOR"
echo "NAMESPACE: ${NAMESPACE}" # default if not provided
echo "$SEPARATOR"
sleep 5

helm uninstall fx-flink -n ${NAMESPACE}

helm uninstall fx -n ${NAMESPACE} # Redis

helm uninstall confluent-operator -n ${NAMESPACE}

helm uninstall fx-market-services -n ${NAMESPACE}

helm uninstall fx-market-externals -n ${NAMESPACE}

./scripts/undeployKafka.sh -n ${NAMESPACE}

kubectl -n ${NAMESPACE} delete statefulset controlcenter
sleep 5
kubectl -n ${NAMESPACE} delete statefulset kafka
sleep 5
kubectl -n ${NAMESPACE} delete statefulset zookeeper
sleep 5

kubectl -n ${NAMESPACE} delete pod zookeeper-0 --force
sleep 10
kubectl -n ${NAMESPACE} delete pod kafka-0 --force
sleep 5

kubectl get pods -n ${NAMESPACE} | grep argo && kubectl delete -n ${NAMESPACE} -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml

#Delete annoying zookeeper
kubectl patch zookeeper.platform.confluent.io/zookeeper -p '{"metadata":{"finalizers":[]}}' --type=merge -n ${NAMESPACE}
sleep 5
kubectl patch controlcenter.platform.confluent.io/controlcenter -p '{"metadata":{"finalizers":[]}}' --type=merge -n ${NAMESPACE}
sleep 5
kubectl patch kafka.platform.confluent.io/kafka -p '{"metadata":{"finalizers":[]}}' --type=merge -n ${NAMESPACE}
sleep 5

kubectl -n ${NAMESPACE} delete zookeeper.platform.confluent.io/zookeeper --force
sleep 5
kubectl -n ${NAMESPACE} delete controlcenter.platform.confluent.io/controlcenter --force
sleep 5
kubectl -n ${NAMESPACE} delete kafka.platform.confluent.io/kafka --force
sleep 5

# DELETE PV AND PVC
kubectl delete pvc --all -n ${NAMESPACE}
kubectl get pv | grep ${NAMESPACE} | awk {'print $1'} | xargs --no-run-if-empty timeout 5 kubectl delete pv
kubectl get pv | grep ${NAMESPACE} | grep Terminating | awk {'print $1'} | xargs -I{} kubectl patch pv {} -p '{"metadata":{"finalizers":[]}}' --type=merge
kubectl get pv | grep ${NAMESPACE} | awk {'print $1'} | xargs --no-run-if-empty timeout 5 kubectl delete pv

kubectl delete all --all -n ${NAMESPACE}

echo "DELETING NAMESPACE"
timeout 15 kubectl delete namespace ${NAMESPACE} --force

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

pushd ./scripts
./undeployKafka.sh -n ${NAMESPACE}
popd

set -v

RES='zookeeper'
kubectl -n ${NAMESPACE} delete statefulset $RES
kubectl wait statefulset $RES --for=condition=delete --timeout=600s -n ${NAMESPACE}

RES='controlcenter'
kubectl -n ${NAMESPACE} delete statefulset $RES
kubectl wait statefulset $RES --for=condition=delete --timeout=600s -n ${NAMESPACE}

RES='kafka'
kubectl -n ${NAMESPACE} delete statefulset $RES
kubectl wait statefulset $RES --for=condition=delete --timeout=600s -n ${NAMESPACE}

RES='zookeeper'
kubectl -n ${NAMESPACE} delete pod $RES
kubectl wait pod $RES --for=condition=delete --timeout=600s -n ${NAMESPACE}

RES='controlcenter'
kubectl -n ${NAMESPACE} delete pod $RES
kubectl wait pod $RES --for=condition=delete --timeout=600s -n ${NAMESPACE}

RES='kafka'
kubectl -n ${NAMESPACE} delete pod $RES
kubectl wait pod $RES --for=condition=delete --timeout=600s -n ${NAMESPACE}

kubectl get pods -n ${NAMESPACE} | grep argo && kubectl delete -n ${NAMESPACE} -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml

#Delete annoying zookeeper
kubectl patch zookeeper.platform.confluent.io/zookeeper -p '{"metadata":{"finalizers":[]}}' --type=merge -n ${NAMESPACE}
kubectl patch controlcenter.platform.confluent.io/controlcenter -p '{"metadata":{"finalizers":[]}}' --type=merge -n ${NAMESPACE}
kubectl patch kafka.platform.confluent.io/kafka -p '{"metadata":{"finalizers":[]}}' --type=merge -n ${NAMESPACE}
sleep 5

RES='zookeeper.platform.confluent.io/zookeeper'
kubectl -n ${NAMESPACE} delete $RES --force
kubectl wait crd $RES --for=condition=delete --timeout=600s -n ${NAMESPACE}

RES='controlcenter.platform.confluent.io/controlcenter'
kubectl -n ${NAMESPACE} delete $RES --force
kubectl wait crd $RES --for=condition=delete --timeout=600s -n ${NAMESPACE}

RES='kafka.platform.confluent.io/kafka'
kubectl -n ${NAMESPACE} delete $RES --force
kubectl wait crd $RES --for=condition=delete --timeout=600s -n ${NAMESPACE}

# DELETE PV AND PVC
kubectl delete pvc --all -n ${NAMESPACE}
kubectl get pv | grep ${NAMESPACE} | awk {'print $1'} | xargs --no-run-if-empty timeout 5 kubectl delete pv
kubectl get pv | grep ${NAMESPACE} | grep Terminating | awk {'print $1'} | xargs -I{} kubectl patch pv {} -p '{"metadata":{"finalizers":[]}}' --type=merge
kubectl get pv | grep ${NAMESPACE} | awk {'print $1'} | xargs --no-run-if-empty timeout 5 kubectl delete pv

kubectl delete all --all -n ${NAMESPACE}

echo "DELETING NAMESPACE"
timeout 15 kubectl delete namespace ${NAMESPACE} --force

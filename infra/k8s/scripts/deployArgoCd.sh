#!/usr/bin/env bash

trap "exit 1" TERM
export TOP_PID=$$

SEPARATOR="==================================="

NAMESPACE="fxmarket" # default if not provided
# Parse command-line options
while [[ "$#" -gt 0 ]]; do
  case "$1" in
    -n) NAMESPACE="$2"; shift ;;
    *) print_usage; exit 1 ;;
  esac
  shift
done

###############################################################
echo "$SEPARATOR"
echo "DEPLOYING ARGOCD - START"
echo "$SEPARATOR"
###############################################################
kubectl get namespace ${NAMESPACE} || kubectl create namespace ${NAMESPACE}
kubectl apply -n ${NAMESPACE} -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
if [[ $? != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0
###############################################################
echo "$SEPARATOR"
echo "DEPLOYING ARGOCD - END"
echo "$SEPARATOR"
###############################################################
PASSWD=$(kubectl get secrets/argocd-initial-admin-secret -n ${NAMESPACE}  --template={{.data.password}} | base64 -d)
###############################################################
echo "$SEPARATOR"
echo "ARGOCD USERNAME: admin"
echo "ARGOCD PASSWORD: ${PASSWD}"
echo "$SEPARATOR"
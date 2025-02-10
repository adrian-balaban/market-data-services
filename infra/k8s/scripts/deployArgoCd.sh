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

#kubectl apply -n -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml ## Without kustomization only argocd namespace

sed -i "s/___CHANGE_ME_NAMESPACE___/${NAMESPACE}/g" ./resources//kustomize/argocd/kustomization.yaml ## Set proper namespace
kubectl apply -k ./resources/kustomize/argocd/
return_status_code=$? # Save to check later after sed revert
sed -i "s/${NAMESPACE}/___CHANGE_ME_NAMESPACE___/g" ./resources/kustomize/argocd/kustomization.yaml ## Revert
if [[ $return_status_code != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0

###############################################################
echo "$SEPARATOR"
echo "DEPLOYING ARGOCD - END"
echo "$SEPARATOR"
sleep 5 ## Wait a few seconds until secret is created
###############################################################
PASSWD=$(kubectl get secrets/argocd-initial-admin-secret -n ${NAMESPACE}  --template={{.data.password}} | base64 -d)
###############################################################
echo "$SEPARATOR"
echo "ARGOCD USERNAME: admin"
echo "ARGOCD PASSWORD: ${PASSWD}"
echo "$SEPARATOR"
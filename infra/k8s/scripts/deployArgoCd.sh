#!/usr/bin/env bash

trap "exit 1" TERM
export TOP_PID=$$

SEPARATOR="==================================="

###############################################################
echo "$SEPARATOR"
echo "DEPLOYING ARGOCD - START"
echo "$SEPARATOR"

kubectl create namespace argocd
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
if [[ $? != 0 ]]; then echo "ERROR | STOP" && exit; fi # check return value, exit if not 0

echo "$SEPARATOR"
echo "DEPLOYING ARGOCD - END"
echo "$SEPARATOR"

PASSWD=$(kubectl get secrets/argocd-initial-admin-secret -n argocd --template={{.data.password}} | base64 -d)

echo "$SEPARATOR"
echo "ARGOCD USERNAME: admin"
echo "ARGOCD PASSWORD: ${PASSWD}"
echo "$SEPARATOR"
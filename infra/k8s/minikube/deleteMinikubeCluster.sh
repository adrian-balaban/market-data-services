#!/usr/bin/env bash

set -e # exit immediately if any command within the script returns a non-zero exit status
set -o xtrace

minikube delete --all=true
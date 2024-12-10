#!/usr/bin/env bash

apt-get update && apt-get install -y jq

NAMESPACE=${1:-develop}

# get random node IP
NODES=( $(kubectl get node -o wide | grep 'Ready \|vmfraluxtpid' | awk '{print $6}') )
NODE=${NODES[ $(( RANDOM % ${#NODES[@]} )) ]}

# get services ports

# set proper envs

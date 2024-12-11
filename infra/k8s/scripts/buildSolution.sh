#!/usr/bin/env bash

docker_registry="localhost:5001"

separator="==================================="

echo "$separator"
echo "BUILDING SERVICES - START"
echo "$separator"
sleep 2

pushd ../../services/docker/fx-market-connector && ./build-image.sh && popd
pushd ../../services/docker/fx-market-processor && ./build-image.sh && popd
pushd ../../services/docker/flink-orchestrator && ./build-image.sh && popd

echo "$separator"
echo "BUILDING SERVICES - END"
echo "$separator"
sleep 2

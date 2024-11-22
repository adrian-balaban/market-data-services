#!/usr/bin/env bash


separator="==================================="

echo "$separator"
echo "BUILDING SERVICES"
echo "$separator"

pushd ../services/docker/fx-market-connector && ./build-image.sh && popd


echo "$separator"
echo "BUILDING STUBS"
echo "$separator"

pushd ../../vendors/market-data-stub && podman build -t market-data-stub . --load && popd


echo "$separator"
echo "RUNNING COMPOSE WITH PODMAN"
echo "$separator"

podman compose up -d
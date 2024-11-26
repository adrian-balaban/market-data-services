#!/usr/bin/env bash


separator="==================================="

echo "$separator"
echo "BUILDING SERVICES"
echo "$separator"

pushd ../../ && gradle build && popd


echo "$separator"
echo "RUNNING COMPOSE WITH PODMAN"
echo "$separator"

podman compose up -d
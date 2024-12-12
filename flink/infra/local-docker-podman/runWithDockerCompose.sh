#!/usr/bin/env bash


separator="==================================="

echo "$separator"
echo "BUILDING JAR"
echo "$separator"

pushd ../../ && gradle build && popd


echo "$separator"
echo "RUNNING COMPOSE WITH DOCKER-COMPOSE"
echo "$separator"

docker-compose compose up -d
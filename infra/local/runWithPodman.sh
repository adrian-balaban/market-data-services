#!/usr/bin/env bash

separator="==================================="

test_mode_flag='false'

print_usage() {
  printf "Usage: ..."
}

while getopts 't' flag; do
  case "${flag}" in
    t) test_mode_flag=true ;;
    *) print_usage
       exit 1 ;;
  esac
done

echo "$separator"
echo "BUILDING SERVICES"
echo "$separator"
sleep 2

pushd ../services/docker/fx-market-connector && ./build-image.sh && popd
pushd ../services/docker/fx-market-processor && ./build-image.sh && popd
pushd ../services/docker/flink-orchestrator && ./build-image.sh && popd

echo "$separator"
echo "BUILDING STUBS"
echo "$separator"
echo "TEST_MODE:$test_mode_flag"
echo "$separator"
sleep 2

pushd ../../vendors/market-data-stub && podman build --build-arg TEST_MODE_ARG=$test_mode_flag -t fx-market/market-data-stub:0.0.1 . --load && popd


echo "$separator"
echo "RUNNING COMPOSE WITH PODMAN"
echo "$separator"
sleep 2

podman compose up -d
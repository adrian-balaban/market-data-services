#!/usr/bin/env bash

test_mode_flag='false'

docker_registry="localhost:5001"

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

separator="==================================="


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

pushd ../../vendors/market-data-stub &&
  docker build --build-arg TEST_MODE_ARG=$test_mode_flag -t ${docker_registry}/fx-market/market-data-stub:0.0.1 . --load &&
  docker push ${docker_registry}/fx-market/market-data-stub:0.0.1 --tls-verify=false &&
popd

#./runWithK8s.sh
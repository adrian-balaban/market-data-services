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

echo "$separator"
echo "BUILDING STUBS - START"
echo "$separator"
echo "TEST_MODE:$test_mode_flag"
echo "$separator"
sleep 2

tag='0.1.0'

pushd ../../../vendors/market-data-stub &&
  docker build --build-arg TEST_MODE_ARG=$test_mode_flag -t ${docker_registry}/fx-market/market-data-stub:${tag} . --load &&
  docker push ${docker_registry}/fx-market/market-data-stub:${tag} --tls-verify=false &&
popd

echo "$separator"
echo "BUILDING STUBS - END"
echo "$separator"
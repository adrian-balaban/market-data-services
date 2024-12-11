#!/usr/bin/env bash
set -e

echo "Building OSI Image - Start"
pushd ../../../../fx-market-services && ./gradlew fx-market-connector:bootBuildImage && popd


#docker run docker.io/library/fx-market-connector:latest

echo "Building OSI Image - Finish"

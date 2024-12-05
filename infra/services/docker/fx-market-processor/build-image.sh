#!/usr/bin/env bash
set -e

echo "Building OSI Image - fx-market-processor - Start"
pushd ../../../../fx-market-services && ./gradlew fx-market-processor:bootBuildImage && popd


#docker run docker.io/library/fx-market-processor:latest

echo "Building OSI Image - fx-market-processor - Finish"

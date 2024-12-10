#!/usr/bin/env bash
set -e

echo "Building OSI Image - flink-orchestrator - Start"
pushd ../../../../fx-market-services && ./gradlew flink-orchestrator:bootBuildImage && popd


#docker run docker.io/library/flink-orchestrator:latest

echo "Building OSI Image - flink-orchestrator - Finish"

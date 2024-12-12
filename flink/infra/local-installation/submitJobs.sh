#!/usr/bin/env bash
set -x

separator="==================================="

echo "$separator"
echo "BUILDING JAR"
echo "$separator"

pushd ../../ && gradle build && popd


echo "$separator"
echo "RUNNING JOBS"
echo "$separator"

start-cluster.sh

# echo run the flink job BloombergForexReadings in background
flink run -c io.github.streamingwithflink.chapter1.BloombergForexReadings ../../build/libs/examples-java-1.0.jar &

# echo run the flink job AverageSensorReadings  in background
flink run -c io.github.streamingwithflink.chapter1.AverageSensorReadings ../../build/libs/examples-java-1.0.jar &



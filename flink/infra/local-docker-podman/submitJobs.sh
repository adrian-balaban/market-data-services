set -x

export CONTAINER_NAME=local-docker-podman-jobmanager-1
# echo copy the jar file to the flink job manager container
docker cp ../../build/libs/examples-java-1.0.jar ${CONTAINER_NAME}:/tmp/examples-java.jar

# echo run the flink job BloombergForexReadings in background
docker exec -it ${CONTAINER_NAME} /opt/flink/bin/flink run -c io.github.streamingwithflink.chapter1.BloombergForexReadings /tmp/examples-java.jar &

# echo run the flink job AverageSensorReadings  in background
docker exec -it ${CONTAINER_NAME} /opt/flink/bin/flink run -c io.github.streamingwithflink.chapter1.AverageSensorReadings /tmp/examples-java.jar &



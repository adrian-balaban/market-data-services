set -x

# echo copy the jar file to the flink job manager container
docker cp ../../build/libs/examples-java-1.0.jar local-jobmanager-1:/tmp/examples-java.jar

# echo run the flink job BloombergForexReadings in background
docker exec -it local-jobmanager-1 /opt/flink/bin/flink run -c io.github.streamingwithflink.chapter1.BloombergForexReadings /tmp/examples-java.jar &

# echo run the flink job AverageSensorReadings  in background
docker exec -it local-jobmanager-1 /opt/flink/bin/flink run -c io.github.streamingwithflink.chapter1.AverageSensorReadings /tmp/examples-java.jar &



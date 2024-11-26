echo build
gradle build

echo run BloombergForexReadings
flink run -c io.github.streamingwithflink.chapter1.BloombergForexReadings build/libs/examples-java-1.0.jar

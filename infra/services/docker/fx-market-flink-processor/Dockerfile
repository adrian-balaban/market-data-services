FROM alpine:latest

# Add all files from the directory to the image
COPY libs /flink/jars

# Set the entry point to an infinite loop that consumes minimal resources
ENTRYPOINT ["sh", "-c", "while :; do sleep 3600; done"]
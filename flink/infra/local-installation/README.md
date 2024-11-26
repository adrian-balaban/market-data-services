# Install Flink 1.20.0 on local machine using docker compose or podman and launch two exaple jobs

# Setup java 17: required for Flink 1.20
# Use https://sdkman.io/install/ to install java 17

``` bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 17.0.12-amzn
sdk default java 17.0.12-amzn
```

# Install flink locally with sdkman
``` bash
sdk install flink
```

# Build the jar with the job
``` bash
pushd ../.. && gradle build && popd
```

## To connect to Flink UI: http://localhost:8081/#/overview

## To run the example jobs:

``` bash
./submitJobs.sh
```
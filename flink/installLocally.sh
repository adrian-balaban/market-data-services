echo install sdk see https://sdkman.io/install/
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

sdk install flink

echo start flink cluster locally
start-cluster.sh


kubectl port-forward -n fxmarket controlcenter-0 9021:9021 &
kubectl port-forward fx-flink-jobmanager 8081:8081 &
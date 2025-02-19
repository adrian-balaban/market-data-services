1. Push public image to private registry:
    - `docker pull apache/flink-kubernetes-operator:1.10.0` 
    - `docker images | grep apache | grep flink` <-- image ID in my case: `3bbd4d8d2f49`
    - `docker tag 3bbd4d8d2f49 192.168.192.96:5001/apache/flink-kubernetes-operator:1.10.0`
    - `docker push 192.168.192.96:5001/apache/flink-kubernetes-operator:1.10.0`


2. List FLink CRDs:
    - `kubectl get flinkdeployment --all-namespaces `

3. Remove annoing
    - `kubectl patch flinkdeployment basic-example -n flink-op-1 -p '{"metadata":{"finalizers":null}}' --type=merge`
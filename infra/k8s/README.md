# Kubernetes Deployment

1. ## Prerequisite
   1. ### Tools
      - Kubectl (tested on v1.31.3)
      - Helm (tested on v3.16.3)
      - If Local Cluster - Kind / Minikube
   2. ### Cluster connection 
      - `kubectl get nodes` returns properly list of nodes.
      1. Local
         1. Minikube - TODO
         2. Kind 
            ```
            cd kind
            ./createKindClusterWithRegistry.sh
            ```
      2. Remote 
         - Ensure you have `~./kube/config` properly set
   3. ### Docker Image Registry Connection  
      1. Local 
         1. Minikube - TODO
         2. Kind - Already created by `createKindClusterWithRegistry.sh` script, and by default exposed on 5001 port.
            For the first time please specify registry in below config:
         
            `sudo cat /etc/containers/registries.conf.d/kind.conf`:
            ```
            [[registry]]
            location = "localhost:5001"
            insecure = true
            ```
2. ## Deployment
   - Run `deployAll.sh`
     - ```
        Usage:
        -build <true|false>           <- to build with test mode - default: true
        -test <true|false>            <- to build with test mode - default: false
        -n <namespace>                <- to specify namespace - default: fxmarket
        -tag <docker_tag>             <- to specify docker tag for services - default: 0.1.0
        -registry <DOCKER_REGISTRY>   <- to specify docker registry - default: localhost:5001
        ```
     - It takes around 10 minutes locally to build and run everything;
     - Works as an "UPSERT" command. You can run several times if needed without need of destroying each time.
   - Destroy `destroyAll.sh`

3. ## Connect
    - Run `./start-port-forwarding.sh -n <NAMESPACE>`
      - *Flink UI*: `http://localhost:8081/`
      - *KAFKA UI*: `http://localhost:9021/`   <- Control center is quite unstable, but Kafka is fine
      - *Kafka Broker*: `localhost:9092`
      - *Market Data Stub*: `localhost:3080`
    - Close `./stop-port-frwarding.sh`

---
### Some helpful commands
```
kubectl exec -ti kafka-0 -- bash
```
### in kafka:
```
kafka-console-consumer --bootstrap-server localhost:9092 --topic fx_rates
kafka-topics --create -replication-factor 1 --partitions 1 --topic fx_rates -bootstrap-server localhost:9092
```
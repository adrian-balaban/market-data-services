## Installation
https://kind.sigs.k8s.io/docs/user/quick-start/#installation

If you are running Kind cluster with podman:

`sudo cat /etc/containers/registries.conf.d/kind.conf`:
```
[[registry]]
location = "localhost:5001"
insecure = true
```

If you are running Minikube cluster with podman:

`sudo cat /etc/containers/registries.conf.d/minikube.conf`:
```
[[registry]]
location = "localhost:5001"
insecure = true
```

http://localhost:5001/v2/_catalog
http://localhost:5001/v2/fx-market/fx-market-connector/tags/list

https://gist.github.com/jefferyb/75f9f41cf14e6e4feae30d65584af108
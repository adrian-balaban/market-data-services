#!/bin/sh

set -e # exit immediately if any command within the script returns a non-zero exit status
set -o xtrace

echo Use the documentation from https://minikube.sigs.k8s.io/docs/start
echo For installing Knative later use these minikube drivers, see details here: https://minikube.sigs.k8s.io/docs/drivers/
echo on MacOS/Linux: virtualbox

if [ "$(uname)" == "Darwin" ]; then # MacOS
  brew install minikube #https://minikube.sigs.k8s.io/docs/start/?arch=%2Fmacos%2Fx86-64%2Fstable%2Fhomebrew
  #curl https://github.com/knative/client/releases/download/latest/kn-darwin-amd64
  #install kn-darwin-amd64 /usr/local/bin/kn
  #rm kn-darwin-amd64
else
  #echo install on linux with dpkg
  #curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube_latest_amd64.deb
  #sudo dpkg -i minikube_latest_amd64.deb
  #rm minikube_latest_amd64.deb

  echo on linux with qemu is needed socket_vmnet network installed, see https://github.com/lima-vm/socket_vmnet?tab=readme-ov-file#from-binary
  # sudo tar Cxzvf /  ~/Downloads/socket_vmnet-1.2.0-x86_64.tar.gz  opt/socket_vmnet
  echo brew install minikube
fi

echo prerequisites commands to setup minikube cluster
minikube config set cpus 5
minikube config set memory 15000
minikube config set driver virtualbox

# see https://minikube.sigs.k8s.io/docs/handbook/registry/#enabling-insecure-registries
minikube start --driver=virtualbox --host-dns-resolver=false --insecure-registry  "10.0.0.0/24" #--cni=false

minikube addons enable volumesnapshots
minikube addons enable csi-hostpath-driver
minikube addons enable dashboard
minikube addons enable default-storageclass
minikube addons enable storage-provisioner
minikube addons enable metrics-server
minikube addons enable helm-tiller
minikube addons enable logviewer
minikube addons enable registry
minikube addons enable registry-aliases

echo Get registry IP
kubectl get service registry -n kube-system -o jsonpath="{.spec.clusterIP}"

reg_port='5000'
echo Document the local registry
# https://github.com/kubernetes/enhancements/tree/master/keps/sig-cluster-lifecycle/generic/1755-communicating-a-local-registry
cat <<EOF | kubectl apply -f -
apiVersion: v1
kind: ConfigMap
metadata:
  name: local-registry-hosting
  namespace: kube-public
data:
  localRegistryHosting.v1: |
    host: "localhost:${reg_port}"
    #help: "https://kind.sigs.k8s.io/docs/user/local-registry/"
EOF

echo https://minikube.sigs.k8s.io/docs/handbook/registry/
echo https://minikube.sigs.k8s.io/docs/handbook/addons/registry-aliases/
#echo change registry type from ClusterIP to LoadBalancer and targetPort to 5001
#kubectl get service/registry -n kube-system -o yaml | sed 's/type: ClusterIP/type: LoadBalancer/'| kubectl replace -f -
#minikube service registry -n kube-system
#kubectl port-forward  svc/registry 5000:80 -n kube-system
#echo check http://localhost:5000/v2/_catalog

#echo change registry type from ClusterIP to NodePort and targetPort to 5001
#kubectl get service/registry -n kube-system -o yaml | sed 's/type: ClusterIP/type: NodePort/'| sed -E "s/(targetPort:\s[0-9]{0,5})/\1\n\ \ \ \ targetPort: 5001/"  | kubectl replace -f -
#echo check http://external-ip:5001/v2/_catalog
#kubectl get svc registry -n kube-system

kubectl port-forward --namespace kube-system service/registry 5001:80


echo continue with installing:./deployFlink.sh &&./deployKafka&&./deployKnativeCamelK.sh
echo install on linux with dpkg
curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube_latest_amd64.deb
sudo dpkg -i minikube_latest_amd64.deb
rm minikube_latest_amd64.deb

echo prerequisites commands to setup minikube cluster

minikube config set cpus 4
minikube config set memory 16384
minikube config set driver docker

minikube start

minikube addons enable volumesnapshots
minikube addons enable csi-hostpath-driver
minikube addons enable dashboard
minikube addons enable default-storageclass
minikube addons enable storage-provisioner
minikube addons enable metrics-server
minikube addons enable ingress
minikube addons enable ingress-dns

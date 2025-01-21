locals {
  registry_local = "localhost:5001"
  registry = "${var.host}:${var.registry_port}"
}
resource "terraform_data" "set-default-namespace" {
  provisioner "local-exec" {
    command = "kubectl config set-context --current --namespace=${var.namespace}"
  }
}

# to add use of var.build & var.test
resource "terraform_data" "buildExternals" {
  provisioner "local-exec" {
    command = "cd ../../k8s/scripts && ./buildExternals.sh -tag ${var.tag} -registry ${local.registry} && cd ../../terraform/cluster"
  }
  depends_on = [terraform_data.set-default-namespace]
}
/*resource "terraform_data" "deployExternals" {
  provisioner "local-exec" {
    command = "cd ../../k8s/scripts && ./deployExternals.sh -n ${var.namespace} -tag ${var.tag} -registry ${local.registry} && cd ../../terraform/cluster && kubectl wait pod --all -n ${var.namespace} --for=condition=ready --timeout=600s"
  }
  depends_on = [terraform_data.deployFlink]
}*/
resource "kubectl_manifest" "fx_market_externals_deployment" {
  yaml_body  = <<YAML
apiVersion: apps/v1
kind: Deployment
metadata:
  name: fx-market-data-stub
  namespace: ${var.namespace}
  labels:
    app: fx-market-data-stub
    release: fx-market-externals
    version: "${var.tag}"
spec:
  replicas:
  selector:
    matchLabels:
      app: fx-market-data-stub
      release: fx-market-externals
  template:
    metadata:
      labels:
        app: fx-market-data-stub
        release: fx-market-externals
        version: "${var.tag}"
    spec:
      containers:
        - name: fx-market-data-stub
          image: "${local.registry}/fx-market-externals/market-data-stub:${var.tag}"
          imagePullPolicy:
          env:

          ports:
            - name: http
              containerPort: 3080
              protocol: TCP
          resources:
            limits:
              cpu: 1
              memory: 1024Mi
            requests:
              cpu: 500m
              memory: 512Mi
          volumeMounts:
      topologySpreadConstraints:
      - maxSkew: 6
        topologyKey: kubernetes.io/hostname
        whenUnsatisfiable: DoNotSchedule
        labelSelector:
          matchLabels:
            release: fx-market-externals
YAML
  depends_on = [terraform_data.buildExternals]
}

resource "kubectl_manifest" "fx_market_externals_stub" {
  yaml_body  = <<YAML
apiVersion: v1
kind: Service
metadata:
  name: fx-market-data-stub-svc
  namespace: ${var.namespace}}
  labels:
    release: fx-market-externals
spec:
  type: ClusterIP
  ports:
    - port: 3080
      targetPort: 3080
      protocol: TCP
      name: http
  selector:
    app: fx-market-data-stub
YAML
  depends_on = [kubectl_manifest.fx_market_externals_deployment]
}

# to add use of var.build & var.test
resource "terraform_data" "buildSolution" {
  provisioner "local-exec" {
    command = "cd ../../k8s/scripts && ./buildSolution.sh -tag ${var.tag} -registry ${local.registry} && cd ../../terraform/cluster && sleep 5"
  }
  depends_on = [terraform_data.buildExternals]
}
resource "terraform_data" "deployKafka" {
  provisioner "local-exec" {
    command = "cd ../../k8s/scripts && ./deployKafka.sh -n ${var.namespace} && cd ../../terraform/cluster && kubectl wait pod --all -n ${var.namespace} --for=condition=ready --timeout=600s"
  }
  depends_on = [terraform_data.buildSolution]
}
resource "terraform_data" "deployFlink" {
  provisioner "local-exec" {
    command = "cd ../../k8s/scripts && ./deployFlink.sh -n ${var.namespace} && cd ../../terraform/cluster && kubectl wait pod --all -n ${var.namespace} --for=condition=ready --timeout=600s"
  }
  depends_on = [terraform_data.deployKafka]
}

resource "terraform_data" "deploySolution" {
  provisioner "local-exec" {
    command = "cd ../../k8s/scripts && ./deploySolution.sh -n ${var.namespace} -tag ${var.tag} -registry ${local.registry} && cd ../../terraform/cluster && kubectl wait pod --all -n ${var.namespace} --for=condition=ready --timeout=600s"
  }
  depends_on = [terraform_data.deployFlink]
}

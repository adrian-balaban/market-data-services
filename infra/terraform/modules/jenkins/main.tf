# used https://jenkinsci.github.io/kubernetes-operator/docs/getting-started/latest/installing-the-operator/
locals {namespace = "jenkins"}
resource "kubernetes_namespace" "jenkins" {
  metadata {
    name = "${local.namespace}"
  }
}
resource "null_resource" "set_default_namespace" {
  provisioner "local-exec" {
    command = "kubectl config set-context --current --namespace=${local.namespace}"
  }
  depends_on = [
    kubernetes_namespace.jenkins
  ]
}
resource "terraform_data" "operator" {
  provisioner "local-exec" {
    command = "helm repo add jenkins https://raw.githubusercontent.com/jenkinsci/kubernetes-operator/master/chart || true"
  }
  depends_on = [null_resource.set_default_namespace]
}
#$ helm install <name> jenkins/jenkins-operator -n <your-namespace>*/
resource "kubectl_manifest" "secret_for_github" {
  yaml_body = <<YAML
apiVersion: v1
kind: Secret
metadata:
    name: market-data-processor-private-repository
    namespace: "${local.namespace}"
    labels:
      argocd.argoproj.io/secret-type: repository
stringData:
  type: git
  url: https://github.com/Jereczek/market-data-processor.git
  username: argocd-private-repo
  password: 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
YAML
  depends_on = [null_resource.set_default_namespace
  ]
}
resource "helm_release" "jenkins" {
  name       = "jenkins"
  repository = "https://charts.jenkins.io"
  chart      = "jenkins"
  namespace  = kubernetes_namespace.jenkins.metadata[0].name

  set = [ {
    name  = "controller.adminPassword"
    value = "admin"
  },
  {
    name  = "persistence.enabled"
    value = "true"
  },{
    name  = "persistence.size"
    value = "8Gi"
  },{
    name  = "resources.requests.cpu"
    value = "500m"
  },{
    name  = "resources.requests.memory"
    value = "512Mi"
  },{
    name  = "resources.limits.cpu"
    value = "1"
  },{
    name  = "resources.limits.memory"
    value = "1024Mi"
  },{
    name  = "controller.installPlugins[0]"
    value = "kubernetes:1.29.2"
  },{
    name  = "controller.installPlugins[1]"
    value = "workflow-aggregator:2.6"
  },{
    name  = "controller.installPlugins[2]"
    value = "git:4.7.1"
  },{
    name  = "controller.jenkinsUrl"
    value = "http://jenkins.example.com"
  },{
    name  = "controller.JCasC.configScripts"
    value = <<-EOT
      jenkins:
        systemMessage: "Welcome to Jenkins configured by Terraform"
        securityRealm:
          local:
            allowsSignup: false
            users:
              - id: "admin"
                password: "admin"
        authorizationStrategy:
          loggedInUsersCanDoAnything:
            allowAnonymousRead: false
    EOT
  },{
    name  = "controller.initScripts[0]"
    value = <<-EOT
      #!/bin/bash
      echo "Custom init script"
    EOT
  },
  
  {
    name  = "ingress.enabled"
    value = "true"
  },{
    name  = "ingress.hosts[0].host"
    value = "jenkins.example.com"
  },{
    name  = "ingress.hosts[0].paths[0].path"
    value = "/"
  },{
    name  = "ingress.hosts[0].paths[0].pathType"
    value = "ImplementationSpecific"
  },{
    name  = "controller.installPlugins[0]"
    value = "job-dsl:1.77"
  },{
    name  = "controller.installPlugins[1]"
    value = "configuration-as-code:1.51"
  }

]
  /*
    set {
      name  = "controller.serviceType"
      value = "LoadBalancer"
    }

    set {
      name  = "controller.numExecutors"
      value = "5"
    },
    {
    name  = "controller.nodeSelector"
    value = <<-EOT
      disktype: ssd
    EOT
  },{
    name  = "controller.tolerations[0].key"
    value = "key1"
  },{
    name  = "controller.tolerations[0].operator"
    value = "Equal"
  },{
    name  = "controller.tolerations[0].value"
    value = "value1"
  },{
    name  = "controller.tolerations[0].effect"
    value = "NoSchedule"
  }
*/
  /*set {
    name  = "controller.JCasC.configScripts"
    value = file("jenkins.yaml")
  }*/
}

resource "terraform_data" "instance" {
  provisioner "local-exec" {
    command = "kubectl apply -f ../jenkins/jenkins_instance.yaml -n ${local.namespace}"
  }
  depends_on = [helm_release.jenkins]
}

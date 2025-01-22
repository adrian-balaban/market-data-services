locals {
  registry = "${var.registry_host}:${var.registry_port}"
}

# to add use of var.build & var.test
resource "terraform_data" "buildExternals" {
  count = var.build == "true" ? 1 : 0
  provisioner "local-exec" {
    command = "cd ../../k8s/scripts && ./buildExternals.sh -tag ${var.tag} -registry ${local.registry} && cd ../../terraform/cluster"
  }
}
# to add use of var.build & var.test
resource "terraform_data" "buildSolution" {
  count = var.build == "true" ? 1 : 0
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
resource "terraform_data" "deployExternals" {
  provisioner "local-exec" {
    command = "cd ../../k8s/scripts && ./deployExternals.sh -n ${var.namespace} -tag ${var.tag} -registry ${local.registry} && cd ../../terraform/cluster && kubectl wait pod --all -n ${var.namespace} --for=condition=ready --timeout=600s"
  }
  depends_on = [terraform_data.deployFlink]
}
resource "terraform_data" "deploySolution" {
  provisioner "local-exec" {
    command = "cd ../../k8s/scripts && ./deploySolution.sh -n ${var.namespace} -tag ${var.tag} -registry ${local.registry} && cd ../../terraform/cluster && kubectl wait pod --all -n ${var.namespace} --for=condition=ready --timeout=600s"
  }
  depends_on = [terraform_data.deployFlink]
}
resource "terraform_data" "set-default-namespace" {
  provisioner "local-exec" {
    command = "kubectl config set-context --current --namespace=${var.namespace}"
  }
  depends_on = [terraform_data.deploySolution]
}
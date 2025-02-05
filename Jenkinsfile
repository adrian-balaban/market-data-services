def deployment = [useBash: true, useArgoCD: false]
pipeline {
    agent any
    tools {
        jdk '21'
    }
    environment {
                build="true"
                test="true"
                tag="0.0.1-${env.BRANCH_NAME}"
                registry="192.168.192.96:5001"
    }
    options {
        buildDiscarder(logRotator(
            // number of builds to keep
            numToKeepStr: env.BRANCH_NAME ==~ /master/ ? '3' :
                env.BRANCH_NAME ==~ /support\/.*/ ? '3' :
                    env.BRANCH_NAME ==~ /release/ ? '3' :
                        env.BRANCH_NAME ==~ /develop/ ? '3' :
                            env.BRANCH_NAME ==~ /feature\/.*|bugfix\/.*|hotfix\/.*/ ? '3' : '1',
            // number of builds to keep the artifacts from
            artifactNumToKeepStr: env.BRANCH_NAME ==~ /master/ ? '3' :
                env.BRANCH_NAME ==~ /support\/.*/ ? '3' :
                    env.BRANCH_NAME ==~ /release/ ? '3' :
                        env.BRANCH_NAME ==~ /develop/ ? '3' :
                            env.BRANCH_NAME ==~ /feature\/.*|bugfix\/.*|hotfix\/.*/ ? '1' : '0'
        ))

        // this will limit the build to one per branch
        disableConcurrentBuilds()
    }

    stages {
        stage('Ask parameters') {
            options { timeout(time: 10, unit: 'SECONDS') }
            steps {
                script {
                    try {
                        deployment = input(
                            message: "Use bash?",
                                parameters: [
                                    booleanParam(defaultValue: true, name: 'useBash')
                                ]
                            )
                    }catch(err) {
                        deployment.useBash = true
                    } finally {
                        echo "CONFIG: deployment.useBash: " + deployment.useBash
                        deployment.useArgoCD = !deployment.useBash
                        echo "CONFIG: deployment.useArgoCD: " + deployment.useArgoCD
                    }
                }
                script {
                  echo "CONFIG: deployment.useBash: " + deployment.useBash
                }
            }
        }
        stage('Checkout') {
            steps {
                checkout scmGit(branches: [[name: '**']], extensions: [], userRemoteConfigs: [[credentialsId: 'github-owner-token', url: 'https://github.com/Jereczek/market-data-services.git']])
            }
        }
        stage("Build&Deploy use bash") {
          when { deployment.useBash }  {
            steps {
              script {
                 def k8s_namespace = env.BRANCH_NAME == 'master' ? 'test' : env.BRANCH_NAME
                 withKubeConfig(caCertificate: 'LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSURCVENDQWUyZ0F3SUJBZ0lJVFRxVjBzTFozUkl3RFFZSktvWklodmNOQVFFTEJRQXdGVEVUTUJFR0ExVUUKQXhNS2EzVmlaWEp1WlhSbGN6QWVGdzB5TlRBeE16RXhNakl6TURSYUZ3MHpOVEF4TWpreE1qSTRNRFJhTUJVeApFekFSQmdOVkJBTVRDbXQxWW1WeWJtVjBaWE13Z2dFaU1BMEdDU3FHU0liM0RRRUJBUVVBQTRJQkR3QXdnZ0VLCkFvSUJBUURoS3ZSYURaVnNPem0xNWRYV2tDeXpQKzBhMWVwaVdKQ1BGUDU5T1dIR25CemwvK0pWZ1hQdEk3ZTkKcVEySWtwejA4dnMrWDBnNFh2UlJrTlhULzdTcEpqVnM2OHlnRFZid0lsK1V4RWg5a0QxdzNDVkNnTjNvNWc5MgoydTBDeXVIUVdFekNqRVBrc0F2TGlRdU5rZlljdXdnbVVGLzcwS1VqdmRMNEhYQThiN055OUV6MTdOZGhNKzMrClV2T1J4dmFSMk9BaE1jN25Pa3Y1T0pCRFNoUEtDbVprVzh4NWNkZ0ozK29OTmdSa29hU2ZkUXdiS3pFM2N3VFgKT3ZFYUVMZHlIbm15bnIrdi9PLzZrU1VsVDhYWUZVNlVJMEdjMFF2RGlLZmkzdzdBOUJMMS9MdDZJN3BISi9EegplZG5JV0NicGNnTVJzN09QdG80cDltZE1rUXFQQWdNQkFBR2pXVEJYTUE0R0ExVWREd0VCL3dRRUF3SUNwREFQCkJnTlZIUk1CQWY4RUJUQURBUUgvTUIwR0ExVWREZ1FXQkJTQkcxamlRcHlpSURUNmhkTG83VGwrZDBFcFh6QVYKQmdOVkhSRUVEakFNZ2dwcmRXSmxjbTVsZEdWek1BMEdDU3FHU0liM0RRRUJDd1VBQTRJQkFRQ0VLNklxNGJ3QgorWkI5a3FhRTl3M1ltSjNPaVpFaElMckNDVXdFcWtjeXlGZW9jVURSMlFOcVVUV3hwdGlKWnoycVlKOGh0STY2Ck14bllCK0F4YWtKYWkvY0JZMzU2UURpWTJJOFNlYlhKdFNtSCtIZjNVQTRJZXRBKzFyNzJVb2kzc3dPcW01Z0sKcm0raEptK2MwZEdFQlZPemRQdlhSb0w5STVMcDBKc2J2aHVnSHJYRjFyQ3pBaXZ4Uit1RVlXWTdiZk9BeG56eAowM0JMTjRQbGVrWGZSbHdZeGFLNXRZV2lzcHlKblpXUWx3VXp2ODdRVDFsOTgrVUpWa2VCakVITTJmTTBpMHBTCkVBd0tiQ1JYY010TWN5YmZObUtWbGRILy81VjlJR25zakpHNUprUXhIaE5GRmtTWjd5TjBSSTcwTVdiaGt3YmUKY2wwbzQxeGFhU2xjCi0tLS0tRU5EIENFUlRJRklDQVRFLS0tLS0K',
                   clusterName: 'kind-kind', contextName: 'kind-kind', credentialsId: 'K8sConfigMichal', namespace: 'default',
                   restrictKubeConfigAccess: true, serverUrl: 'https://192.168.192.96:6443') {
                     sh "cd infra/k8s && ./deployAll.sh -build ${env.build} -test ${env.test} -n ${k8s_namespace} -tag ${env.tag} -registry ${env.registry}"
                 }
              }
            }
          }
        }
        stage("Build&Deploy use ArgoCD") {
          when { deployment.useArgoCD } {
            steps {
              script {
                 def k8s_namespace = env.BRANCH_NAME == 'master' ? 'test' : env.BRANCH_NAME
                 withKubeConfig(caCertificate: 'LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSURCVENDQWUyZ0F3SUJBZ0lJVFRxVjBzTFozUkl3RFFZSktvWklodmNOQVFFTEJRQXdGVEVUTUJFR0ExVUUKQXhNS2EzVmlaWEp1WlhSbGN6QWVGdzB5TlRBeE16RXhNakl6TURSYUZ3MHpOVEF4TWpreE1qSTRNRFJhTUJVeApFekFSQmdOVkJBTVRDbXQxWW1WeWJtVjBaWE13Z2dFaU1BMEdDU3FHU0liM0RRRUJBUVVBQTRJQkR3QXdnZ0VLCkFvSUJBUURoS3ZSYURaVnNPem0xNWRYV2tDeXpQKzBhMWVwaVdKQ1BGUDU5T1dIR25CemwvK0pWZ1hQdEk3ZTkKcVEySWtwejA4dnMrWDBnNFh2UlJrTlhULzdTcEpqVnM2OHlnRFZid0lsK1V4RWg5a0QxdzNDVkNnTjNvNWc5MgoydTBDeXVIUVdFekNqRVBrc0F2TGlRdU5rZlljdXdnbVVGLzcwS1VqdmRMNEhYQThiN055OUV6MTdOZGhNKzMrClV2T1J4dmFSMk9BaE1jN25Pa3Y1T0pCRFNoUEtDbVprVzh4NWNkZ0ozK29OTmdSa29hU2ZkUXdiS3pFM2N3VFgKT3ZFYUVMZHlIbm15bnIrdi9PLzZrU1VsVDhYWUZVNlVJMEdjMFF2RGlLZmkzdzdBOUJMMS9MdDZJN3BISi9EegplZG5JV0NicGNnTVJzN09QdG80cDltZE1rUXFQQWdNQkFBR2pXVEJYTUE0R0ExVWREd0VCL3dRRUF3SUNwREFQCkJnTlZIUk1CQWY4RUJUQURBUUgvTUIwR0ExVWREZ1FXQkJTQkcxamlRcHlpSURUNmhkTG83VGwrZDBFcFh6QVYKQmdOVkhSRUVEakFNZ2dwcmRXSmxjbTVsZEdWek1BMEdDU3FHU0liM0RRRUJDd1VBQTRJQkFRQ0VLNklxNGJ3QgorWkI5a3FhRTl3M1ltSjNPaVpFaElMckNDVXdFcWtjeXlGZW9jVURSMlFOcVVUV3hwdGlKWnoycVlKOGh0STY2Ck14bllCK0F4YWtKYWkvY0JZMzU2UURpWTJJOFNlYlhKdFNtSCtIZjNVQTRJZXRBKzFyNzJVb2kzc3dPcW01Z0sKcm0raEptK2MwZEdFQlZPemRQdlhSb0w5STVMcDBKc2J2aHVnSHJYRjFyQ3pBaXZ4Uit1RVlXWTdiZk9BeG56eAowM0JMTjRQbGVrWGZSbHdZeGFLNXRZV2lzcHlKblpXUWx3VXp2ODdRVDFsOTgrVUpWa2VCakVITTJmTTBpMHBTCkVBd0tiQ1JYY010TWN5YmZObUtWbGRILy81VjlJR25zakpHNUprUXhIaE5GRmtTWjd5TjBSSTcwTVdiaGt3YmUKY2wwbzQxeGFhU2xjCi0tLS0tRU5EIENFUlRJRklDQVRFLS0tLS0K',
                   clusterName: 'kind-kind', contextName: 'kind-kind', credentialsId: 'K8sConfigMichal', namespace: 'default',
                   restrictKubeConfigAccess: true, serverUrl: 'https://192.168.192.96:6443') {
                     sh "cd infra/argo && ./deployAllWithArgo.sh -build ${env.build} -test ${env.test} -n ${k8s_namespace} -tag ${env.tag} -registry ${env.registry} -env test -branch ${env.BRANCH_NAME}"
                 }
              }
            }
          }
        }
        stage('Test') {
            steps {
                sh 'cd qa && ./gradlew cucumberFullRun'
            }
        }
    }
    post {
            always {
                cleanWs()
            }
    }
}

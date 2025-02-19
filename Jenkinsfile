def jenkinsfilename = 'Jenkinsfile'
pipeline {
    agent any
    tools {
        jdk '21'
    }
    parameters {
        booleanParam(defaultValue: true, name: 'build')
        booleanParam(defaultValue: false, name: 'test')
        string(defaultValue: "0.0.1", name: 'tag_root')
        string(defaultValue: "192.168.192.96:5001", name: 'registry')
        string(defaultValue: env.BRANCH_NAME, name: 'k8s_namespace')
    }
    options {
        buildDiscarder(logRotator(
            // number of builds to keep
            numToKeepStr: env.BRANCH_NAME ==~ /master/ ? '3' :
                env.BRANCH_NAME ==~ /support\/.*/ ? '3' :
                    env.BRANCH_NAME ==~ /release/ ? '3' :
                        env.BRANCH_NAME ==~ /develop/ ? '3' :
                            env.BRANCH_NAME ==~ /feature\/.*|bugfix\/.*|hotfix\/.*/ ? '3' : '3',
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
        stage("Clean workspace") {
            steps {
                cleanWs()
                script {
                    deleteDir()
                }
            }
        }
        stage('Checkout') {
            when {
                allOf {
                    triggeredBy 'UserIdCause' // start the job only if it is launched by user
                    //not { changeset pattern: "${jenkinsfilename}" }  // exclude this Jenkinsfile from the “changeset” detected by Jenkins Pipeline
                }
            }
            steps {
                checkout scmGit(branches: [[name: '**']], extensions: [], userRemoteConfigs: [[credentialsId: 'github-owner-token', url: 'https://github.com/Jereczek/market-data-services.git']])
            }
        }
        stage("Build&Deploy with bash script") {
            when {
                allOf {
                    triggeredBy 'UserIdCause' // start the job only if it is launched by user
                    //not { changeset pattern: "${jenkinsfilename}" }  // exclude this Jenkinsfile from the “changeset” detected by Jenkins Pipeline
                }
            }
            steps {
              script {
                withKubeConfig(
                  clusterName: 'kind-kind', contextName: 'kind-kind', credentialsId: 'K8sConfigMichal', namespace: 'default',
                  restrictKubeConfigAccess: true, serverUrl: 'https://192.168.192.96:6443') {
                    sh "cd ./infra/k8s && ./deployAll.sh      -build ${params.build} -test ${params.test} -n ${params.k8s_namespace} -tag ${params.tag_root}-${env.BRANCH_NAME} -registry ${params.registry}"
                }
              }
            }
        }
        /*stage("Build&Deploy with argocd script") {
            when {
                allOf {
                    triggeredBy 'UserIdCause' // start the job only if it is launched by user
                    not { changeset pattern: "${jenkinsfilename}" }  // exclude this Jenkinsfile from the “changeset” detected by Jenkins Pipeline
                }
            }
            steps {
                script {
                    withKubeConfig(
                        clusterName: 'kind-kind', contextName: 'kind-kind', credentialsId: 'K8sConfigMichal', namespace: 'default',
                        restrictKubeConfigAccess: true, serverUrl: 'https://192.168.192.96:6443') {
                        sh "cd  ./infra/argo && ./deployAllWithArgo.sh -build ${params.build} -test ${params.test} -n ${params.k8s_namespace} -tag ${params.tag_root}-${env.BRANCH_NAME} -registry ${params.registry} -branch ${env.BRANCH_NAME} -env test"
                    }
                }
            }
        }*/
        stage('CucumberRun') {
            environment {
                JENKINS_RUN = "true"
            }
            when {
                allOf {
                    triggeredBy 'UserIdCause' // start the job only if it is launched by user
                    //not { changeset pattern: "${jenkinsfilename}" }  // exclude this Jenkinsfile from the “changeset” detected by Jenkins Pipeline
                }
            }
            steps {
                script {
                    sh 'infra/k8s/stop-port-forwarding.sh'
                    sh "infra/k8s/start-port-forwarding.sh -n ${params.k8s_namespace}"
                    try {
                        sh 'cd qa && ./gradlew cucumberFullRun'
                    } finally {
                        sh 'infra/k8s/stop-port-forwarding.sh'
                    }
                }
            }
            post {
                always {
                    junit '**/qa/build/reports/cucumber/cucumber.xml'

                    cucumber(
                        buildStatus: 'UNCHANGED',
                        customCssFiles: '',
                        customJsFiles: '',
                        failedFeaturesNumber: -1,
                        failedScenariosNumber: -1,
                        failedStepsNumber: -1,
                        fileIncludePattern: 'qa/build/reports/cucumber/cucumber.json',
                        pendingStepsNumber: -1,
                        skippedStepsNumber: -1,
                        sortingMethod: 'ALPHABETICAL',
                        undefinedStepsNumber: -1
                    )
                }
            }
        }
    }
}
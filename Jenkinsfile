pipeline {
    agent any
    tools {
        jdk '21'
    }
    environment {
                build="true"
                test="true"
                tag="test"
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
        stage('Checkout') {
            steps {
                checkout scmGit(branches: [[name: '**']], extensions: [], userRemoteConfigs: [[credentialsId: 'github-owner-token', url: 'https://github.com/Jereczek/market-data-services.git']])
            }
        }
        stage('Build&Deploy') {
            steps {
                //sh 'cd fx-market-services && ./gradlew --no-daemon clean build --refresh-dependencies '
                sh "cd infra/k8s && ./deployAll.sh -build ${env.build} -test ${env.test} -n ${env.BRANCH_NAME} -tag ${env.tag} -registry ${env.registry}"
            }
        }
        stage('Test') {
            steps {
                sh 'cd fx-market-services && ./gradlew test'
            }
        }
    }
}

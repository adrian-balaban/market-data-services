pipelineJob('springboot-build') {
    definition {
        cps {
            script("""
                pipeline {
                    agent any
                    stages {
                        stage('Checkout') {
                            steps {
                                git 'https://github.com/your-repo/springboot-project.git'
                            }
                        }
                        stage('Build') {
                            steps {
                                sh './mvnw clean package'
                            }
                        }
                        stage('Test') {
                            steps {
                                sh './mvnw test'
                            }
                        }
                        stage('Deploy') {
                            steps {
                                echo 'Deploying...'
                                // Add your deployment steps here
                            }
                        }
                    }
                }
            """)
        }
    }
}
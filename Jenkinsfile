pipeline {
    agent any
    environment {
        MAVEN_OPTS = "-Dmaven.repo.local=.m2/repository"
    }
    options {
        skipDefaultCheckout() // Tránh clone lại toàn bộ repo
    }
    stages {
        stage('Checkout') {
            steps {
                script {
                    // Lấy commit trước đó để so sánh
                    def previousCommit = sh(script: "git rev-parse HEAD~1", returnStdout: true).trim()
                    def changedFiles = sh(script: "git diff --name-only ${previousCommit}", returnStdout: true).trim().split('\n')

                    // Danh sách service trong repo
                    def services = ['customers-service', 'vets-service', 'visits-service', 'api-gateway', 'config-server', 'discovery-server']
                    def affectedServices = services.findAll { service ->
                        changedFiles.any { it.startsWith("${service}/") }
                    }

                    if (affectedServices.isEmpty()) {
                        echo "No services affected, skipping pipeline."
                        currentBuild.result = 'SUCCESS'
                        return
                    }

                    env.AFFECTED_SERVICES = affectedServices.join(' ')
                    echo "Affected services: ${env.AFFECTED_SERVICES}"
                }
            }
        }

        stage('Test') {
            when {
                expression { env.AFFECTED_SERVICES != null && env.AFFECTED_SERVICES.trim() != '' }
            }
            steps {
                script {
                    env.AFFECTED_SERVICES.split(' ').each { service ->
                        dir(service) {
                            echo "Running tests for ${service}"
                            sh 'mvn test'
                            junit '**/target/surefire-reports/*.xml' // Upload test results
                            jacoco execPattern: '**/target/jacoco.exec' // Thu thập độ phủ code
                        }
                    }
                }
            }
        }

        stage('Build') {
            when {
                expression { env.AFFECTED_SERVICES != null && env.AFFECTED_SERVICES.trim() != '' }
            }
            steps {
                script {
                    env.AFFECTED_SERVICES.split(' ').each { service ->
                        dir(service) {
                            echo "Building ${service}"
                            sh 'mvn package -DskipTests'
                        }
                    }
                }
            }
        }
    }
}

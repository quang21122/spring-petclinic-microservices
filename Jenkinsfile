pipeline {
  agent any
  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Test') {
      post {
        always {
          junit '**/target/surefire-reports/*.xml'
        }

      }
      steps {
        sh './mvnw test'
      }
    }

    stage('Build') {
      steps {
        sh './mvnw package'
      }
    }

  }
  environment {
    MAVEN_HOME = 'Maven'
  }
}
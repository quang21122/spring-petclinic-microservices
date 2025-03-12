pipeline {
  agent any
  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Test') {
      when {
        expression {
          env.NO_SERVICES_TO_BUILD == 'false'
        }

      }
      post {
        always {
          script {
            env.SERVICES_TO_BUILD.split(',').each { service ->
            dir("spring-petclinic-${service}") {
              junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
              jacoco(
                execPattern: '**/target/jacoco.exec',
                classPattern: '**/target/classes',
                sourcePattern: '**/src/main/java',
                exclusionPattern: '**/test/**'
              )
            }
          }
        }

      }

    }
    steps {
      script {
        env.SERVICES_TO_BUILD.split(',').each { service ->
        dir("spring-petclinic-${service}") {
          echo "Testing ${service}..."
          try {
            // Run tests with JaCoCo coverage for specific service
            sh """
            echo "Running tests for ${service}"
            ../mvnw clean test verify -Pcoverage
            """
          } catch (Exception e) {
            echo "Tests failed for ${service}"
            throw e
          }
        }
      }
    }

  }
}

stage('Build') {
  when {
    expression {
      env.NO_SERVICES_TO_BUILD == 'false'
    }

  }
  post {
    success {
      script {
        env.SERVICES_TO_BUILD.split(',').each { service ->
        dir("spring-petclinic-${service}") {
          archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
        }
      }
    }

  }

}
steps {
  script {
    env.SERVICES_TO_BUILD.split(',').each { service ->
    dir("spring-petclinic-${service}") {
      echo "Building ${service}..."
      try {
        sh """
        echo "Building ${service}"
        ../mvnw clean package -DskipTests
        """
      } catch (Exception e) {
        echo "Build failed for ${service}"
        throw e
      }
    }
  }
}

}
}

}
environment {
MAVEN_HOME = 'Maven'
}
post {
always {
cleanWs()
}

}
}
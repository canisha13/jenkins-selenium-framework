pipeline {
    agent any

    stages {
        stage('Build and Test') {
            steps {
                sh './mvnw clean test'
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/TEST-*.xml'
            archiveArtifacts allowEmptyArchive: true, artifacts: 'target/surefire-reports/**,target/screenshots/**,test-output/**'
        }
    }
}
pipeline {
    agent any

    stages {
        stage('Start Selenium') {
            steps {
                sh '''
                docker rm -f selenium-browser || true
                docker run -d \
                  --name selenium-browser \
                  --shm-size=2g \
                  -p 4444:4444 \
                  -p 7900:7900 \
                  -e SE_VNC_NO_PASSWORD=1 \
                  selenium/standalone-chrome:4.44.0-20260505
                '''
            }
        }

        stage('Wait for Selenium') {
            steps {
                sh '''
                for i in $(seq 1 30); do
                  if curl -s http://localhost:4444/status | grep -q '"ready": true'; then
                    echo "Selenium is ready"
                    exit 0
                  fi
                  echo "Waiting for Selenium..."
                  sleep 2
                done
                exit 1
                '''
            }
        }

        stage('Build and Test') {
            steps {
                sh 'mvn clean test -Dbrowser=chrome -DremoteUrl=http://localhost:4444 -DbaseUrl=https://www.saucedemo.com/'
            }
        }
    }

    post {
        always {
            sh 'docker logs selenium-browser > selenium-container.log 2>&1 || true'
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/TEST-*.xml'
            archiveArtifacts allowEmptyArchive: true, artifacts: 'target/surefire-reports/**,target/screenshots/**,test-output/**,selenium-container.log'
            sh 'docker rm -f selenium-browser || true'
        }
    }
}
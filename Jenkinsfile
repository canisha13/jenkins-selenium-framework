pipeline {
    agent any

    triggers {
        cron('H 1 * * 1-5')
    }

    environment {
        SAUCE_USERNAME = credentials('saucedemo-username')
        SAUCE_PASSWORD = credentials('saucedemo-password')
    }

    parameters {
        choice(name: 'BROWSER', choices: ['chrome', 'firefox'], description: 'Browser to run')
        choice(name: 'TEST_GROUP', choices: ['all', 'smoke', 'regression'], description: 'TestNG group')
    }

    stages {
        stage('Start Selenium') {
            steps {
                sh '''
                docker rm -f selenium-browser || true
                docker run -d \
                  --name selenium-browser \
                  --network jenkins-lab \
                  --shm-size=2g \
                  -e SE_VNC_NO_PASSWORD=1 \
                  selenium/standalone-${BROWSER}:4.44.0-20260505
                '''
            }
        }

        stage('Wait for Selenium') {
            steps {
                sh '''
                for i in $(seq 1 30); do
                  if curl -s http://selenium-browser:4444/status | grep -q '"ready": true'; then
                    echo "Selenium is ready"
                    exit 0
                  fi
                  sleep 2
                done
                exit 1
                '''
            }
        }

        stage('Build and Test') {
            steps {
                sh """
                mvn clean test \
                  -Dbrowser=${params.BROWSER} \
                  -DremoteUrl=http://selenium-browser:4444 \
                  -DbaseUrl=https://www.saucedemo.com/ \
                  -Dusername=$SAUCE_USERNAME_USR \
                  -Dpassword=$SAUCE_PASSWORD
                """
            }
        }
    }

    post {
        always {
            sh 'docker logs selenium-browser > selenium-container.log || true'
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/TEST-*.xml'
            archiveArtifacts allowEmptyArchive: true, artifacts: 'target/screenshots/**,target/surefire-reports/**,test-output/**,selenium-container.log'
            sh 'docker rm -f selenium-browser || true'
        }
    }
}
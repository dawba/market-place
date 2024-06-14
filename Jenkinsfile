pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                // Checkout the code from your version control system
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh "./gradlew build"
            }
        }

        stage('Test') {
            steps {
                sh "./gradlew test"
            }
        }

        stage('Deploy') {
            steps {
                // Run ./gradlew deploy task (replace 'deploy' with your actual deploy task)
                sh "./gradlew deploy"
            }
        }
    }

    post {
        success {
            // Send notification on success
            echo 'Build successful! Deployed successfully!'
        }

        failure {
            // Send notification on failure
            echo 'Build or deploy failed!'
        }
    }
}
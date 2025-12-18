pipeline {
    agent any

    tools {
        maven 'Maven-3'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Sherikoo/eCommerce-App.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Docker Build') {
            steps {
                sh '''
                docker build -t ecommerce-app .
                '''
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                docker stop ecommerce-app || true
                docker rm ecommerce-app || true

                docker run -d \
                  --name ecommerce-app \
                  -p 8080:8080 \
                  ecommerce-app
                '''
            }
        }
    }
}


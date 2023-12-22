pipeline {

    agent any

    tools {
        maven 'my-maven'
    }
    environment {
        MYSQL_ROOT_LOGIN = credentials('mysql-root-login')
        DOCKERHUB_CREDENTIALS = credentials('dockerhub')
    }
    stages {

        stage('Build with Maven') {
            steps {
                sh 'mvn --version'
                sh 'java -version'
                sh 'mvn clean package -Dmaven.test.failure.ignore=true'
            }
        }

        stage('Packaging/Pull image') {
            steps {
                sh "docker pull hoangtammht/exeproject:latest"
                sh "docker stop springboot-deploy || true && docker rm springboot-deploy || true"
                sh "docker run --name springboot-deploy -d -p 8081:8081 hoangtammht/exeproject:latest"
            }
        }

    }
    post {
        // Clean after build
        always {
            cleanWs()
        }
    }
}
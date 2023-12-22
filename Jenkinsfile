pipeline {
    agent any

    tools {
        maven 'my-maven'
    }

    environment {
        DOCKER_IMAGE = 'hoangtammht/exeproject:latest'
        CONTAINER_NAME = 'springboot-deploy'
        CONTAINER_PORT = 8081
    }

    stages {
        stage('Build with Maven') {
            steps {
                echo 'Checking Maven version:'
                sh 'mvn --version'

                echo 'Checking Java version:'
                sh 'java -version'

                echo 'Building with Maven:'
                sh 'mvn clean package -Dmaven.test.failure.ignore=true'
            }
        }

        stage('Packaging/Pull image') {
            steps {
                echo "Pulling Docker image: ${DOCKER_IMAGE}"
                sh "docker pull ${DOCKER_IMAGE}"

                echo "Stopping and removing existing container: ${CONTAINER_NAME}"
                sh "docker stop ${CONTAINER_NAME} || true && docker rm ${CONTAINER_NAME} || true"

                echo "Running Docker container: ${CONTAINER_NAME}"
                sh "docker run --name ${CONTAINER_NAME} -d -p ${CONTAINER_PORT}:${CONTAINER_PORT} ${DOCKER_IMAGE}"
            }
        }
    }

    post {
        always {
            echo 'Cleaning workspace...'
            cleanWs()
        }
    }
}

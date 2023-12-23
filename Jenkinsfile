pipeline {

    agent any

    tools {
        maven 'my-maven'
    }
    environment {
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

        stage('Packaging/Pushing image') {
                    steps {
                        withDockerRegistry(credentialsId: 'dockerhub', url: 'https://index.docker.io/v1/') {
                            sh 'docker build -t hoangtammht/exeproject .'
                            sh 'docker push hoangtammht/exeproject'
                        }
                    }
                }

        stage('Deploy MySQL to DEV') {
            steps {
                echo 'Deploying and cleaning'
                sh 'docker image pull mysql:8.0'
                sh 'docker network create dev || echo "this network exists"'
                sh 'docker container stop hoangtammht-mysql || echo "this container does not exist" '
                sh 'echo y | docker container prune '
                sh 'docker volume rm hoangtammht-mysql-data || echo "no volume"'

                sh "docker run --name hoangtammht-mysql --rm --network dev -v hoangtammht-mysql-data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_LOGIN_PSW} -e MYSQL_DATABASE=db_example  -d mysql:8.0 "
                sh 'sleep 20'
            }
        }

        stage('Deploy Spring Boot to DEV') {
            steps {
                echo 'Deploying and cleaning'
                sh 'docker image pull hoangtammht/springboot'
                sh 'docker container stop hoangtammht-springboot || echo "this container does not exist" '
                sh 'docker network create dev || echo "this network exists"'
                sh 'echo y | docker container prune '

                sh 'docker container run -d --rm --name hoangtammht-springboot -p 8081:8080 --network dev hoangtammht/springboot'
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
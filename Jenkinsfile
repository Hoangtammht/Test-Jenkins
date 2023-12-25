pipeline{
    agent any
    tools{
        maven 'my-maven'
    }
    stages{
        stage('Build  maven'){
            steps{
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/Hoangtammht/Test-Jenkins']])
                sh 'mvn clean install'
            }
        }
        stage('Build docker image'){
            steps{
                script{
                    sh 'docker build -t hoangtammht/devops-project .'
                }
            }
        }
        stage('Push image to hub'){
            steps{
                script{
                    withCredentials([string(credentialsId: 'dockerhub-pwd', variable: 'dockerhubpwd')]) {
                        sh 'docker login  -u hoangtammht -p ${dockerhubpwd}'
                    }
                    sh 'docker push hoangtammht/devops-project'
                }
            }
        }
        stage('Deploy Spring Boot to DEV') {
                steps {
                    echo 'Deploying and cleaning'
                    sh 'docker image pull hoangtammht/devops-project'
                    sh 'docker container stop devops-project || echo "this container does not exist" '
                    sh 'docker network create dev || echo "this network exists"'
                    sh 'echo y | docker container prune '
                    sh 'docker container run -d --rm --name devops-project -p 8081:8080 --network dev hoangtammht/devops-project'
                }
        }
    }
    post {
        always {
            cleanWs()
        }
    }
}
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
    }
}
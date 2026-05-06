pipeline {
    agent any
    stages {
        stage('Code-pull') {
            steps {
                git branch: 'main', credentialsId: '2b26b3e0-bceb-42f9-9fa2-cf95c1c91672', url: 'https://github.com/rahulgupta-25/jenkins_deployement.git'
            }
        }
        stage('Build') {
            steps {
                sh '''
                    cd FlightReservationApplication
                    mvn clean package 
                '''
            }
        }
        stage('QA-Test') {
            steps {
                withSonarQubeEnv(installationName: 'sonar', credentialsId: 'sonar-token') {
                    sh '''
                        cd FlightReservationApplication
                        mvn sonar:sonar -Dsonar.projectKey=flight-reservation-backend 
                    '''
                
                }
            }
        }
        stage('Docker'){
            steps {
                sh '''
                    cd FlightReservationApplication
                    docker build -t mayurwagh/flight-reservation-pls-19-20:latest . 
                    docker push mayurwagh/flight-reservation-pls-19-20:latest
                    docker rmi mayurwagh/flight-reservation-pls-19-20:latest
                '''
            }
        }
        stage('Deploy') {
            steps {
                sh '''
                    cd FlightReservationApplication
                    kubectl apply -f k8s/deployment.yaml
                    kubectl apply -f k8s/service.yaml
                '''
            }
        }
    }
}

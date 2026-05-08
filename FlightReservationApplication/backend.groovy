pipeline {
    agent any
    stages {
        stage('Code-pull') {
            steps {
                git branch: 'main', credentialsId: 'github-creds', url: 'https://github.com/rahulgupta-25/jenkins_deployement.git'
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
       
        stage('Docker'){
            steps {
                sh '''
                    cd FlightReservationApplication
                    docker build -t rahul25cloud/flight-reservation-pls-19-20:latest . 
                    docker push rahul25cloud/flight-reservation-pls-19-20:latest
                    docker rmi rahul25cloud/flight-reservation-pls-19-20:latest
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

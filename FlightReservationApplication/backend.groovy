pipeline{
    agent any 
    stages{
        stage('Code-pull'){
            steps{
                git branch: 'main', url: 'https://github.com/SakshiP2008/flight-reservation-app.git'
            }
        }
        stage('Code-build'){
            steps{
                sh '''
                cd FlightReservationApplication
                mvn clean package
                '''
            }
        }
        stage('QA-TEST'){
            steps{
                withSonarQubeEnv(installationName:'sonar', credentialsId: 'Sonar-token') {
                    sh '''
                        cd FlightReservationApplication
                        mvn sonar:sonar -Dsonar.projectKey=flight-reservation
                    '''
                }
            }
            
        }
        stage('Docker-build'){
            steps{
                sh '''
                    cd FlightReservationApplication
                    docker build . -t sakshi2008/flightreservation-new:latest
                    docker push sakshi2008/flightreservation-new:latest
                    docker rmi sakshi2008/flightreservation-new:latest
                '''
            }
        }
        stage('Deploy'){
            steps{
                sh '''
                    cd FlightReservationApplication
                    kubectl apply -f k8s/
                '''
            }
        }
    }
}

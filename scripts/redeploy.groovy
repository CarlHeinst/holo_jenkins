pipeline {    
    stages {
        stage('Rerun') {
            steps {
                script {
                  build('loader/setup')
                }
            }
        }
        stage('Approve') {
            steps {
                script {
                  build('Approve')
                }
            }
        }
        stage('Rerun') {
            steps {
                script {
                  build('loader/setup')
                }
            }
        }
    }
}
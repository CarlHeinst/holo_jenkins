pipeline {    
    agent none
    stages {
        stage('Rerun') {
            steps {
                script {
                  catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    build('loader/setup')
                  }
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
        stage('Final Run') {
            steps {
                script {
                  build('loader/setup')
                }
            }
        }
    }
}
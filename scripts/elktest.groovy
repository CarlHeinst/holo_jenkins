pipeline {    
    agent none
    triggers {
      cron('*/5 * * * *')
    }
    stages {
        stage('Rerun') {
            steps {
                script {                  
                  build job:'loader/setup', propagate:false
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

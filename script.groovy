multibranchPipelineJob('my-build') {
    scm {
        git {
          remote {
            url('https://github.com/tknerr/jenkins-pipes-helloworld.git')
          }
          branch('*/master')
        }
    factory {
        workflowBranchProjectFactory {
            scriptPath('Jenkinsfile')
        }
    }
}
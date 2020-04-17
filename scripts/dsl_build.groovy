multibranchPipelineJob('OVT/OVT-CLONE1') {
    branchSources {
        git {
            id = 'admin'
            remote('https://github.com/tknerr/jenkins-pipes-helloworld.git')
        }
    }
    factory {
        workflowBranchProjectFactory {
            scriptPath('Jenkinsfile')
            }
    }
}

multibranchPipelineJob('OVT/OVT-CLONE2') {
    branchSources {
        git {
            id = 'admin'
            remote('https://github.com/tknerr/jenkins-pipes-helloworld.git')
        }
    }
    factory {
        workflowBranchProjectFactory {
            scriptPath('Jenkinsfile')
            }
    }
}

multibranchPipelineJob('REDEPLOY') {
    branchSources {
        git {
            id = 'admin'
            remote('https://github.com/CarlHeinst/holo_jenkins.git')
        }
    }
    factory {
        workflowBranchProjectFactory {
            scriptPath('scripts/redeploy.groovy')
            }
    }
}

folder('crap')
folder('crap1')
folder('crap2')
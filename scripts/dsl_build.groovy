def create_pipeline_from_template(jobname, jenkinspath, repo_url, job_credential) {
  multibranchPipelineJob(jobname) {
    branchSources {
        git {
            id = job_credential
            remote(repo_url)
        }
    }
    factory {
        workflowBranchProjectFactory {
            scriptPath(jenkinspath)
            }
    }
  }
}

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

create_pipeline_from_template('OVT/OVT-CLONE7', 'Jenkinsfile', 'https://github.com/tknerr/jenkins-pipes-helloworld.git', 'admin')
create_pipeline_from_template('OVT/OVT-CLONE8', 'Jenkinsfile', 'https://github.com/tknerr/jenkins-pipes-helloworld.git', 'admin')
create_pipeline_from_template(jobname='OVT/OVT-CLONE9', jenkinspath='Jenkinsfile', repo_url='https://github.com/tknerr/jenkins-pipes-helloworld.git', job_credential='admin')

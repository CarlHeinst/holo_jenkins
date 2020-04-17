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

multibranchPipelineJob('OVT/OVT-CLONE3') {
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

// multibranchPipelineJob('OVT/OVT-CLONE4') {
//     branchSources {
//         git {
//             id = 'admin'
//             remote('https://github.com/tknerr/jenkins-pipes-helloworld.git')
//         }
//     }
//     factory {
//         workflowBranchProjectFactory {
//             scriptPath('Jenkinsfile')
//             }
//     }
// }

// multibranchPipelineJob('OVT/OVT-CLONE5') {
//     branchSources {
//         git {
//             id = 'admin'
//             remote('https://github.com/tknerr/jenkins-pipes-helloworld.git')
//         }
//     }
//     factory {
//         workflowBranchProjectFactory {
//             scriptPath('Jenkinsfile')
//             }
//     }
// }
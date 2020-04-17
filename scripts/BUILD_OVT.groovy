folder('loader')

// multibranchPipelineJob('OVT/OVT-CLONE1') {
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

// multibranchPipelineJob('OVT/OVT-CLONE2') {
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

// multibranchPipelineJob('OVT/OVT-CLONE3') {
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

job('loader/setup') {
    scm {
      git{
        remote{
          url('https://github.com/CarlHeinst/holo_jenkins.git')
          credentials('wba-jenkins-scm')
        }
      }
    }
    triggers {
      //cron('*/1 * * *')
      cron('' + delay.getMinutes() + ',' + second_delay.getMinutes() + ' ' + delay.getHours() + ' ' + delay[Calendar.DAY_OF_MONTH] + ' ' + (delay.getMonth()+1) + ' *' )
    }
    steps {
      dsl {
        external('scripts/BUILD_OVT.groovy')
        removeAction('DELETE')
        ignoreExisting(false)
      }
    }
    configure{ configurationXML ->
      configurationXML / 'builders' / 'javaposse.jobdsl.plugin.ExecuteDslScripts' / sandbox(true)
      //configurationXML / 'builders' / 'javaposse.jobdsl.plugin.ExecuteDslScripts' / ignoreMissingFiles(true)
    }
  }  
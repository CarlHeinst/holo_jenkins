folder('loader')

import groovy.time.TimeCategory
  currentDate = new Date()
  use( TimeCategory ){
    delay = currentDate + 1.minutes
    second_delay = currentDate + 2.minutes
  }

//import jenkins.model.Jenkins
// Jenkins j = Jenkins.instance
// if(!j.isQuietingDown()) {
//     def job_dsl_security = j.getExtensionList('javaposse.jobdsl.plugin.GlobalJobDslSecurityConfiguration')[0]
//     if(job_dsl_security.useScriptSecurity) {
//         job_dsl_security.useScriptSecurity = false
//         println 'Job DSL script security has changed.  It is now disabled.'
//         job_dsl_security.save()
//         j.save()
//     }
//     else {
//         println 'Nothing changed.  Job DSL script security already disabled.'
//     }
// }
// else {
//     println 'Shutdown mode enabled.  Configure Job DSL script security SKIPPED.'
// }

 pipelineJob('DISABLE') {
        definition {
          cps {
            script("""\
              pipeline {
                agent {label 'master'}
                stages {
                  stage ('test') {
                    steps {
                      echo "hello"
                    }
                  }
                }
              }""".stripIndent())
          }
        }
      }

job('loader/setup') {
    scm {
      git{
        remote{
          url('https://github.com/CarlHeinst/holo_jenkins.git')
          credentials('admin')
        }
      }
    }
    triggers {
      //cron('*/1 * * *')
      cron('' + delay.getMinutes() + ',' + second_delay.getMinutes() + ' ' + delay.getHours() + ' ' + delay[Calendar.DAY_OF_MONTH] + ' ' + (delay.getMonth()+1) + ' *' )
    }
    steps {
      dsl {
        external('scripts/dsl_build.groovy')
        removeAction('DELETE')
        ignoreExisting(false)
      }
    }
    //configure{ configurationXML ->
      //configurationXML / 'builders' / 'javaposse.jobdsl.plugin.ExecuteDslScripts' / sandbox(true)
      //configurationXML / 'builders' / 'javaposse.jobdsl.plugin.ExecuteDslScripts' / ignoreMissingFiles(true)
    //}
  }

def approval_script = """import org.jenkinsci.plugins.scriptsecurity.scripts.*
  def scriptApproval = org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval.get()

  String[] signs = [
      "method org.jenkinsci.plugins.workflow.steps.FlowInterruptedException getCauses",
      "method org.jenkinsci.plugins.workflow.support.steps.input.Rejection getUser"
      ]

  for( String sign : signs ) {
      scriptApproval.approveSignature(sign)
  }

  scriptApproval.save()
  toApprove = ScriptApproval.get().getPendingScripts().collect()
  toApprove.each {pending -> ScriptApproval.get().approveScript(pending.getHash())}
"""

job('Approve') {
    triggers {
      //cron('*/1 * * *')
      cron('' + delay.getMinutes() + ',' + second_delay.getMinutes() + ' ' + delay.getHours() + ' ' + delay[Calendar.DAY_OF_MONTH] + ' ' + (delay.getMonth()+1) + ' *' )
    }
    steps {
        //systemGroovyScriptFile('/usr/local/approve.groovy')
        systemGroovyCommand(approval_script)
    }
    configure{ configurationXML ->
      configurationXML / 'builders' / 'hudson.plugins.groovy.SystemGroovy' / 'source' / 'script' / sandbox(true)
    }    
}

queue('loader/setup')
queue('loader/setup')
queue('DISABLE')
queue('DISABLE')
queue('Approve')
queue('Approve')
queue('Approve')

queue('loader/setup')
queue('loader/setup')
queue('DISABLE')
queue('DISABLE')
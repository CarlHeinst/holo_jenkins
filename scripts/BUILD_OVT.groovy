import groovy.time.TimeCategory
  currentDate = new Date()
  use( TimeCategory ){
    delay = currentDate + 1.minutes
    second_delay = currentDate + 4.minutes
  }

folder('loader')
job('loader/setup') {
    scm {
      git{
        remote{
          url('https://github.com/CarlHeinst/holo_jenkins.git')
          name('dsl job')
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
    configure{ configurationXML ->
      configurationXML / 'builders' / 'javaposse.jobdsl.plugin.ExecuteDslScripts' / sandbox(true)
      //configurationXML / 'builders' / 'javaposse.jobdsl.plugin.ExecuteDslScripts' / ignoreMissingFiles(true)
    }
  }

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

queue('DISABLE')
queue('loader/setup')

import org.jenkinsci.plugins.scriptsecurity.scripts.*
toApprove = ScriptApproval.get().getPendingScripts().collect()
toApprove.each {pending -> ScriptApproval.get().approveScript(pending.getHash())}

queue('DISABLE')
queue('loader/setup')

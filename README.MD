# Holo Jenkins

- [Holo Jenkins](#holo-jenkins)
- [Creating Local Jenkins User Accounts](#creating-local-jenkins-user-accounts)
- [Binding your jenkins instance to an active directory](#binding-your-jenkins-instance-to-an-active-directory)
- [Automatically Installing Plugins based on plugins.txt](#automatically-installing-plugins-based-on-pluginstxt)
- [Automatically Configuring a Multibranch pipeline job](#automatically-configuring-a-multibranch-pipeline-job)
- [Automatically configuring a pipeline job](#automatically-configuring-a-pipeline-job)
- [Executing Groovy DSL Scripts to Configure Jobs](#executing-groovy-dsl-scripts-to-configure-jobs)
- [A Discussion on Script Approvals](#a-discussion-on-script-approvals)
  - [Disabled Script Approval - Simple but Unsafe](#disabled-script-approval---simple-but-unsafe)
  - [Creating an Approval Pipeline Job - Better but Still Dangerous.](#creating-an-approval-pipeline-job---better-but-still-dangerous)
  - [Creating an Approval Pipeline Job in a High Integrity Jenkins as Code Environment](#creating-an-approval-pipeline-job-in-a-high-integrity-jenkins-as-code-environment)
- [Jenkins DSL Job to configure the Entire Instance](#jenkins-dsl-job-to-configure-the-entire-instance)
  - [Having an Approval Job](#having-an-approval-job)
  - [REDEPLOY Job](#redeploy-job)
- [Appendix i - Even more DSL](#appendix-i---even-more-dsl)

To test clone down the repo, then execute:

```bash
docker container rm holo-jenk -f
docker build -t holo-jenkins .
docker run -d -p 8080:8080 --env-file env.file --name=holo-jenk holo-jenkins
```

This will create a new instance of jenkins which has been prepopulated with the super insecure "admin" "admin" credentials.

It Provides Examples of the following items within it's conf.yaml

# Creating Local Jenkins User Accounts

> This section in the config creates the local account "admin":

  ```yaml
  securityRealm:
      local:
        allowsSignup: false
        users:
        - id: "admin"          
          password: "${ADMIN_PASSWORD}"
  ```

> The environment variable ADMIN_PASSWORD is used to supply the password. 
>
> In this highly insecure example this environment variable is populated from the env.file located within the repository.
>
> Ideally this file would be located in another location and not held within source control.

# Binding your jenkins instance to an active directory

This section within [conf.yaml](conf.yaml) is commented out, as otherwise it would interfere with the deployment of holo jenkins.

However when uncommented and appropriate elements updated it would allow your Jenkins instance to bind to active directory.

You would then be able to assign roles to users based on Active Directory group membership:

  ```yaml
  securityRealm:
  activeDirectory:
    bindPassword: "${BIND}"
    customDomain: true
    domains:
    - bindName: "CN=YOURACCOUNT.OU=YOUROU,DC=YOURDOMAIN,DC=com"
      bindPassword: "${BIND}"
      name: "YOURDOMAIN.COM"
      servers: "YOURDOMAINCONTROLLER.YOURDOMAIN.COM"
      site: "YOUR AD SITE"
      tlsConfiguration: TRUST_ALL_CERTIFICATES
    groupLookupStrategy: AUTO
    removeIrrelevantGroups: false
    startTks: true
  ```

> The environment variable BIND is used to supply the password for the AD account to bind Jenkins to AD. 
>
> In this highly insecure example this environment variable is populated from the env.file located within the repository.
>
> Ideally this file would be located in another location and not held within source control.

# Automatically Installing Plugins based on plugins.txt

By leveraging the install-plugins.sh file which is included in the Jenkins dockers image, it is possible to specify the plugins for Jenkins to install.

It is also possible to specify the version of the plugin by appending it to the end of the plugin name seperated by a :

  ```bash
  credentials:2.3.7
  ldap:1.22
  ```

# Automatically Configuring a Multibranch pipeline job

The following example within the [conf.yaml](conf.yaml) sets up a multibranch pipeline job when Jenkins starts:

  ```yaml
  jobs:
  - script: >
      multibranchPipelineJob('OVT/OVT-JENKFILE') {
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
  ```

The id field should point to a valid credential as specified elsewhere in [conf.yaml](conf.yaml).

# Automatically configuring a pipeline job

# Executing Groovy DSL Scripts to Configure Jobs

The following example within the [conf.yaml](conf.yaml) demonstrates how you can execute a groovy script which contains Jenkins DSL elements to configure your instance.

This execellent [resource](https://jenkinsci.github.io/job-dsl-plugin/) provides more detailed information about Jenkins DSL syntax.

  ```yaml
  jobs:
  - file: /usr/local/BUILD_OVT.groovy
  - script: queue('OVT/OVT-CLONE1')
  - script: queue('OVT/OVT-CLONE2')
  - script: queue('OVT/OVT-CLONE3')
  ```

Once the groovy script has defined the jobs using the script: queue() method orders Jenkins to execute them at launch.

# A Discussion on Script Approvals

One of the challenges with Jenkins is for it to be able to configure itself in an automated fashion without leaving numerous security holes exposed.

## Disabled Script Approval - Simple but Unsafe

One VERY insecure method for achieving this goal would be to disable script approvals in Jenkins, which would mean that you can execute your DSL scripts to configure your instance.

On the plus side all of your scripts will now work in your instance without needing to be approved. On the negative side so will the scripts of any nefarious person who manages to gain access to your instance, or even a script that accidentally causes damage to your instance.

## Creating an Approval Pipeline Job - Better but Still Dangerous.

A slightly better approach to disabling script approval altogether is to wrap a multibranch pipeline around the approval process.

The pipeline would carry out three tasks:

- Execute the DSL configuration job - which will fail, but will register a request for approval.
- Run the Approval Job, which will approve the pending request.
- Run the DSL configuration job again - which will not execute successfully as it has been approved.

> There remain downsides to this approach, because the approval job, approves any outstanding scripts, meaning that nefarious scripts might find their way in.

## Creating an Approval Pipeline Job in a High Integrity Jenkins as Code Environment 

One way to enhance the previous suggestion is to for your jenkins environment to be stored as code. Any changes to the configuration would then have to go through some form of code review via Pull Request.

Because your entire instance is in code and designed to configure itself, you can stand up your instance but remove the need for their to be administrators upon it.

Any changes to the configuration will either result in the instance being torn down and redeployed from code, or an updated version of the DSL job script running and updating the instances config.

End users can no longer execute nefarious scripts unless their co workers are conspiring with them...


# Jenkins DSL Job to configure the Entire Instance

In [conf.yaml](conf.yaml) we see this line:

  ```yaml
  - file: "/usr/local/BUILD_OVT.groovy" 
  ```

- This causes the groovy script BUILD_OVT.groovy to be executed on your instance as it launches

- Within BUILD_OVT.groovy is this job configuration:

  ```groovy

  folder('loader')

  import groovy.time.TimeCategory
    currentDate = new Date()
    use( TimeCategory ){
      delay = currentDate + 1.minutes
      second_delay = currentDate + 2.minutes
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
      cron('' + delay.getMinutes() + ',' + second_delay.getMinutes() + ' ' + delay.getHours() + ' ' + delay[Calendar.DAY_OF_MONTH] + ' ' + (delay.getMonth()+1) + ' *' )
    }
    steps {
      dsl {
        external('scripts/dsl_build.groovy')
        removeAction('DELETE')
        ignoreExisting(false)
      }
    }
  }
  ```

- This script performs the following:
  - Creates a folder called `loader`
  - Generates a variable capturing the current time.
  - Creates a job called `loader/setup` which will
    - Go to github and run the DSL script `scripts/dsl_build.groovy`
    - Be configured by cron to trigger 1 minute after the script launches.
    - Be configured by cron to trigger 2 minutes after the script launches.

> The multiple executions are necessary as the first time the job runs it will fail due to the methods it wishes to call not being approved.
>
> However another job called `APPROVE` will run after it's initial execution, which will mark `scripts/dsl_build.groovy` as approved allowing it to work the second time.

## Having an Approval Job

When loader above first tries to run it will fail as it is not authorised to execute the DSL commands it contains.

Due to the way that Jenkins approves scripts for operation we can not simple provide a list of filenames as "good" scripts, instead we must approve them.

The Approval job is split into two parts, the approval script itself and the job which runs it:

```groovy
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
      cron('' + delay.getMinutes() + ',' + second_delay.getMinutes() + ' ' + delay.getHours() + ' ' + delay[Calendar.DAY_OF_MONTH] + ' ' + (delay.getMonth()+1) + ' *' )
    }
    steps {        
        systemGroovyCommand(approval_script)
    }
    configure{ configurationXML ->
      configurationXML / 'builders' / 'hudson.plugins.groovy.SystemGroovy' / 'source' / 'script' / sandbox(true)
    }    
}
```

> Preauthorising the powerful methods utilised by the approval script is potentially dangerous. However the intention is that this instance will be entirely managed from source control, with no one permitted to be an administrator of it.

> Should the instance fail it would be rebuilt.

An interesting example can be seen where the job DSL is making use of the configurationXML method. This can be coupled with the config.xml of a Jenkins job to understand how to configure additional settings beyond those directly available in Jenkins DSL.

You will also note the use again of custom CRON timers to ensure that the approval job runs automatically shortly after the instance launches.

## REDEPLOY Job

Now we assemble our elements into the redploy job. Redeploy is to be used for when your instance had been deployed but some configuration changes are made to our dsl_build.groovy file within our source control systems.

You would then run the redeploy job, triggering the following events:

- `loader/setup` job runs, it's hash value is compared against what Jenkins has on record and has changed - Jenkins refuses to allow the job to run.
  - The redeploy job has been configured to ignore this failure and continue with it's next step.
- `Approve` is trigggered, updating the hash value for `loader/setup`
- `loader/setup` is run a second time and as it has now been authorised, it executes and updates the configuration of your instance accordingly.

# Appendix i - Even more DSL

Jenkins DSL scripts allow us to dictate the configuration of our Jenkins jobs. Life is far too short for clicking through a GUI to deploy a new job, especially when 90% of the configuration within that job is the same as dozens of others you've already created:

```groovy
multibranchPipelineJob('Name of New Job') {
    branchSources {
        git {
            id = 'admin'
            remote('https://github.com/PlaceYourRpoLives.git')
        }
    }
    factory {
        workflowBranchProjectFactory {
            scriptPath('path_to_jenkinsfile')
            }
    }
}
```

By filling a groovy script with entries like the one above think how much more quickly you can deploy new jobs to Jenkins, knowing that they will be consistent with one another. 

You could even template this configuration using a function for even more minimal code:

```groovy
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
create_pipeline_from_template('OVT/OVT-CLONE7', 'Jenkinsfile', 'https://github.com/tknerr/jenkins-pipes-helloworld.git', 'admin')
create_pipeline_from_template('OVT/OVT-CLONE8', 'Jenkinsfile', 'https://github.com/tknerr/jenkins-pipes-helloworld.git', 'admin')
create_pipeline_from_template('OVT/OVT-CLONE9'by alm, 'Jenkinsfile', 'https://github.com/tknerr/jenkins-pipes-helloworld.git', 'admin')
```
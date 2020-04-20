# Holo Jenkins

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

## Having a REDEPLOY Job that tries to redeploy, fails, approves and succeeds
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

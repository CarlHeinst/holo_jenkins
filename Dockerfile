##FROM jenkins/jenkins:lts-alpine
FROM jenkins/jenkins:lts

ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"
ENV JENKINS_UC="https://updates.jenkins.io"
ENV JENKINS_UC_DOWNLOAD="${JENKINS_UC}/download"

COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
## Configure casc to use the uploaded YAML file.
ENV CASC_JENKINS_CONFIG="/usr/local/conf.yaml"
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt

## Copy your DSL script into the correct location
add ./scripts/BUILD_OVT.groovy /usr/local/BUILD_OVT.groovy
## Copy your CASC yaml file into the correct location
add conf.yaml /usr/local/conf.yaml
## This file can then be called for execution via conf.yaml

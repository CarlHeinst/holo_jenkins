FROM jenkins/jenkins:lts-alpine

ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"
ENV JENKINS_UC="https://updates.jenkins.io"
ENV JENKINS_UC_DOWNLOAD="${JENKINS_UC}/download"

COPY security.groovy /usr/share/jenkins/ref/init.groovy.d/security.groovy

COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt

COPY conf.yaml /usr/local/conf.yaml
ENV CASC_JENKINS_CONFIG="/usr/local/conf.yaml"
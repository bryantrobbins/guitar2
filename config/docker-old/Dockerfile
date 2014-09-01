FROM ubuntu:14.04

RUN echo "1.576" > .lts-version-number

RUN apt-get update && apt-get install -y wget curl zip
RUN apt-get update && apt-get install -y --no-install-recommends openjdk-7-jdk
RUN apt-get update && apt-get install -y git maven ant

# Install groovy
# lines from nacyot/groovy-groovy
RUN wget -O /opt/groovy.zip http://dl.bintray.com/groovy/maven/groovy-binary-2.3.3.zip
WORKDIR /opt
RUN unzip groovy.zip
RUN mv groovy-2.3.3 groovy
RUN bash -c "ln -s /opt/groovy/bin/{grape,groovy,groovyConsole,groovyc,groovydoc,groovysh,java2groovy,startGroovy} /usr/local/bin/"
WORKDIR /

RUN wget -q -O - http://pkg.jenkins-ci.org/debian-stable/jenkins-ci.org.key | sudo apt-key add -
RUN echo deb http://pkg.jenkins-ci.org/debian-stable binary/ >> /etc/apt/sources.list
RUN apt-get update && apt-get install -y jenkins
#RUN mkdir -p /var/jenkins_home && chown -R jenkins /var/jenkins_home
RUN mkdir -p /var/jenkins_home

VOLUME /var/jenkins_home
ENV JENKINS_HOME /var/jenkins_home

# define url prefix for running jenkins behind Apache (https://wiki.jenkins-ci.org/display/JENKINS/Running+Jenkins+behind+Apache)
ENV JENKINS_PREFIX /

# Run bootstrap script
RUN mkdir -p /var/bootstrap
ADD bootstrap.groovy /var/bootstrap/bootstrap.groovy
RUN groovy /var/bootstrap/bootstrap.groovy

# Put init.groovy script in place
ADD init.groovy /tmp/WEB-INF/init.groovy
RUN cd /tmp && zip -g /usr/share/jenkins/jenkins.war WEB-INF/init.groovy
ADD ./jenkins.sh /usr/local/bin/jenkins.sh
RUN chmod +x /usr/local/bin/jenkins.sh
#USER jenkins 

# for main web interface:
EXPOSE 8080 

# will be used by attached slave agents:
EXPOSE 50000 

CMD ["/usr/local/bin/jenkins.sh"]

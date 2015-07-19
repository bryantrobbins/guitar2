FROM ubuntu:14.04
MAINTAINER Bryan Robbins <bryantrobbins@gmail.com>

# Get latest packages
RUN apt-get update && apt-get clean

# Needed for Jenkins
RUN apt-get install --no-install-recommends openjdk-7-jdk -y

# Needed for Jenkins jobs
RUN apt-get install ant wget unzip bzip2 xvfb git -y

# Needed for R
RUN apt-get install r-base r-base-dev -y

# Install Gradle
COPY gradle-2.1-bin.zip /gradle-2.1-bin.zip
RUN unzip /gradle-2.1-bin.zip
RUN mv /gradle-2.1 /gradle
ENV PATH $PATH:/gradle/bin

RUN mkdir jslave
RUN mkdir jslave/home
ADD swarm.jar /jslave/swarm.jar

EXPOSE 22
EXPOSE 5005

WORKDIR jslave
CMD    ["java", "-jar", "swarm.jar", "-master", "http://guitar05.cs.umd.edu:8888", "-username", "admin", "-password", "amalga84go", "-labels", "basic R", "-executors", "1", "-fsroot", "/jslave/home"]

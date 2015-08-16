FROM ubuntu:14.04
MAINTAINER Bryan Robbins <bryantrobbins@gmail.com>

# Get latest packages
RUN echo "deb http://lib.stat.cmu.edu/R/CRAN/bin/linux/ubuntu trusty/" >> /etc/apt/sources.list
RUN apt-key adv --keyserver keyserver.ubuntu.com --recv-keys E084DAB9
RUN apt-get update && apt-get clean

# Needed for Jenkins
RUN apt-get install --no-install-recommends openjdk-7-jdk -y

# Needed for Jenkins jobs
RUN apt-get install ant wget unzip bzip2 xvfb git -y

# Needed for R
RUN apt-get install r-base r-base-dev libxml2-dev libcurl3-dev -y
COPY install.r /install.r
RUN Rscript install.r

# Install Gradle
COPY gradle-2.1-bin.zip /gradle-2.1-bin.zip
RUN unzip /gradle-2.1-bin.zip
RUN mv /gradle-2.1 /gradle
ENV PATH $PATH:/gradle/bin

# Set up slave files and user
RUN mkdir jslave
RUN mkdir jslave/home
ADD swarm.jar /jslave/swarm.jar
RUN groupadd -r jslave && useradd -r -g jslave jslave
RUN chown -R jslave:jslave /jslave

EXPOSE 22
EXPOSE 5005

USER jslave
WORKDIR jslave
CMD    ["java", "-jar", "swarm.jar", "-master", "http://guitar05.cs.umd.edu:8888", "-username", "admin", "-password", "amalga84go", "-labels", "basic R", "-executors", "1", "-fsroot", "/jslave/home"]

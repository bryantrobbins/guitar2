FROM ubuntu:12.04
MAINTAINER Bryan Robbins <bryantrobbins@gmail.com>

RUN apt-get update && apt-get clean
RUN apt-get install --no-install-recommends openjdk-7-jdk -y
RUN apt-get install ant wget bunzip2 -y

RUN mkdir jslave
RUN mkdir jslave/home
ADD swarm.jar /jslave/swarm.jar

EXPOSE 22
WORKDIR jslave
CMD    ["java", "-Xmx256m", "-jar", "swarm.jar", "-master", "http://jenkins:8080", "-username", "admin", "-password", "amalga84go", "-labels", "basic", "-executors", "1", "-fsroot", "/jslave/home"]

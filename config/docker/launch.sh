#!/bin/bash

docker run --name myjenkins -p 8080:8080 -v /var/jenkins_home jenkins:latest


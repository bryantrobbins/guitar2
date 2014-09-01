#!/bin/bash

export DOCKER_HOST=$(/usr/local/bin/boot2docker ip 2>/dev/null):2375
docker rmi bryantrobbins/jenkins
docker build -t bryantrobbins/jenkins .

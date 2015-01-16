#/bin/bash

docker ps -a | awk '{print $1,$2}' | grep "bryantrobbins/jslave" | awk '{print $1}' | xargs docker stop
docker ps -a | awk '{print $1,$2}' | grep "bryantrobbins/jslave" | awk '{print $1}' | xargs docker rm
docker ps -a | awk '{print $1,$2}' | grep "bryantrobbins/jslave-linked" | awk '{print $1}' | xargs docker stop
docker ps -a | awk '{print $1,$2}' | grep "bryantrobbins/jslave-linked" | awk '{print $1}' | xargs docker rm

# Build images for jenkins slaves
docker rmi bryantrobbins/jslave
docker build -t="bryantrobbins/jslave" ./slave

docker rmi bryantrobbins/jslave-linked
docker build -t="bryantrobbins/jslave-linked" ./slave-linked

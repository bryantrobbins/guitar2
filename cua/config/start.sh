#/bin/bash

nslave=$1

if [ -z "$nslave" ]; then
  echo "Number of jenkins slaves not provided"
  echo "Usage: $0 numberOfSlaves"
  exit
fi
 
# Build image for jenkins slaves
docker ps -a | awk '{print $1,$2}' | grep "bryantrobbins/jslave" | awk '{print $1}' | xargs docker stop
docker ps -a | awk '{print $1,$2}' | grep "bryantrobbins/jslave" | awk '{print $1}' | xargs docker rm
docker rmi bryantrobbins/jslave

docker build -t="bryantrobbins/jslave" ./slave

# Start cuanexus
docker stop cuanexus
docker rm cuanexus
docker run -d --name cuanexus -h cuanexus --volumes-from cuadata -p 9081:8081 conceptnotfound/sonatype-nexus

# Start cuamongo
docker stop cuamongo
docker rm cuamongo
docker run -d --name cuamongo --volumes-from cuadata -p 37017:27017 -p 38017:28017 dockerfile/mongodb mongod --rest --httpinterface
 
# Start cuajenkins
docker stop cuajenkins
docker rm cuajenkins
docker run -d --name cuajenkins --volumes-from cuadata --link cuanexus:cuanexus --link cuamongo:cuamongo -p 9080:8080 -u root jenkins

# Start some jenkins slaves
for i in `seq 1 $nslave`;
do
  echo $i
  docker stop cuaslave-$i
  docker rm cuaslave-$i
  docker run -d --name cuaslave-$i --link cuajenkins:cuajenkins --link cuanexus:cuanexus --link cuamongo:cuamongo bryantrobbins/jslave
done

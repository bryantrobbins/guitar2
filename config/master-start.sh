#/bin/bash
nslave=$1

if [ -z "$nslave" ]; then
  echo "Number of jenkins slaves not provided"
  echo "Usage: $0 numberOfSlaves"
  exit
fi

# Start nexus
docker stop nexus
docker rm nexus
docker run -d --name nexus -h nexus --volumes-from cuadata -p 9081:8081 conceptnotfound/sonatype-nexus

# Start mongo
docker stop mongo
docker rm mongo
docker run -d --name mongo --volumes-from cuadata -p 37017:27017 -p 38017:28017 dockerfile/mongodb mongod --rest --httpinterface
 
# Start jenkins
docker stop jenkins
docker rm jenkins
docker run -d --name jenkins --volumes-from cuadata --link nexus:nexus --link mongo:mongo -p 9080:8080 -u root jenkins

# Build image for jenkins slaves
docker ps -a | awk '{print $1,$2}' | grep "bryantrobbins/jslave-linked" | awk '{print $1}' | xargs docker stop
docker ps -a | awk '{print $1,$2}' | grep "bryantrobbins/jslave-linked" | awk '{print $1}' | xargs docker rm

# Start some jenkins slaves
for i in `seq 1 $nslave`;
do
  echo $i
  docker run -d --volumes-from cuadata -P --name slave-$i bryantrobbins/jslave-linked
done

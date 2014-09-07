#/bin/bash
 
# Start cuanexus
docker stop cuanexus
docker rm cuanexus
docker run -d --name cuanexus --volumes-from cuadata -p 9081:8081 conceptnotfound/sonatype-nexus

# Start cuamongo
docker stop cuamongo
docker rm cuamongo
docker run -d --name cuamongo --volumes-from cuadata -p 37017:27017 -p 38017:28017 dockerfile/mongodb mongod --rest --httpinterface
 
# Start cuajenkins
docker stop cuajenkins
docker rm cuajenkins
docker run -d --name cuajenkins --volumes-from cuadata --link cuanexus:cuanexus --link cuamongo:cuamongo -p 9080:8080 -u root jenkins

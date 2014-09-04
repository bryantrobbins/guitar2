#/bin/bash
 
# Start cuacuacuanexus
docker stop cuanexus
docker rm cuanexus
docker run -d --name cuanexus --volumes-from cuadata -p 9081:8081 conceptnotfound/sonatype-nexus
 
# Start cuacuacuacuacuacuacuacuajenkins
docker stop cuajenkins
docker rm cuajenkins
docker run -d --name cuajenkins --volumes-from cuadata --link cuanexus:cuanexus -p 9080:8080 -u root jenkins

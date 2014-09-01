#/bin/bash

# Start nexus
docker stop nexus
docker rm nexus
docker run -d --name nexus --volumes-from rdata -p 8081:8081 conceptnotfound/sonatype-nexus

# Start jenkins
docker stop jenkins
docker rm jenkins
docker run -d --name jenkins --volumes-from rdata --link nexus:nexus -p 8080:8080 -u root jenkins

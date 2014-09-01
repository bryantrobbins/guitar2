#/bin/bash

# Export data from previous container
echo "Exporting data from previous rdata container"
docker stop rdata
docker rm backup
docker run --name backup --volumes-from rdata -v $(pwd):/backup busybox tar cvf /backup/backup.tar var/jenkins_home data/db nexus
docker rm rdata

# Start new data container
echo "Starting new  rdata container"
docker run -d --name rdata -v /var/jenkins_home -v /data/db -v /nexus busybox true

# Copy in files from old data container
echo "Restoring old files into new rdata"
docker rm restore
docker run --name restore --volumes-from rdata -v $(pwd):/backup busybox tar xvf /backup/backup.tar

# Start jenkins
docker stop jenkins
docker rm jenkins
docker run -d --name jenkins --volumes-from rdata -p 8080:8080 -u root jenkins

# Start nexus
docker stop nexus
docker rm nexus
docker run -d --name nexus --volumes-from rdata -p 8081:8081 conceptnotfound/sonatype-nexus

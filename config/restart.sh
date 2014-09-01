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

./start.sh

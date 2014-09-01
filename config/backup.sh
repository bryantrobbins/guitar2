#/bin/bash

local=`pwd`

echo "Exporting to $local"

# Export data from previous container
echo "Exporting data current rdata container"
docker rm backup
docker run --name backup --volumes-from rdata -v $local:/backup busybox tar cvf /backup/backup.tar var/jenkins_home data/db nexus
docker rm backup

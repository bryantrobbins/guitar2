#/bin/bash
 
now=`date +%s`
archive="backup.slave.$now.tar"
 
local=`pwd`
echo "Backing up to $archive"
 
echo "Exporting data current cuadata container"
docker rm backup
docker run --name backup --volumes-from cuadata -v $local:/backup busybox tar cvf /backup/$archive sources
docker rm backup

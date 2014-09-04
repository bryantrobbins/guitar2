#/bin/bash
 
archive=$1
 
if [ -z "$archive" ]; then
  echo "No archive provided."
  echo "Usage: $0 archive.tar"
  exit
fi
 
if [ -e "$archive" ]; then
  echo "File $archive already exists. Please choose another name"
  exit
fi
 
local=`pwd`
echo "Backing up to $archive"
 
echo "Exporting data current cuadata container"
docker rm backup
docker run --name backup --volumes-from cuadata -v $local:/backup busybox tar cvf /backup/$archive var/jenkins_home nexus
docker rm backup

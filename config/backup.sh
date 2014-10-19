#/bin/bash
 
archive=$1
 
if [ -z "$archive" ]; then
  now=`date +%s`
  archive="backup.$now.tar"
  echo "No archive provided. Using default of $archive"
fi
 
if [ -e "$archive" ]; then
  echo "File $archive already exists. Please choose another name"
  exit
fi
 
local=`pwd`
echo "Backing up to $archive"
 
echo "Exporting data current cuadata container"
docker rm backup
docker run --name backup --volumes-from cuadata -v $local:/backup busybox tar cvf /backup/$archive var/jenkins_home nexus db/data sources
docker rm backup

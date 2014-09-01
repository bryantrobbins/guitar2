#/bin/bash

archive=$1

if [ -z "$archive" ]; then
  echo "No archive provided."
  echo "Usage: $0 archive.tar"
  exit
fi

echo "Loading from $archive"

# Clean up any old stuff here
docker rm rdata

# Start new data container
echo "Starting new  rdata container"
docker run -d --name rdata -v /var/jenkins_home -v /data/db -v /nexus busybox true

# Copy in files from old data container
echo "Restoring old files into new rdata"
docker rm restore
docker run --name restore --volumes-from rdata -v $(pwd):/backup busybox tar xvf /backup/$archive

./start.sh

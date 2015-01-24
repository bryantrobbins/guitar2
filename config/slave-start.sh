#/bin/bash

archive=$1

if [ -z "$archive" ]; then
  echo "No archive provided."
  echo "Usage: $0 archive.tar numberOfSlaves"
  exit
fi

nslave=$2

if [ -z "$nslave" ]; then
  echo "Number of jenkins slaves not provided"
  echo "Usage: $0 archive.tar numberOfSlaves"
  exit
fi

echo "Restoring from $archive"

./load-slave.sh $archive

# Stop existing slaves
docker ps -a | awk '{print $1,$2}' | grep "bryantrobbins/jslave" | awk '{print $1}' | xargs docker stop
docker ps -a | awk '{print $1,$2}' | grep "bryantrobbins/jslave" | awk '{print $1}' | xargs docker rm

# Build image for slaves
docker rmi bryantrobbins/jslave
docker build -t="bryantrobbins/jslave" ./slave

# Start some slaves
for i in `seq 1 $nslave`;
do
  echo $i
  docker run -d --volumes-from cuadata -P --name slave-$i bryantrobbins/jslave
done

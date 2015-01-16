#/bin/bash

nslave=$1

if [ -z "$nslave" ]; then
  echo "Number of jenkins slaves not provided"
  echo "Usage: $0 numberOfSlaves"
  exit
fi
 
# Start some jenkins slaves
for i in `seq 1 $nslave`;
do
  echo $i
  docker run -d --volumes-from cuadata -P --name slave-$i bryantrobbins/jslave
done

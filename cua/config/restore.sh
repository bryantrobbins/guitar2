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
 
./load.sh $archive
./start.sh $nslave

#/bin/bash

archive=$1

if [ -z "$archive" ]; then
  echo "No archive provided."
  echo "Usage: $0 archive.tar"
  exit
fi

echo "Restoring from $archive"

./load.sh $archive
./start.sh

#/bin/bash
 
nslave=$1

if [ -z "$nslave" ]; then
  echo "Number of jenkins slaves not provided"
  echo "Usage: $0 numberOfSlaves"
  exit
fi

now=`date +%s`
arch="backup.$now.tar"
 
./backup.sh $arch
./restore.sh $arch $nslave

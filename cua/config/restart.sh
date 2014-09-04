#/bin/bash
 
now=`date +%s`
arch="backup.$now.tar"
 
./backup.sh $arch
./load.sh $arch
rm $arch
./start.sh

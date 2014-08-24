#!/bin/bash
if [ $# -lt 1 ]
then
	echo "Usage: cobertura-report-name.sh <ser_file> [destination]"
	exit
fi 

ser_file=$1
ser_name=`basename "$ser_file"`
ser_name=${ser_name%.*}

if [ -z $2 ]
then
	destination=`dirname $ser_file`
else
	destination=$2
fi

java -cp `dirname $0`/cobertura.jar:`dirname $0`/lib/asm-3.0.jar:`dirname $0`/lib/asm-tree-3.0.jar:`dirname $0`/lib/log4j-1.2.9.jar:`dirname $0`/lib/jakarta-oro-2.0.8.jar net.sourceforge.cobertura.reporting.Main --destination $destination --format extXmlBranch --datafile $ser_file

mv -f $destination/coverage.xml $destination/$ser_name.xml



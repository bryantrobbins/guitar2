#!/bin/bash 

#--------------------------------------
# Check out Gephi
#
#	By	baonn@cs.umd.edu
#	Date: 	07/12/2011
#--------------------------------------

script_root_dir=`dirname $0`
source "$script_root_dir/tools.cfg"

echo "*** Checking out Gephi ***"

pushd $gephi_dir
if [ $? -ne 0 ]
then
   echo FAILED $gephi_dir not found
   exit 1
fi

retry=2
while [ $retry -ne 0 ]
do
   retry=$(($retry-1))
   exec_cmd "sleep $((RANDOM%32))"
   exec_cmd "curl -L http://launchpad.net/gephi/toolkit/toolkit-0.8.2234/+download/gephi-toolkit-0.8.2234-all.zip -o gephi-toolkit-0.8.2234-all.zip"
   ret=$?

   if [ $ret -ne 0 ] && [ $retry -eq 0 ]
   then
      echo FAILED Checkout $ret no more retry
   fi

   if [ $ret -ne 0 ] && [ $retry -ne 0 ]
   then
      echo FAILED Checkout $ret Retrying $retry times
   fi
done

if [ $ret -eq 0 ]
then
   exec_cmd "unzip *.zip"
   exec_cmd "mv gephi-toolkit-0.8.2234-all/*.jar ."
   exec_cmd "rm -rf gephi-toolkit-0.8.2234-all*"
fi

echo "*** DONE Checking out Gephi ***"

popd
exit $ret

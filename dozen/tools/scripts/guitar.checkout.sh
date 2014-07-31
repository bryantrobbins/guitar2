#!/bin/bash 

#--------------------------------------
# Check out GUITAR 
#
#	By	baonn@cs.umd.edu
#	Date: 	07/12/2011
#--------------------------------------

script_root_dir=`dirname $0`
source "$script_root_dir/tools.cfg"
source "$script_root_dir/utils.sh"

#------------------
# Parameter check
#------------------
if [ -z $1 ]
then
    echo "Usage: $0 <guitar-revision>"
    exit 1
fi
guitar_revision=$1

echo "*** Checking out GUITAR $guitar_revision from sourceforge ***"

retry=2
while [ $retry -ne 0 ]
do
   retry=$(($retry-1))
   s=$(($RANDOM%128))
   echo "Waiting random $s seconds"
#   exec_cmd "sleep $s"
   exec_cmd "svn co --non-interactive --trust-server-cert -r $guitar_revision svn://svn.code.sf.net/p/guitar/code/trunk/ $guitar_dir"
   ret=$?

   if [ $ret -ne 0 ] && [ $retry -eq 0 ]
   then
      echo FAILED Checkout $ret no more retry
   fi

   if [ $ret -ne 0 ] && [ $retry -ne 0 ]
   then
      echo FAILED Checkout $ret Retrying $retry times
      exec_cmd "svn cleanup"
   fi

   if [ $ret -eq 0 ]
   then
      break
   fi
done

echo "*** DONE Checking out GUITAR $guitar_revision from sourceforge ***"

exit $ret

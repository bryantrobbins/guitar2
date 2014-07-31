#!/bin/bash 

#--------------------------------------
# Main script to install all tools  
#
#	By	baonn@cs.umd.edu
#	Date: 	06/08/2011
#--------------------------------------

script_root_dir=`dirname $0`

#
# check out cobertura and necessary scripts 
#
$script_root_dir/cobertura.checkout.sh
ret=$?
if [ $ret -ne 0 ]
then
   echo FAILED checkout cobertura $ret
   exit 1
fi

#
# build cobertura 
#
$script_root_dir/cobertura.build.sh
ret=$?
if [ $ret -ne 0 ]
then
   echo FAILED build cobertura $ret
   exit 1
fi

exit 0

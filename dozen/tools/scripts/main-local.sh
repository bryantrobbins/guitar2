#!/bin/bash 

#--------------------------------------
# Main script to install all tools  
#
#	By	baonn@cs.umd.edu
#	Date: 	06/08/2011
#--------------------------------------

script_root_dir=`dirname $0`

echo "*** Checking out and installing tools ***"

#
# Initialize a standard directory layout
#
$script_root_dir/init-dir.sh
ret=$?
if [ $ret -ne 0 ]
then
   echo FAILED init dir $ret
   exit 1
fi

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

#
# check out Gephi jars
#
$script_root_dir/gephi.checkout.sh
ret=$?
if [ $ret -ne 0 ]
then
   echo FAILED checkout Gephi $ret
   exit 1
fi

#
# build Gephi converter
#
$script_root_dir/gephi.build.sh
ret=$?
if [ $ret -ne 0 ]
then
   echo FAILED build Gephi $ret
   exit 1
fi

#
# check out data analysis scripts 
#
$script_root_dir/script.checkout.sh
ret=$?
if [ $ret -ne 0 ]
then
   echo FAILED checkout data analysis scripts $ret
   exit 1
fi

echo "*** DONE Checking out and installing tools ***"

exit 0

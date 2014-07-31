#!/bin/bash 

#--------------------------------------
# Check out GUITAR 
#
#	By	baonn@cs.umd.edu
#	Date: 	06/08/2011
#--------------------------------------

script_root_dir=`dirname $0`
source "$script_root_dir/tools.cfg"
source "$script_root_dir/utils.sh"

echo "*** Building cobertura ***"

pushd $cobertura_dir

cmd="ant jar"
echo $cmd
$cmd 
ret=$?
if [ $ret -ne 0 ]
then
   echo FAILED $ret
   exit 1
fi

cmd="ant war"
echo $cmd
$cmd
ret=$?
if [ $ret -ne 0 ]
then
   echo FAILED $ret
   exit 1
fi

popd  

echo "*** DONE Building cobertura ***"

exit 0

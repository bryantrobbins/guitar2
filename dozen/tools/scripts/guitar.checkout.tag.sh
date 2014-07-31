#!/bin/bash 

#--------------------------------------
# Check out tagged version of GUITAR 
#
#	By	baonn@cs.umd.edu, brobbins@cs.umd.edu
#	Date: 	Revised 1/16/12 (btr)
#--------------------------------------

script_root_dir=`dirname $0`
source "$script_root_dir/tools.cfg"
source "$script_root_dir/utils.sh"

#------------------
# Parameter check
#------------------
if [ -z $1 ]
then
    echo "Usage: $0 <guitar-tag>"
    exit 1
fi
tag=$1

echo "*** Checking out GUITAR revision with tag $tag from sourceforge ***"

exec_cmd "svn co --non-interactive --trust-server-cert svn://svn.code.sf.net/p/guitar/code/trunk/$tag  $guitar_dir"
ret=$?
if [ $ret -ne 0 ]
then
   echo FAILED Checkout $ret
fi

echo "*** DONE Checking out GUITAR revision with tag $tag from sourceforge ***"

exit $ret

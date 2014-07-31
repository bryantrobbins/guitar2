#!/bin/bash 

#--------------------------------------
# Check out data analysis scripts 
#
#	By	baonn@cs.umd.edu
#	Date: 	06/08/2011
#--------------------------------------

script_root_dir=`dirname $0`
source "$script_root_dir/tools.cfg"
source "$script_root_dir/utils.sh"

b_mkdir $script_dir

echo "*** Checking out data analysis scripts from cs.umd.edu ***"

pushd $script_dir 

svn co --non-interactive --trust-server-cert --username sv-guitar --password ''  https://svn.cs.umd.edu/repos/guitar/data-analysis $script_dir
ret=$?

echo "*** DONE Checking out data analysis scripts from cs.umd.edu ***"

popd 

exit $ret

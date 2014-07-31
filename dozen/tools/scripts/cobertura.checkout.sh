#!/bin/bash 

#--------------------------------------
# Check out cobertura 
#
#	By	baonn@cs.umd.edu
#	Date: 	06/08/2011
#--------------------------------------

script_root_dir=`dirname  $0`
source "$script_root_dir/tools.cfg"
source "$script_root_dir/utils.sh"

exec_cmd "mkdir -p $cobertura_dir"
echo "*** Checking out cobetura from cs.umd.edu ***"
svn co --non-interactive --trust-server-cert --username sv-guitar --password ''  --quiet https://svn.cs.umd.edu/repos/guitar/cobertura $cobertura_dir
ret=$?
echo "*** DONE Checking out cobetura from cs.umd.edu ***"
exit $ret

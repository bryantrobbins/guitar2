#!/bin/bash 

#--------------------------------------
# Buid GUITAR 
#
#	By	baonn@cs.umd.edu
#	Date: 	06/08/2011
#--------------------------------------

script_root_dir=`dirname  $0`
source "$script_root_dir/tools.cfg"

echo "*** Building GUITAR ***"

pushd $guitar_dir

cmd="ant jfc.dist"
echo $cmd
$cmd 
ret=$?

chmod +x dist/guitar/*.sh

popd  

echo "*** DONE Building GUITAR ***"

exit $ret

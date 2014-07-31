#!/bin/bash 

script_root_dir=`dirname $0`
source "$script_root_dir/tools.cfg"
source "$script_root_dir/utils.sh"

echo "*** Checking out Sikuli ***"

pushd $sikuli_dir
if [ $? -ne 0 ]
then
   echo FAILED $sikuli_dir not found
   exit 1
fi

exec_cmd "rm -rf sikulix"
# exec_cmd "git clone https://ishanbanerjee@bitbucket.org/doubleshow/sikulix.git"
exec_cmd "svn checkout https://svn.cs.umd.edu/repos/guitar/sikuli-snapshot/sikulix"


ret=$?
if [ $ret -ne 0 ]
then
   popd
   echo FAILED Checkout $ret
   exit 1
fi

if [ ! -e "sikulix" ]
then
   popd
   echo FAILED Checkout "sikulix" not found
   exit 1
fi

echo "*** DONE Checking out Gephi ***"

popd
exit $ret

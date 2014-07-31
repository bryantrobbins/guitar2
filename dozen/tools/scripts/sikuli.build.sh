#!/bin/bash 

source `dirname $0`/utils.sh
source `dirname $0`/tools.cfg

echo "*** Building Sikuli converter ***"

exec_cmd "pushd $sikuli_dir/sikulix"

# Delete converter if exists
exec_cmd "rm -f $sikuli_dir/target/"

# Build sikuli
exec_cmd "mvn package -pl sikuli-guitar -am -Dmaven.test.skip=true"
ret=$?
if [ $ret -ne 0 ]
then
   echo FAILED compiling Sikuli
   exit 1
fi

popd  

echo "*** DONE Building Sikuli ***"

exit $ret

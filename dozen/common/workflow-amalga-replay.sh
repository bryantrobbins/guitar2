#!/bin/bash
#
# $workspace = location of jenkins SVN tree
# $JAVA_HOME = JDK location (required by some AUTs)
#
aut_name=$1
java=$2
maven=$3
workspace=$4
testdataHost=$5
testdataPort=$6
dbId=$7
testId=$8
execId=$9

if [ -z $java ] ||  [ -z $workspace ] || [ -z $aut_name ] || [ -z $maven ]
then
    echo "Usage: $0 <aut-name> <java-home> <maven-home> <workspace-root> <xsession-dir> <amalga-args ...>"
    exit 1
fi

if [ -z $workspace ]
then
    workspace=`pwd`
fi

echo "Workspace is $workspace"

export PATH=$PATH:$workspace/dozen/common/
export PATH=$PATH:$workspace/dozen/tools/scripts/
export PATH=$PATH:$workspace/dozen/tools/bin/guitar/bin/

export aut_root=$workspace/dozen/auts/

export GUITAR_HOME=$workspace/dozen/tools/amalga

if [ -z $JAVA_HOME ]
then
    echo "Please set JAVA_HOME"
fi

echo "Java is at $JAVA_HOME"
echo "Workspace is at $workspace"

# Set up variables
source "`dirname $0`/common-load-args.sh"
source "`dirname $0`/common.cfg"

# Set up Cobertura for instrumenting AUT
setup.cobertura.sh
setup.guitar.standalone.sh

# Test the aut
common-init-dir.sh $aut_name
ret=$?
if [ $ret -ne 0 ]
then
   exit $ret
fi

common-checkout.sh $aut_name
ret=$?
if [ $ret -ne 0 ]
then
   exit $ret
fi

common-build.sh $aut_name
ret=$?
if [ $ret -ne 0 ]
then
   exit $ret
fi

common-inst.sh $aut_name
ret=$?
if [ $ret -ne 0 ]
then
   exit $ret
fi

common-build-check.sh $aut_name
ret=$?
if [ $ret -ne 0 ]
then
   exit $ret
fi

common-replay-single.sh $aut_name /tmp/xxxx $testdataHost $testdataPort $dbId $testId
ret=$?
if [ $ret -ne 0 ]
then
   exit $ret
fi

# Save data to TestDataManager
save_cmd="$GUITAR_HOME/common-save-replay.groovy $dbId $testId $execId $model_dir/${testId}.ser $model_dir/${testId}.log"
echo $save_cmd
eval $save_cmd

# Ignore tar-ing and comparing errors
exit 0

#!/bin/bash
#
# $workspace = location of jenkins SVN tree
# $JAVA_HOME = JDK location (required by some AUTs)
#
aut_name=$1
java=$2
maven=$3
workspace=$4
x_session_dir=$5
dbId=$6
suiteId=$7
testId=$8
testLength=$9

if [ -z $java ] ||  [ -z $workspace ] || [ -z $x_session_dir ] || [ -z $aut_name ] || [ -z $maven ]
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

common-rip-single.sh $aut_name $x_session_dir /usr/lib/X11 Xvfb $testId $testLength
ret=$?
if [ $ret -ne 0 ]
then
   exit $ret
fi

# Save data to edu.umd.cs.guitar.main.TestDataManager
save_cmd="$GUITAR_HOME/common-save.groovy $dbId $suiteId $testId $model_dir/$testId.tst $model_dir/$testId.map $model_dir/$testId.gui $model_dir/ripping-coverage/cobertura_ripper.ser"
echo $save_cmd
eval $save_cmd

# Ignore tar-ing and comparing errors
exit 0

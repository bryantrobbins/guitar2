#!/bin/bash
#
# $workspace = location of jenkins SVN tree
# $JAVA_HOME = JDK location (required by some AUTs)
#

java=$1
workspace=$2
x_session_dir=$3
x_lib_path=$4
cmd_xvfb=$5
aut_name=$6

if [ -z $java ] ||  [ -z $workspace ] || [ -z $aut_name ] || [ -z $x_lib_path ] || [ -z $cmd_xvfb ] || [ -z $x_session_dir ]
then
    echo "Usage: $0 <java-home> <workspace-root> <x11-home> <xvfb-command> <aut-name>"
    exit 1
fi

if [ -z $workspace ]
then
    workspace=`pwd`
fi

echo "Workspace is $workspace"

export PATH=$PATH:$workspace/dozen/common/
export PATH=$PATH:$workspace/dozen/tools/scripts/
export PATH=$PATH:$workspace/dozen/tools/cobertura/
export PATH=$PATH:$workspace/dozen/tools/bin/guitar/bin/

echo $PATH
export aut_root=$workspace/dozen/auts/


echo "Java is at $java"

# Prepare GUITAR tools
echo "*** Building GUITAR tools ***"
main.sh v1.3 tag
ret=$?
if [ $ret -ne 0 ]
then
   exit $ret
fi

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

common-rip.sh $aut_name $x_session_dir $x_lib_path $cmd_xvfb
ret=$?
if [ $ret -ne 0 ]
then
   exit $ret
fi

common-gui2efg.sh $aut_name new
ret=$?
if [ $ret -ne 0 ]
then
   exit $ret
fi

common-efg2gephipdf.sh $aut_name
ret=$?
if [ $ret -ne 0 ]
then
   exit $ret
fi

common-tc-gen-rand.sh $aut_name 5 100
ret=$?
if [ $ret -ne 0 ]
then
   exit $ret
fi

common-replay.sh $aut_name $aut_root/$aut_name/models/$aut_name.GUI $aut_root/$aut_name/models/$aut_name.EFG $aut_root/$aut_name/testsuites/ rand_l_5 $x_session_dir $x_lib_path $cmd_xvfb
ret=$?
if [ $ret -ne 0 ]
then
    exit $ret
fi

# Archive replay results
tar cvf suite.tar $aut_root/$aut_name/testsuites/rand_l_5
gzip suite.tar

# Aggregate and report coverage information

# Prune test suite based on feasible events
cd $workspace/dozen/tools/amalga
./run.sh scripts/get_feasible.rb $aut_root/$aut_name/testsuites/rand_l_5/logs $aut_root/$aut_name/testsuites/rand_l_5/testcases $aut_root/$aut_name/testsuites/feasible/testcases

# Create amalga models

# Create amalga test suites

# Execute amalga test suites

# Ignore tar-ing and comparing errors
exit 0

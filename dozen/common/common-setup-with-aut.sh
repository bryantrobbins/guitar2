#!/bin/bash
#
# $workspace = location of jenkins SVN tree
# $JAVA_HOME = JDK location (required by some AUTs)
#

aut_name=$1
workspace=$2

if [ -z $workspace ]
then
    workspace=`pwd`
fi

if [ -z $workspace ] || [ -z $aut_name ]
then
    echo "Usage: $0 <aut-name> <workspace-root>"
    exit 1
fi

export PATH=$PATH:$workspace/common/
export PATH=$PATH:$workspace/tools/scripts/
export PATH=$PATH:$workspace/tools/cobertura/
export PATH=$PATH:$workspace/tools/bin/guitar/bin/

export aut_root=$workspace/auts/
export root_dir=$workspace
echo $root_dir

if [ -z $JAVA_HOME ]
then
    echo "Please set JAVA_HOME"
fi

echo "Java is at $JAVA_HOME"

# Set up variables
source "`dirname $0`/common-load-args.sh"
source "`dirname $0`/common.cfg"

# Tool setup
common-setup.sh $aut_name $workspace

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

# Ignore tar-ing and comparing errors
exit 0

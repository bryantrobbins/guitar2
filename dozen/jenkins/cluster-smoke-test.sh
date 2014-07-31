#
# Script to test a cluster from the local machine
#

#
# $JAVA_HOME = JDK location (required by some AUTs)
#
dozen_dir=$(readlink -f $0)
dozen_dir=`dirname $dozen_dir`
dozen_dir=`dirname $dozen_dir`/
jenkins_dir=$dozen_dir/jenkins/
export PATH=$PATH:$dozen_dir/common/
export PATH=$PATH:$dozen_dir/tools/scripts/
export PATH=$PATH:$dozen_dir/tools/cobertura/
export PATH=$PATH:$dozen_dir/tools/bin/guitar/bin/

echo "*** Debug Info ***"
echo dozen_dir=$dozen_dir
echo JAVA_HOME=$JAVA_HOME
pwd=`pwd`
echo "*** Debug Info ***"

if [ -z $1 ] || [ -z $2 ]
then
   echo "Usage: $0 <AUT Name> <host-config>"
   exit 1
fi
if [ ! -e $2 ]
then
 echo "host config file $2 not found"
 exit 1
fi

# Config
aut_name=$1
cfg=$2

# Read $cfg
source $cfg

# Test for expected variabled
if [ -z $efg_arg_type ]
then
   echo efg_arg_type not specified
   exit 1
fi

if [ -z $tmpdirtag ] || [ -z $stage_host ] || [ -z $host_list ] || [ -z $username ] || [ -z $x_lib_path ] || [ -z $cmd_xvfb ]
then
   echo "tmpdirtag=$tmpdirtag stage_host=$stage_host host_list=$host_list username=$username x_lib_path=$x_lib_path cmd_xvfb=$cmd_xvfb not specified in <host config file>"
   exit 1
fi

#
# Test cluster
#
cluster-test.sh $cfg
ret=$?
if [ $ret -ne 0 ]
then
   exit $ret
fi

#
# Prepare GUITAR tools
#
main.sh HEAD num
ret=$?
if [ $ret -ne 0 ]
then
   exit $ret
fi

#
# Prepare $aut_name testcases on LOCAL machine
#
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

#
# Ripping on local machine
#
common-rip.sh $aut_name NULL NULL NULL
ret=$?
if [ $ret -ne 0 ]
then
   exit $ret
fi

common-gui2efg.sh $aut_name $efg_arg_type
ret=$?
if [ $ret -ne 0 ]
then
   exit $ret
fi

common-tc-gen-sq.sh $aut_name 2 0
ret=$?
if [ $ret -ne 0 ]
then
   exit $ret
fi

#
# Split in 2 parts
#
util-split-dir.sh $dozen_dir/auts/$aut_name/testsuites/sq_l_2/ 2
ret=$?
if [ $ret -ne 0 ]
then
   exit $ret
fi

#
# Setup $aut_name on 2 REMOTE machines
#
cluster-setup.sh $aut_name $cfg
ret=$?
if [ $ret -ne 0 ]
then
   exit $ret
fi

#
# Distribute $aut_name testcases from local machine to remote machines
#
cluster-tc-dist.sh $aut_name $cfg NULL NULL $dozen_dir/auts/ sq_l_2
ret=$?
if [ $ret -ne 0 ]
then
   exit $ret
fi

#
# Replay testcases on cluster, store result on archive machine.
# Archive location is hardcoded. Ideally, this should be configurable
# to enable concurrent execution of this script.
#
cluster-replay.sh $aut_name $cfg jenkins gandalf.cs.umd.edu /var/lib/jenkins/workspace/UMD-Dozen-Cluster-Setup-Smoke-Test/guitar/dozen/auts/ sq_l_2
ret=$?
if [ $ret -ne 0 ]
then
   exit $ret
fi

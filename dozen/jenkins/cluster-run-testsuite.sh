#!/bin/bash

#
# $JAVA_HOME = JDK location (required by some AUTs)
#

#
# Required by GUITAR and dozen scripts
#
dozen_dir=$(readlink -f $0)
dozen_dir=`dirname $dozen_dir`
dozen_dir=`dirname $dozen_dir`/
export PATH=$PATH:$dozen_dir/common/
export PATH=$PATH:$dozen_dir/tools/scripts/
export PATH=$PATH:$dozen_dir/tools/cobertura/
export PATH=$PATH:$dozen_dir/tools/bin/guitar/bin/

#
# Note:
#
# This script currently assumes that the archive_hostname
# is the machine on which it is running. This will not
# be true in general. This dependency needs to be removed
# by executing the "split" command remotely on the
# archive_hostname.
#
# Note:
#
# Archive location:
#
#    $archive_auts_dir/$aut_name/testsuites/$testsuite_name/
#
# will be first cleared. Content will be filled during replay.
#

# Debug
echo "*** Debug info ***"
echo dozen_dir=$dozen_dir
echo JAVA_HOME=$JAVA_HOME
pwd
ls -l
echo "*** Debug info ***"

# Start script
aut_name=$1                # AUT from "dozen"
archive_username=$2        # Machine to store all results
archive_hostlist=$3        # Machine to store all results
archive_hostlist_orig=$3   # Machine to store all results
archive_auts_dir=$4        # Dir to store all results from starting "/"
testsuite_name=$5          # Name only of testsuite to execute
cfg=$6                     # Remote host config file

# Parameter check
if [ -z $aut_name ] || [ -z $archive_username ] || [ -z $archive_hostlist ] || [ -z $archive_auts_dir ] || [ -z $testsuite_name ] || [ -z $cfg ]
then
   echo "Usage: $0 <AUT-name> <archive-username> <archive-hostlist> <archive-auts-dir> <testsuite-name> <remote host cfg>"
   echo  ""
   echo "<archive-hostlist> = ':' separated list of archive hosts. Results will be archived to the first host. Rest are temporary stores."

   exit 1
fi

# Replace ":" with " "
archive_hostlist=`echo "$archive_hostlist" | awk 'BEGIN{FS=":"}{for (i=1; i<=NF; i++) print $i}'`

# Set $archive_hostname as the first name in $archive_hostlist
archive_hostname=""
for h in $archive_hostlist
do
   archive_hostname=$h
   break
done


# Log file for this script
log=cluster-run-testsuite.log

# Check cluster configuration file
if [ ! -e $cfg ]
then
   echo $cfg not found
   exit 1
fi
source "$cfg"
if [ -z $host_list ] || [ -z $username ]
then
   echo $cfg should specify username and host_list
   exit 1
fi


#
# Test cluster
#
echo "*** Begin executing testcases on cluster ***"
echo "*** CRT 1/5 Test cluster ***"
rm -f $log
cluster-test.sh $cfg
ret=$?
if [ $ret -ne 0 ]
then
   echo FAILED cluster test
   exit $ret
fi

# Check cluster for remnant of Xvfb lock files
cluster-cmd.sh $cfg "ls -la /tmp/.X55-lock /tmp/.X11-unix/X55*"

#
# Setup $aut_name on remote machines
#
echo "*** CRT 2/5 Cluster setup ***"
cluster-setup.sh $aut_name $cfg | tee -a $log
ret=${PIPESTATUS[0]}
if [ $ret -ne 0 ]
then
   echo FAILED cluster setup
   exit $ret
fi

#
# Split testsuite into as many parts as experiment machines
#
# TODO: Run util-split-dir.sh on archinve_hostname
#
echo "*** CRT 3/5 Split testcases ***"
util-split-dir.sh $archive_auts_dir/$aut_name/testsuites/$testsuite_name ${#host_list[@]} | tee -a $log
ret=${PIPESTATUS[0]}
if [ $ret -ne 0 ]
then
   echo FAILED split dir
   exit $ret
fi

#
# Distribute testcases on remote machines from archive_hostname
#
echo "*** CRT 4/5 Distribute testcases ***"
cluster-tc-dist.sh $aut_name $cfg $archive_username $archive_hostname $archive_auts_dir $testsuite_name | tee -a $log
ret=${PIPESTATUS[0]}
if [ $ret -ne 0 ]
then
   echo FAILED testcase dist
   exit $ret
fi

#
# Replay testcases on cluster, store result on archive_hostlist
# Archive location $archive_auts_dir/$aut_name/* will be
# first cleared. Content will be filled during replay.
#
echo "*** CRT 5/5 Replay testcases ***"
cluster-replay.sh $aut_name $cfg $archive_username $archive_hostlist_orig $archive_auts_dir $testsuite_name | tee -a $log
ret=${PIPESTATUS[0]}
if [ $ret -ne 0 ]
then
   echo FAILED Replay
   exit $ret
fi

# Save the replay log with the replay results
rsync $log $archive_username@$archive_hostname:$archive_auts_dir/$aut_name/testsuites/$testsuite_name/

echo "*** Completed executing testcases on cluster ***"
exit 0

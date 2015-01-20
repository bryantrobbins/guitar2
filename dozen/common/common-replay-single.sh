#!/bin/bash

#
#  Copyright (c) 2009-@year@. The  GUITAR group  at the University of
#  Maryland. Names of owners of this group may be obtained by sending
#  an e-mail to atif@cs.umd.edu
#
#  Permission is hereby granted, free of charge, to any person obtaining
#  a copy of this software and associated documentation files
#  (the "Software"), to deal in the Software without restriction,
#  including without limitation  the rights to use, copy, modify, merge,
#  publish,  distribute, sublicense, and/or sell copies of the Software,
#  and to  permit persons  to whom  the Software  is furnished to do so,
#  subject to the following conditions:
#
#  The above copyright notice and this permission notice shall be included
#  in all copies or substantial portions of the Software.
#
#  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
#  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
#  MERCHANTABILITY,  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
#  IN NO  EVENT SHALL THE  AUTHORS OR COPYRIGHT  HOLDERS BE LIABLE FOR ANY
#  CLAIM, DAMAGES OR  OTHER LIABILITY,  WHETHER IN AN  ACTION OF CONTRACT,
#  TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
#  SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
#

####################################################
# Replay the test suites 
#
#   By   baonn@cs.umd.edu
#   Date:    06/08/2011
####################################################


echo "*** Replaying ***"

#--------------------------
# Parameter check
#--------------------------
if [ $# -lt 5 ]
then
   echo "Usage: $0 <AUT name> <tmpDir> <testdataHost> <testdataPort> <dbId> <testId>"
   exit 1
fi

testdataHost=$3
testdataPort=$4
dbId=$5
testId=$6

#--------------------------
# Script configuration
#--------------------------
# Load common configuration
source "`dirname $0`/common-load-args.sh"
source "`dirname $0`/common.cfg"
source "`dirname $0`/util.sh"

# Load aut local configuration 
source "$aut_scripts_dir/aut.cfg"
source "$aut_scripts_dir/aut.utils.sh"

ret=0
cmd_cobertura_report="$cobertura_dir/cobertura-mod-report-name.sh"
cmd_cobertura_merge="$cobertura_dir/cobertura-merge.sh"

#classpath=$cobertura_classpath:$aut_inst_classpath:$aut_classpath
classpath=$aut_inst_classpath:$aut_classpath

# Environments
globalRet=0

#------------------------------
# Local directory setup
#------------------------------
# create tmp dirs
if [ ! -e $tmp_dir ]
then
   exec_cmd "mkdir $tmp_dir"
fi

#--------------------------
# Cobertura files
#--------------------------
cobertura_main_file=$model_dir/$testId.ser

#--------------------------
# Replay
#--------------------------

# Setup java command line parameters
export JAVA_CMD_PREFIX="java $JAVA_OPTS -Xms64m -Xmx512m -Duser.home=$tmp_home -Dnet.sourceforge.cobertura.datafile=$cobertura_main_file "

testname=$testId

echo "*** Executing test $testname ***" 

echo Cleaning up $aut_name
cleanup
sleep 1

local_log_file=$model_dir/$testname.log
local_oracle_file=$model_dir/$testname.orc 
local_LOG_file=$model_dir/$testname.LOG

cmd="$cmd_jfcreplayer_amalga -cp  $classpath  $replayer_launcher -c $aut_mainclass -l $local_LOG_file -gs $local_oracle_file  -i $aut_initial_waitting_time -d $aut_replay_delay -cf $aut_configuration_file -to $aut_replay_timeout -d $aut_replay_delay -so $aut_replay_step_timeout -tdi $testId -tdd $dbId -tdh $testdataHost -tdp $testdataPort"

if [ ! -z $aut_arguments ]       
then
   cmd="$cmd -a $aut_arguments"   
fi

if [ ! -z $aut_arguments2 ]       
then
   cmd="$cmd:$aut_arguments2"   
fi

if $reg_title_match; then
   cmd="$cmd -r"
fi 

#----------------------
# Execute replay
#----------------------
replay_start=`date +%s`
pushd $tmp_home
exec_cmd "$cmd 2>&1 | tee $local_log_file"
ret=$?
popd
replay_stop=`date +%s`
if [ $ret -ne 0 ]
then
   echo FAILED replaying on `hostname` $testname
   echo FAILED replaying $cmd
   echo FAILED continuing with next
   globalRet=$ret
fi
#exec_cmd "sync"

# --------------------
# Log time
# --------------------
echo Replay time $((replay_stop - replay_start))
echo Cobertura process time $((cov_proc_stop - cov_proc_start))
echo Rsync time $((rsync_stop - rsync_start))

exec_cmd "rm -f *.log"
exec_cmd "rm -rf ~/NewFolder*"

if [ $globalRet -ne 0 ]
then
   echo FAILED replay
fi
echo "*** DONE $aut_name ***"

#
# Might consider not returning an error code
# if an individual replay fails. Instead return
# a non-zero status only if there is a global 
# error.
exit $globalRet

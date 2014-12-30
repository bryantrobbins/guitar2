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
if [ $# -lt 4 ]
then
   echo "Usage: $0 <AUT name> <dbId> <suiteId> <testId>"
   exit 1
fi

dbId=$2
suiteId=$3
testId=$4

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

testname=$testId
echo "*** Executing test $testname ***" 

echo Cleaning up $aut_name
cleanup
sleep 1

local_log_file=$model_dir/$testname.log
local_oracle_file=$model_dir/$testname.orc 
local_LOG_file=$model_dir/$testname.LOG

# Setup java command line parameters
log_file=$local_LOG_file
oracle_file=$local_oracle_file
aut_initial_waiting_time=$aut_initial_waitting_time
delay=$aut_replay_delay
test_id=$testId
db_id=$dbId

# Copy clean cobertura file
cp $cobertura_data_file_clean $cobertura_main_file

# Create coverage dir
coverage_dir=$model_dir/$testname_replay_coverage
mkdir $coverage_dir

cmd="gradle -b $guitar_dir/guitar.gradle replay -Paut_mainclass=$aut_mainclass -Paut_bin=$aut_bin_dir -Paut_inst=$aut_inst_dir -Plog_file=$local_LOG_file -Poracle_file=$local_oracle_file -Paut_initial_waiting_time=$aut_initial_waitting_time -Pdelay=$aut_replay_delay -Paut_configuration_file=$aut_configuration_file -Paut_replay_timeout=$aut_replay_timeout -Paut_replay_step_timeout=$aut_replay_step_timeout -Ptest_id=$testId -Psuite_id=$suiteId -Pdb_id=$dbId -Ptmp_home=$tmp_home -Pcobertura_file=$cobertura_main_file -Pclean_coverage_file=$cobertura_data_file_clean -Pcoverage_dir=$coverage_dir"

if [ ! -z $aut_arguments ]
then
   cmd="$cmd -Paut_arguments=$aut_arguments"
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
exec_cmd "sync"

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

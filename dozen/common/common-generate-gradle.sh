#!/bin/bash

##################################
# Generate testcases
##################################


#--------------
# Configuration
#--------------
source "`dirname $0`/common-load-args.sh"
source "`dirname $0`/common.cfg"
source "`dirname $0`/util.sh"

echo "*** Generating testcases $aut_name ***"

#--------------
# Parameter check
#--------------
if [ $# -lt 2 ]
then
   echo "Usage $0 <AUT NAME> <test suite length> [max number]"    
   exit
fi

cmd_tc_gen="gradle -b $guitar_dir/guitar.gradle"
length=$2
max_number=0

#--------------
# Create testsuites root dir to contain all test suites  
#--------------
exec_cmd "mkdir -p $testsuite_root"

#--------------
# Create test suite dir
#--------------
testsuite_name="sq_l_$length"
testsuite_dir=$testsuite_root/$testsuite_name
exec_cmd "mkdir -p $testsuite_dir"

#--------------
# Create test cases dir 
#--------------
testcases_dir=$testsuite_dir/$testcases_dir_name
if [ -e $testcases_dir ]
then   
   exec_cmd "rm -rf $testcases_dir"
fi 
exec_cmd "mkdir $testcases_dir"

#--------------
# Generate test cases 
#--------------
exec_cmd "$cmd_tc_gen -Paut_efg_file=$aut_efg_file -Plength=$length -Ptestcases_dir=$testcases_dir generate_sl"
exec_cmd "rm -f *.log"

#--------------
# Sanity checks
#--------------
numtc=`ls -l $testcases_dir/*.tst | wc -l`
r=$?
if [ $r -ne 0 ]
then
   echo FAILED $aut_name
   exit 1
fi

ret=0
if [ ! $numtc -eq $max_number ] && [ $max_num -ne 0 ]
then
  ret=1
  echo FAILED $aut_name $numtc $max_number
fi

echo "*** DONE $aut_name ***"
exit $ret

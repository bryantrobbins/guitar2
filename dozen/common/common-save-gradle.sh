#!/bin/bash

#--------------
# Configuration
#--------------
source "`dirname $0`/common-load-args.sh"
source "`dirname $0`/common.cfg"
source "`dirname $0`/util.sh"

echo "*** Saving testcases ***"

#--------------
# Parameter check
#--------------
if [ $# -lt 3 ]
then
   echo "Usage $0 <AUT NAME> <test suite name> <db id>"
   exit
fi

cmd_save="gradle -b $guitar_dir/guitar.gradle"

testsuite_name=$2
testsuite_dir=$testsuite_root/$testsuite_name
testcases_dir=$testsuite_dir/$testcases_dir_name

suite_id="amalga_${aut_name}_${testsuite_name}"

db_id=$3

#--------------
# Save suite-level artifacts
#--------------
exec_cmd "$cmd_save -Paut_efg_file=$aut_efg_file -Pdb_id=$db_id -Psuite_id=$suite_id -Paut_gui_file=$aut_gui_file -Paut_ripper_coverage_file=$ripper_coverage_dir/cobertura_ripper.ser saveSuiteFiles"

#--------------
# Save TC-level artifacts
#--------------
for t in $testcases_dir/*.tst
do
	echo "Saving test case file $t"
	exec_cmd "$cmd_save -Pdb_id=$db_id -Psuite_id=$suite_id -Ptest_case_file=$testcases_dir/$t saveTestCaseFiles"
done

r=$?
if [ $r -ne 0 ]
then
	echo FAILED $aut_name
	exit 1
fi

ret=0
echo "*** DONE $aut_name ***"
exit $ret

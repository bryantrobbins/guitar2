#!/bin/bash

if [ -z $aut_name ]
then
   echo "Missing aut name in $0"
   exit 1
fi 

if [ -z $root_dir ]
then
   echo "Missing root directory in $0"
   exit 1
fi 


# This script is used to setup the AUT before each run
function cleanup { 
   if [ -z $tmp_home ]
   then
       echo FAILED tmp_home not specified
       exit 1
   fi

   # Delete coverage files
   exec_cmd "rm -f  $ripper_coverage_dir/*"
   exec_cmd "rm -fr $tmp_home"

   exec_cmd "mkdir -p $tmp_home"
   exec_cmd "cp -f $aut_input_dir/* $tmp_home/"
}





#!/bin/bash

# This script is used to clean up aut after each run

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

function cleanup {  
   #
   # Make home directory since some AUTs need it and hence the
   # common script expects it to be present.
   #
   if [ -z $tmp_home ]
   then
      echo FAILED tmp_home not specified
      exit 1
   fi
   exec_cmd "rm -rf $tmp_home"
   exec_cmd "mkdir -p $tmp_home"

   # Copy input files to AUT specific location
   exec_cmd "cp -fr $aut_dir/input/* $aut_bin_dir/org/cesilko/rachota/core/"
   exec_cmd "cp -fr $aut_dir/input/* $aut_inst_dir/org/cesilko/rachota/core/"
}

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
   echo "Cleaning up..."

   if [ -z $tmp_home ]
   then
      echo FAILED tmp_home not defined
      exit 1
   fi

   exec_cmd "rm -rf $tmp_home/"
   exec_cmd "mkdir -p $tmp_home/"

   exec_cmd "cp -fr $aut_input_dir/.jedit $tmp_home/"
   exec_cmd "cp -fr $aut_bin_dir/keymaps $tmp_home/"
   exec_cmd "cp -fr $aut_bin_dir/modes $tmp_home/"
}

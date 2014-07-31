#!/bin/bash 

#--------------------------------------
# Initialize directory structure for tools 
#
#	By	baonn@cs.umd.edu
#	Date: 	06/08/2011
#--------------------------------------
source "`dirname $0`/tools.cfg"
source "`dirname $0`/utils.sh"

exec_cmd "mkdir -p $tool_root"

exec_cmd "mkdir -p $guitar_dir"
exec_cmd "mkdir -p $cobertura_dir"
exec_cmd "mkdir -p $script_dir"
exec_cmd "mkdir -p $gephi_dir"

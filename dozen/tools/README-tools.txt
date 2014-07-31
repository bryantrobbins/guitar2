#======================================
#TOOL SETUP  INSTRUCTION 
#
#	 By baonn@cs.umd.edu
#	 Date: 06/08/2011
#======================================
#
#I. DIRECTORY LAYOUT 
#
#All benchmark share the following directory layout
#
#<Root DIR>
#	|- scripts:	tool setup scripts
#	|
#	|- bin: built tools
#		|- guitar
#		|- cobertura
#		|- scripts
#

#II. WORKFLOW
#
#Run the following scripts (in order) to setup tools for running experiments
#
# go into scripts directory
cd scripts 

# initialize a standard directory layout
./init-dir.sh

# check out GUITAR and necessary scripts 
./guitar.checkout.sh

# build GUITAR 
./guitar.build.sh

# check out cobertura and necessary scripts 
./cobertura.checkout.sh

# build cobertura 
./cobertura.build.sh

# check out data analysis scripts 
./script.checkout.sh

# setup environment variables or current shell 
#
# NOTE: if you want to permanently call scripts from any location, you need to manually add all following directory to your PATH variable: 
# 	<Root DIR>/bin/guitar/bin
# 	<Root DIR>/cobertura
# 	<Root DIR>/scripts

source env.cfg


# III. TOOL CONFIGURATIONS
# Change the tool configutarions in tool.cfg


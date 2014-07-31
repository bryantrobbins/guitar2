#!/bin/bash 

#--------------------------------------
# Buid GUITAR 
#
#	By	baonn@cs.umd.edu
#	Date: 	06/08/2011
#--------------------------------------

source `dirname $0`/tools.cfg

script_root_dir=$(abspath `dirname $0`)
if [ "$script_root_dir" == "" ]
then
   echo FAILED abspath
   exit 1
fi

gephi_converter_dir=$script_root_dir/../gephi/

echo "*** Building Gephi converter ***"

pushd $gephi_converter_dir

# Delete converter if exists
exec_cmd "rm -f $gephi_dir/efg2gephipdf.jar"

# Locate gephi toolkit jar file
gephi_jar=`ls $gephi_dir/*.jar`

# Build new converter
exec_cmd "javac -cp $gephi_jar edu/umd/cs/guitar/gephi/efg2gephipdf/EFG2GephiPDF.java"
ret=$?
if [ $ret -ne 0 ]
then
   echo FAILED compiling gephi converter
else
   # Create jar
   exec_cmd "rm -f efg2gephipdf.jar"
   exec_cmd "jar cf efg2gephipdf.jar edu"

   # Move to bin dir
   exec_cmd "mv *.jar $gephi_dir/"
   ret=$?
fi

popd  

echo "*** DONE Building Gephi converter ***"

exit $ret

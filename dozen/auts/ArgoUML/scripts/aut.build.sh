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
# build the  application 
#
#   By   baonn@cs.umd.edu
#   Date:    06/08/2011
# 
# NOTE: 
#    Need to setup JAVA_HOME  pointing to jdk directory and ANT_HOME  pointing to ant directory
#   Example: 
#      Unbutu:
#         export JAVA_HOME=/usr/lib/jvm/java-6-sun/
#         export ANT_HOME=/usr/share/ant/
#
#      Mac OS:
#           export JAVA_HOME=/Library/Java/Home
#           export ANT_HOME=/usr/share/ant/
#
####################################################

#--------------
# Configuration
#--------------
source "$root_dir/common/common.cfg"
source "$root_dir/common/util.sh"
source "`dirname $0`/aut.cfg"

#--------------
# Parameter check
#--------------
echo "Build into $aut_bin_dir"
if [ ! -e $aut_bin_dir ]
then   
   exec_cmd "mkdir $aut_bin_dir"
fi

#--------------
# Build
#--------------
# Enter src
pushd $aut_src_dir

# Enter directory containing build 
pushd  src 

# Build using default build process
./build.sh 

# Quit build dir 
popd 

# Quit src dir 
popd 

#--------------
# Move necessary files to bin 
#--------------
# move to bin
exec_cmd "rm -rf $aut_bin_dir"
exec_cmd "mkdir -p $aut_bin_dir"

# Copy built files to bin 
echo "Current dir `pwd`"
exec_cmd "mv -f $aut_src_dir/src/argouml-build/build/* $aut_bin_dir/"

# Unjar for instrumentation 
unjar_list=("argouml-euml.jar"  "argouml.jar"  "argouml-mdr.jar" "argouml-model.jar")

for file in ${unjar_list[@]}
do
   # enter class dir 
   pushd $aut_bin_dir/classes
   
   # extract jar file from parent dir 
   jar xvf ../$file
   
   # delete .jar file to avoid duplication 
   rm ../$file

   popd 
done 

#--------------
# Sanity check
#--------------
ret=0
class=`echo $aut_mainclass | perl -pi -e 's/\./\//g' `
if [ ! -e $aut_bin_dir/classes/$class.class ]
then
   echo FAILED build $aut_name
   ret=1
fi
exit $ret

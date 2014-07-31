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

#############################
# Build the  application in to bin directory in an easy to instrument structure
#	By	baonn@cs.umd.edu
#	Date: 	06/08/2011
##############################

#--------------
# Configuration
#--------------
source "$root_dir/common/common.cfg"
source "$root_dir/common/util.sh"
source "`dirname $0`/aut.cfg"

#--------------
# Parameter check
#--------------
echo "Build into $aut_bin_dir..."
if [ ! -e $aut_bin_dir ]
then	
	cmd="mkdir $aut_bin_dir"
	echo $cmd
	$cmd
	echo "DONE"
fi

#--------------
# Cleanup
#--------------
cmd="rm -rf $aut_bin_dir/* "
echo $cmd
$cmd 
echo "DONE"

#--------------
# Build
#--------------
find $aut_src_dir -name "*.java" -exec javac -cp $aut_src_dir -d $aut_bin_dir {} \;
cp -f $aut_src_dir/*.txt $aut_bin_dir

#--------------
# Sanity check
#--------------
ret=0
class=`echo $aut_mainclass | perl -pi -e 's/\./\//g' `
if [ ! -e $aut_bin_dir/$class.class ]
then
   echo FAILED build $aut_name
   ret=1
fi
exit $ret

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
# Checkout Buddi
#	By	baonn@cs.umd.edu
#	Date: 	06/08/2011
####################################################

source "$root_dir/common/common.cfg"
source "$root_dir/common/utils.sh"
source "`dirname $0`/aut.cfg"
source "`dirname $0`/aut.utils.sh"

echo "Checking out to $aut_src_dir"
exec_cmd "mkdir -p $aut_src_dir"
echo "DONE"

# Enter src dir 
pushd $aut_src_dir

# Cleanup old stuff
exec_cmd "rm -rf * "
echo "DONE"

# Create tmp dir for downloading
tmp_dir="tmp"
if [ ! -e $tmp_dir ]
then
	mkdir $tmp_dir
fi

# Enter tmp directory
pushd $tmp_dir

# Download the application 
exec_cmd "cvs -z3 -d:pserver:anonymous@rachota.cvs.sourceforge.net:/cvsroot/rachota co  rachota"
echo "DONE"

# Exit tmp dir back to src 
popd 

# Copy source code 
exec_cmd "mv $tmp_dir/rachota/* ."
echo "DONE"

# Cleanup .tar file and tmp directory
exec_cmd "rm -rf $tmp_dir"

# Sanity check to see if "src" dir exists in the Buddi src
ret=0
if [ ! -e "src" ]
then
   ret=1
   echo FAILED checkout
fi

popd
exit $ret

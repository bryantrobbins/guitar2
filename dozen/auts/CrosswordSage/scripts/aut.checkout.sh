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
# checkout the  application to src directory
#
#	By	baonn@cs.umd.edu
#	Date: 	06/08/2011
####################################################

source "$root_dir/common/common.cfg"
source "$root_dir/common/util.cfg"
source "`dirname $0`/aut.cfg"
source "`dirname $0`/utils.sh"

echo "Checking out to $aut_src_dir"
cmd="mkdir -p $aut_src_dir"
echo $cmd
$cmd
echo "DONE"

# enter src dir 
echo "Entering $aut_src_dir" 
pushd $aut_src_dir

# cleanup
cmd="rm -rf * "
echo $cmd
$cmd 
echo "DONE"


# create tmp dir
tmp_dir="tmp"
if [ ! -e $tmp_dir ]
then
	mkdir $tmp_dir
fi
pushd $tmp_dir

# download the application 
cmd="wget http://iweb.dl.sourceforge.net/project/crosswordsage/crosswordsage/0.3.5/crosswordsage-0.3.5-src.zip"
echo $cmd
$cmd
echo "DONE"

# unzip
cmd="unzip -o -d . crosswordsage-0.3.5-src.zip  "
echo $cmd
$cmd 
echo "DONE"

# quit tmp dir back to src 
popd 

# reorganize source code 
# copy source code 
cmd="mv $tmp_dir/sf_crosswordsage/src/* ."
echo $cmd
$cmd 
echo "DONE"

# copy configuration files
cmd="mv $tmp_dir/sf_crosswordsage/*.txt ."
echo $cmd
$cmd 
echo "DONE"


# cleanup
rm -rf tmp

# copy to the remote site 

popd



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
#   By   tnt@cs.umd.edu
#   Date:    09/30/2012
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
if [ -z $aut_bin_dir ] || [ -z $aut_src_dir ] || [ -z $aut_dir ]
then
   echo \$aut_bin_dir or \$aut_src_dir or \$aut_dir not specified
   exit 1
fi
if [ ! -e $aut_bin_dir ]
then   
   exec_cmd "mkdir $aut_bin_dir"
fi
if [ -z $aut_bin_dir ]
then
   echo \$aut_bin_dir not created
   exit 1
fi

#--------------
# Patch build.properties
#--------------
bldprop=$aut_src_dir/pdfsam-maine/ant//build.properties 
tmpbldprop=$aut_src_dir/pdfsam-maine/ant//build.properties.tmp

# pdfsam.deploy.dir
replace_bin_dir=$(echo $aut_bin_dir | sed -e 's/\//\\\//g')
replace_src_dir=$(echo $aut_src_dir | sed -e 's/\//\\\//g')

exec_cmd "sed 's/pdfsam\.deploy\.dir.*/pdfsam\.deploy\.dir=$replace_bin_dir/' < $bldprop > $tmpbldprop"
exec_cmd "mv $tmpbldprop $bldprop"

# workspace.dir
exec_cmd "sed 's/workspace\.dir.*/workspace\.dir=$replace_src_dir/' < $bldprop > $tmpbldprop"
exec_cmd "mv $tmpbldprop $bldprop"

# build.dir
exec_cmd "sed 's/build\.dir.*/build\.dir=$replace_bin_dir/' < $bldprop > $tmpbldprop"
exec_cmd "mv $tmpbldprop $bldprop"

#--------------
# Build
#--------------
# Clean bin directory
exec_cmd "rm -rf $aut_bin_dir/* $aut_dir/ext $aut_dir/lib $aut_dir/plugins"

# Enter src
exec_cmd "pushd $aut_src_dir"

# Enter directory containing build 
exec_cmd "pushd  pdfsam-maine"
exec_cmd "pushd  ant"

# Build
exec_cmd "ant"

# Quit build dir 
exec_cmd "popd"
exec_cmd "popd"

# Quit src dir 
exec_cmd "popd"

# Extract release pdfsam jar file
exec_cmd "rm -rf $aut_bin_dir/pdfsam-maine/release/dist/pdfsam-basic/pdfsam-2.2.1"
exec_cmd "mkdir $aut_bin_dir/pdfsam-maine/release/dist/pdfsam-basic/pdfsam-2.2.1"
exec_cmd "cd $aut_bin_dir/pdfsam-maine/release/dist/pdfsam-basic/pdfsam-2.2.1"
exec_cmd "jar -xf $aut_bin_dir/pdfsam-maine/release/dist/pdfsam-basic/pdfsam-2.2.1.jar"

# Copy necessary files to right location for rip
exec_cmd "cp $aut_bin_dir/pdfsam-maine/release/dist/pdfsam-basic/pdfsam-config.xml /root/cs.svn/guitar/dozen/auts/PdfSam/aut/pdfsam-config.xml"

exec_cmd "mkdir /root/cs.svn/guitar/dozen/auts/PdfSam/aut/lib/"
exec_cmd "cp $aut_bin_dir/pdfsam-maine/release/dist/pdfsam-basic/lib/* /root/cs.svn/guitar/dozen/auts/PdfSam/aut/lib/"

exec_cmd "mkdir /root/cs.svn/guitar/dozen/auts/PdfSam/aut/plugins/"
exec_cmd "cp -r $aut_bin_dir/pdfsam-maine/release/dist/pdfsam-basic/plugins/* /root/cs.svn/guitar/dozen/auts/PdfSam/aut/plugins/"

exec_cmd "mkdir /root/cs.svn/guitar/dozen/auts/PdfSam/aut/ext/"
exec_cmd "cp $aut_bin_dir/pdfsam-maine/release/dist/pdfsam-basic/ext/* /root/cs.svn/guitar/dozen/auts/PdfSam/aut/ext/"

#--------------
# Sanity check
#--------------
ret=0
if [ ! -e $aut_bin_check_list ]
then
   echo FAILED build $aut_name
   ret=1
fi
exit $ret

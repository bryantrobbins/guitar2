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
# checkout the  application 
#
#   By   baonn@cs.umd.edu
#   Date:    06/08/2011
####################################################


source "$root_dir/common/common.cfg"
source "$root_dir/common/util.sh"
source "`dirname $0`/aut.cfg"
source "`dirname $0`/aut.utils.sh"

if [ -z $1 ]
then
   revision=19844
else
   revision=$1
fi 

# Check out a dir to src 
function check_out_dir {
   dir=$1

   # Clean up svn 
   exec_cmd "svn cleanup $dir"  

   # Check out 
   exec_cmd "svn co --no-auth-cache --quiet -r $revision --username guest --password '' http://argouml.tigris.org/svn/argouml/trunk/$dir $dir"
}

echo "Checking out to $aut_src_dir"
mkdir -p  $aut_src_dir

# Enter src dir 
pushd $aut_src_dir

# Main 
check_out_dir src
check_out_dir tools

# cleanup
popd

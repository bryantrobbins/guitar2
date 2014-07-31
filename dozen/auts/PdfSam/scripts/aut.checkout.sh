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
#   By   tnt@cs.umd.edu
#   Date:    09/30/2012
####################################################

source "$root_dir/common/common.cfg"
source "$root_dir/common/util.sh"
source "`dirname $0`/aut.cfg"
source "`dirname $0`/aut.utils.sh" 

#
# Setup
#
echo "Checking out to $aut_src_dir"
if [ -z $aut_src_dir ]
then
   echo \$aut_src_dir not specified
   exit 1
fi
exec_cmd "mkdir -p  $aut_src_dir"
exec_cmd "rm -rf $aut_src_dir/*"

#
# Enter src dir 
#
pushd $aut_src_dir

exec_cmd "wget http://sourceforge.net/projects/pdfsam/files/pdfsam/2.2.1/pdfsam-2.2.1-out-src.zip"
if [ $? -ne 0 ]
then
   echo "Error in wget"
   exit 1
fi

#
# Unzip .zip files
#
unzip pdfsam-2.2.1-out-src.zip
unzip emp4j-1.0.2-build-src.zip
unzip jpodcreator-0.0.1-build-src.zip
unzip launcher-src.zip
unzip nsi.zip
unzip pdfsam-2.2.1-build-src.zip
unzip pdfsam-2.2.1-libraries.zip
unzip pdfsam-2.2.1-libraries-jPodRenderer.zip
unzip pdfsam-2.2.1-template.zip
unzip pdfsam-console-2.3.1e-build-src.zip
unzip pdfsam-jcmdline-1.0.6-build-src.zip
unzip pdfsam-langpack-build-src.zip
unzip pdfsam-merge-0.7.4-build-src.zip
unzip pdfsam-mix-0.2.0-build-src.zip
unzip pdfsam-rotate-0.0.5-build-src.zip
unzip pdfsam-split-0.5.7-build-src.zip
unzip pdfsam-vcomposer-0.0.8-build-src.zip
unzip pdfsam-vpagereorder-0.0.7-build-src.zip

#
# Cleanup
#
rm pdfsam-2.2.1-out-src.zip
rm emp4j-1.0.2-build-src.zip
rm jpodcreator-0.0.1-build-src.zip
rm launcher-src.zip
rm nsi.zip
rm pdfsam-2.2.1-build-src.zip
rm pdfsam-2.2.1-libraries.zip
rm pdfsam-2.2.1-libraries-jPodRenderer.zip
rm pdfsam-2.2.1-template.zip
rm pdfsam-console-2.3.1e-build-src.zip
rm pdfsam-jcmdline-1.0.6-build-src.zip
rm pdfsam-langpack-build-src.zip
rm pdfsam-merge-0.7.4-build-src.zip
rm pdfsam-mix-0.2.0-build-src.zip
rm pdfsam-rotate-0.0.5-build-src.zip
rm pdfsam-split-0.5.7-build-src.zip
rm pdfsam-vcomposer-0.0.8-build-src.zip
rm pdfsam-vpagereorder-0.0.7-build-src.zip

#
# Exit dir
#
popd

#--------------------------------------
# Utilities function  
#
#   By   baonn@cs.umd.edu
#   Date:    06/08/2011
#--------------------------------------


# create a new dir if not exist 
function b_mkdir {
   if [ ! -e $1 ]
   then 
      mkdir $1
   fi 
}

# execute a command 
function exec_cmd {
   cmd=eval $@

   return $?
}

# readlink or equivalent
function abspath {
   ABSPATH=""
   hostos=`uname`
   if [ "$hostos" == "Linux" ]
   then
      # Linux
      ABSPATH=$(readlink -f $1)
   else
      # Mac OS
      ABSPATH=$(perl -MCwd=realpath -e "print realpath '$1'")
   fi
   echo $ABSPATH
}

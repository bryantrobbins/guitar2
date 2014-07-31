#!/bin/bash
# This scirpt wraps a call to a Groovy script
# It uses a "shaded jar" of Groovy (groovy-all) to
# avoid classpath conflicts between Groovy and AUTs.

extra_classpath=$1
shift
script=$1
shift

amalga_root=`dirname $0`/../amalga
base_classpath="$amalga_root/*"
classpath=$base_classpath

if [ ! -z $extra_classpath ]
then
	classpath=$classpath:$extra_classpath
fi

cmd="java -cp \"$classpath\" groovy.ui.GroovyMain $script"

echo $cmd $@
eval $cmd $@

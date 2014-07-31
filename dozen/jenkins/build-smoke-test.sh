#
# $JAVA_HOME = JDK location (required by some AUTs)
#

#
# Required by GUITAR and dozen scripts
#
dozen_dir=$(readlink -f $0)
dozen_dir=`dirname $dozen_dir`
dozen_dir=`dirname $dozen_dir`/
aut_root=$dozen_dir/auts/
export PATH=$PATH:$dozen_dir/common/
export PATH=$PATH:$dozen_dir/tools/scripts/
export PATH=$PATH:$dozen_dir/tools/cobertura/
export PATH=$PATH:$dozen_dir/tools/bin/guitar/bin/
revision=$1
if [ -z $revision ]
then
   echo "Usage: $0 <GUITAR revision number>"
   exit 1
fi

echo "*** Debug Info ***"
revision=$revision
echo JAVA_HOME=$JAVA_HOME
pwd
ls -l
echo "*** Debug Info ***"

# Prepare GUITAR tools
echo "*** SMOKE Building GUITAR tools ***"
main.sh $revision num
ret=$?
if [ $ret -ne 0 ]
then
   exit $ret
fi

# Run each AUT
for i in RadioButton # ArgoUML Buddi CrosswordSage FreeMind JabRef OmegaT
do
   echo "*** SMOKE $i $i $i $i ***"

   common-init-dir.sh $i
   ret=$?
   if [ $ret -ne 0 ]
   then
      exit $ret
   fi

   common-checkout.sh $i
   ret=$?
   if [ $ret -ne 0 ]
   then
      exit $ret
   fi

   common-build.sh $i
   ret=$?
   if [ $ret -ne 0 ]
   then
      exit $ret
   fi

   common-inst.sh $i
   ret=$?
   if [ $ret -ne 0 ]
   then
      exit $ret
   fi

   common-build-check.sh $i
   ret=$?
   if [ $ret -ne 0 ]
   then
      exit $ret
   fi

   common-rip.sh $i NULL NULL NULL
   ret=$?
   if [ $ret -ne 0 ]
   then
      exit $ret
   fi

   common-gui2efg.sh $i new
   ret=$?
   if [ $ret -ne 0 ]
   then
      exit $ret
   fi

   common-efg2gephipdf.sh $i
   ret=$?
   if [ $ret -ne 0 ]
   then
      exit $ret
   fi

   common-tc-gen-sq.sh $i 2 0
   ret=$?
   if [ $ret -ne 0 ]
   then
      exit $ret
   fi

   common-replay.sh $i $aut_root/$i/models/$i.GUI $aut_root/$i/models/$i.EFG $aut_root/$i/testsuites/ sq_l_2 NULL NULL NULL
   ret=$?
   if [ $ret -ne 0 ]
   then
      exit $ret
   fi
done


# Tar current artifacts
echo "*** SMOKE Tar-ing artifacts ***"
if [ -e "auts-prev.tar" ]
then
   rm -f auts-prev.tar
fi
if [ -e "auts-curr.tar" ]
then
   mv auts-curr.tar auts-prev.tar
fi

tar --exclude=.svn -cf auts-curr.tar ./guitar/dozen/auts

# Compare previous and current artifacts
echo "*** SMOKE Comparing current with previous artifacts ***"
if [ -e prev ]
then
   rm -rf prev
fi
if [ -e curr ]
then
   rm -rf curr
fi

if [ -e auts-prev.tar ]
then
   mkdir prev ; cd  ./prev ; tar -xf ../auts-prev.tar ; cd ..
   mkdir curr ; cd  ./curr ; tar -xf ../auts-curr.tar ; cd ..
   diff --exclude=".svn" --exclude=logs -q -r prev/ curr/
   rm -rf prev curr
fi

# Ignore tar-ing and comparing errors
exit 0

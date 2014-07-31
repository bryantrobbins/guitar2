#!/bin/bash
# 
# xvfb start/stop Xvfb server
#
# Written by Bao N. Nguyen (2010)

case "$1" in
'start')

   DISPLAY_NUMBER=$2 
   SESSION_DIR=$3
   export LD_LIBRARY_PATH=$4
   CMD_XVFB=$5

   if [ -z $DISPLAY_NUMBER ]
   then
     echo "No DISPLAY_NUMBER. Exiting."
     exit 1
   fi

   if [ -z $SESSION_DIR ] 
   then
     echo "No SESSION_DIR. Exiting."
     exit 1
   fi
 
   if [ ! -e $SESSION_DIR ]
   then
      mkdir $SESSION_DIR
   fi

   touch /tmp/.X11-unix/$$
   if [ ! -e "/tmp/.X11-unix/$$" ]
   then
      echo FAILED No write permission on /tmp/.X11-unix
      exit 1
   fi
   rm -f /tmp/.X11-unix/$$

   if [ ! -e $LD_LIBRARY_PATH ]
   then
      echo FAILED Path $LD_LIBRARY_PATH not found
      echo FAILED Machine name `hostname`
      exit 1
   else
      nohup $CMD_XVFB :$DISPLAY_NUMBER -fbdir $SESSION_DIR -screen 0 1600x1200x16 &
      sleep 3
      cat nohup.out

      # Check if lock file was created
      ls /tmp/.X$DISPLAY_NUMBER'-lock' 2>/dev/null
      if [ $? -ne 0 ]
      then
         echo FAILED Xvfb not started. /tmp/.X$DISPLAY_NUMBER'-lock' not found
         exit 1
      fi
      ls /tmp/.X11-unix/X$DISPLAY_NUMBER  2>/dev/null
      if [ $? -ne 0 ]
      then
         echo FAILED Xvfb not started. /tmp/.X11-unix/X$DISPLAY_NUMBER not found
         exit 1
      fi

      echo Xvfb started at display number $DISPLAY_NUMBER 1600x1200x32

   fi
   ;;

'stop')

   DISPLAY_NUMBER=$2 
   if [ -z $2 ]
   then
     echo "No DISPLAY_NUMBER. Exiting."
     exit 1
   fi

   echo stopping Xvfb

   killall -9  Xvfb
   rm -f "/tmp/.X"$DISPLAY_NUMBER'-lock'
   if [ -e "/tmp/.X"$DISPLAY_NUMBER'-lock' ]
   then
     echo FAILED Could not delete .X files
     exit 1
   fi

   rm -f "/tmp/.X11-unix/X"$DISPLAY_NUMBER
   if [ -e "/tmp/.X11-unix/X"$DISPLAY_NUMBER ]
   then
     echo FAILED Could not delete .X files
     exit 1
   fi
   ;;

*)
   echo "Usage: $0 { start [Display number] | stop } <DISPLAY NUMBER> <x-session-dir> <x-lib-path> <cmd-xvfb>"
   exit 1
   ;;
esac

exit 0

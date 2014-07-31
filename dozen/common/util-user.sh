#!/bin/bash

#
# Script to create a set of users on a Linux / Mac computer.
#
# Usernames are of the type:
#    tester[n]
# They belong to the group:
#    testergroup
#
# Usage:
#
#    $0 create <ssh_keys_dir> <: separated home dirs>
#
#    OR
#
#    $0 delete
#
# Where:
#
# <ssh_keys_dir> contains the .ssh/ directory content to be copied
#                into the new users' .ssh/
#
#
# Drawbacks of this script:
#
#    * Error checking and propagation is insufficient
#

#
# Globals
#
group_str=testergroup
user_list="1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20"
username_str=tester

#####################
#        OS X       #
#####################

#
# Delete group of given string
#
function delete_group_osx
{
   exists=$(dscl . -list /Groups | grep $group_str)
   if [ "$exists" != "$group_str" ]
   then
      echo Group $group_str does not exist
      return 1
   fi

   dscl . -delete /Groups/$group_str
   return $?
}

#
# Create group of given string
#
function create_group_osx
{
   exists=$(dscl . -list /Groups | grep $group_str)
   if [ "$exists" == "$group_str" ]
   then
      echo Group $group_str already exists
      return 0
   fi

   last=$(dscl . -list /Groups PrimaryGroupID | awk '{print $2}' | sort -n | tail -1)
   gid=$((last + 1))
   dscl . -create /Groups/$group_str
   dscl . -create /Groups/$group_str PrimaryGroupID $gid

   return 0
}


#
# Get a group's gid
#
function get_group_gid
{
   g=$(dscl . -list /Groups PrimaryGroupID | grep $group_str | awk '{print $2}')
   if [ "$g" == "" ]
   then
      echo ""
   else
      echo $g
   fi
}

#
# Get a user's generated ID
#
function exists_user_osx_guid
{
   g=$(dscl . -list /Users GeneratedUID | grep $1 | awk '{print $2}')
   if [ "$g" == "" ]
   then
      echo 0    # does not exist
   else
      echo 1    # exists
   fi
}

#
# Create all users of given string
#
function create_users_osx
{
   g=$(get_group_gid)
   n=0
   for num in $user_list
   do
      u=$username_str$num
      exists_user=$(exists_user_osx_guid $u)
      if [ $exists_user -eq 0 ]
      then
         h="${home_dir[$n]}/$u"
         n=$((n+1))
         if [ $n -ge $num_home_dir ]
         then
            n=0
         fi

         last=$(dscl . -list /Users UniqueID | awk '{print $2}' | sort -n | tail -1)
         uid=$((last + 1))
         dscl . -create /Users/$u
         dscl . -create /Users/$u UserShell /bin/bash
         dscl . -create /Users/$u UniqueID $uid
         dscl . -create /Users/$u PrimaryGroupID $g
         dscl . -create /Users/$u NFSHomeDirectory $h
         createhomedir -c -u $u
         dscl . -passwd /Users/$u guitar
      else
         echo User $u already exists
      fi
   done
}

#
# Delete user $1
#
function delete_user_osx
{
   user=$1

   OIFS=$IFS
   export IFS=$' \t\n'

   # Check
   if [ -z "$user" ]
   then
      echo FAILED user not specified
      return 1
   fi

   # Search user name
   usertest=$(dscl . -search /Users name $user 2>/dev/null)
   if [ -z "$usertest" ]
   then
      echo $user does not exist
      return 0
   fi

   # get user's group memberships and remove
   groups_of_user=$(id -Gn $user)
   if [ $? -eq 0 ] && [ -n "$(dscl . -search /Groups GroupMembership $user)" ]
   then 
      # delete the user's group memberships
      for group in $groups_of_user
      do
         dscl . -delete /Groups/$group  GroupMembership $user
         if [ $? -ne 0 ]
         then
            echo WARN failed removing GroupMembership $group for $user
         fi
      done
   fi

   # delete the user's primary group
   if [[ -n "$(dscl . -search /Groups name "$user")" ]]
   then
      dseditgroup -o delete "$user"
      if [ $? -ne 0 ]
      then
         echo FAILED deleting user
         return 1
      fi
   fi

   # if the user's primary group has not been deleted ...
   if [[ -n "$(/usr/bin/dscl . -search /Groups name "$user")" ]]
   then
      echo FAILED primary group not deleted
      return 1
   fi


   # delete the user
   dscl . -delete "/Users/$user"
   if [ $? -ne 0 ]
   then
      return 1
   fi

   # remove the user's home directory
   if [[ -d "/Users/$user" ]]; then
      rm -rf "/Users/$user"
      if [ $? -ne 0 ]
      then
         return 1
      fi
   fi

   export IFS=$OIFS
   return 0
}

#
# Delete all users of given string
#
function delete_users_osx
{
   for num in $user_list
   do
      delete_user_osx $username_str$num
      if [ $? -ne 0 ]
      then
         return 1
      fi
   done
}


#####################
#     Linix         #
#####################

#
# Delete group of given string
#
function delete_group_linux
{
   exists=$(egrep -i "^$group_str" /etc/group)
   if [ $? -ne 0 ]
   then
      echo Group $group_str does not exist
      return 1
   fi

   groupdel $group_str
   return $?
}

#
# Create group of given string
#
function create_group_linux
{
   exists=$(egrep -i "^$group_str" /etc/group)
   if [ $? -eq 0 ]
   then
      echo Group $group_str already exists
      return 0
   fi

   groupadd $group_str
   return $?
}

#
# Test if a username exists
#
function exists_user_linux
{
   id $1 > /dev/null 2>&1
   if [ $? -eq 0 ]
   then
      echo 1    # exists
   else
      echo 0    # does not exist
   fi
}

#
# Create all users of given string
#
function create_users_linux
{
   n=0
   for num in $user_list
   do
      u=$username_str$num
      h="${home_dir[$n]}/$u"
      n=$((n+1))
      if [ $n -ge $num_home_dir ]
      then
         n=0
      fi

      exists_user=$(exists_user_linux $u)
      if [ $exists_user -eq 0 ]
      then
         useradd -d "$h" -G $group_str -p "guitar" -m $u
         if [ $? -ne 0 ]
         then
            return 1
         fi
      else
         echo User $u already exists
      fi
   done

   return 0
}


#
# Delete all users of given string
#
function delete_users_linux
{
   for num in $user_list
   do
      u=$username_str$num
      userdel -r $u
      if [ $? -ne 0 ]
      then
         echo FAILED deleting $u
         return 1
      fi
   done
}

#####################
#     Common        #
#####################

#
# Copy .ssh keys to .ssh keys directory
#
function copy_ssh_keys
{
   echo "*** Creating user ssh keys ***"
   n=0
   for num in $user_list
   do
      u=$username_str$num
      h="${home_dir[$n]}/$u"
      if [ ! -d "$h" ]
      then
         echo FAILED home dir $h does not exist
      fi

      n=$((n+1))
      if [ $n -ge $num_home_dir ]
      then
         n=0
      fi

      mkdir $h/.ssh/
      cp $ssh_keys_dir/* $h/.ssh/
      chown $u $h/.ssh/*
      chown $u $h/.ssh
      chmod 600 $h/.ssh/id_rsa
      ls -ld $h/.ssh
   done
}

#####################
#      Entry        #
#####################

cmd=$1
ssh_keys_dir=$2
home_dir_list=$3
ret=0

#
# Sanity check
#
if [ "$cmd" == "" ]
then
   echo "Usgae: $0 [create|delete] [<ssh_keys_directory>]"
   exit 1
fi

#
# If specified, replace ":" with " " in home dirs
#
home_dir_list=`echo "$home_dir_list" | awk 'BEGIN{FS=":"}{for (i=1; i<=NF; i++) print $i}'`

#
# If specified, Count number of home_dirs
#
num_home_dir=0
home_dir[0]=""
for hd in $home_dir_list
do
   if [ -d "$hd" ]
   then
      home_dir[$num_home_dir]=$hd
      num_home_dir=$((num_home_dir + 1))
   else
      echo FAILED $hd does not exist
      exit 1
   fi
done


# Determine OS type, Linux or Mac OS
hostos=`uname`
os=""
if [ "$hostos" == "Linux" ]
then
   # Linux
   os="linux"
else
   # Mac 
   os="osx"
fi

#
# Test first parameter and
#    * create group + users
#    * delete group + users
#
if [ "$cmd" == "create" ]
then
   if [ "$ssh_keys_dir" == "" ] || [ ! -d "$ssh_keys_dir" ] ||
      [ $num_home_dir -eq 0 ]
   then
      echo "Usgae: $0 [create|delete] [<ssh_keys_directory>] [: separated home dirs]"
      exit 1
   fi

   if [ $os == "osx" ]
   then
      create_group_osx
      ret=$?
      if [ $ret -eq 0 ]
      then
         create_users_osx
         copy_ssh_keys
      fi
   else # [ os == "Linux" ]
      create_group_linux
      ret=$?
      if [ $ret -eq 0 ]
      then
         create_users_linux
         if [ $? -eq 0 ]
         then
            copy_ssh_keys
         fi
      fi
   fi
fi # create

if [ "$1" == "delete" ]
then
   if [ $os == "osx" ]
   then
      delete_users_osx
      ret=$?
      if [ $ret -eq 0 ]
      then
         delete_group_osx
      else
         echo FAILED deleting users
      fi
   else # [ os == "Linux" ]
      delete_users_linux
      ret=$?
      if [ $ret -eq 0 ]
      then
         delete_group_linux
      else
         echo FAILED deleting users
      fi
   fi
fi # delete

exit $ret

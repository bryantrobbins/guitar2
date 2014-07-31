#!/bin/bash

groupId=$1
artifactId=$2
version=$3

server="http://gollum.cs.umd.edu:8080"
uiUrl="$server/archiva/browse/$groupId/$artifactId/$version"
jarPath=`curl $uiUrl | sed -rn 's/.+href=\"(.+\.jar)\" title=.+\".+/\1/p'`
jarUrl="$server$jarPath"
wget -O $artifactId.jar $jarUrl

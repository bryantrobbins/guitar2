#!/bin/bash

export JAVA_HOME="/usr/lib/jvm/java-1.7.0-openjdk-amd64"

rm -rf coverage logs oracles *.ser *.ll $HOME/.ivy2/cache/*

#export JAVA_OPTS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005

./common/workflow-amalga-replay.sh JabRef $JAVA_HOME /home/bryan/.m2 /home/bryan/code gollum.cs.umd.edu 27017 test_suites_for_pitest test_333 e001 | tee replay.ll

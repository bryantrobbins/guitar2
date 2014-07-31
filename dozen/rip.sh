#!/bin/bash

rm -rf coverage logs oracles *.ser *.ll $HOME/.ivy2/cache/*

export JAVA_HOME=/usr/lib/jvm/java-1.7.0-openjdk-amd64
#export JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005 -Dlog4j.configuration=log/guitar-clean.glc"

xvfb-run -a ./common/workflow-amalga.sh JabRef $JAVA_HOME /ignore /home/guitar /tmp/bryan_11111 test_suites_for_pitest JabRef_input test_666 20 | tee rip.ll

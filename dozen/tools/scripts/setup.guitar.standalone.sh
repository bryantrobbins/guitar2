#!/bin/bash

ivy_loc="`dirname $0`/../ivy"
guitar_loc="`dirname $0`/../amalga"

rm -rf $guitar_loc/jars/*

java -jar $ivy_loc/ivy-2.3.0.jar -settings $ivy_loc/ivysettings.xml -dependency edu.umd.cs.guitar gui-ripper-jfc 0.0.1-SNAPSHOT -retrieve "$guitar_loc/jars/[artifact].[ext]"
java -jar $ivy_loc/ivy-2.3.0.jar -settings $ivy_loc/ivysettings.xml -dependency edu.umd.cs.guitar ome-replayer-jfc 0.0.1-SNAPSHOT -retrieve "$guitar_loc/jars/[artifact].[ext]"

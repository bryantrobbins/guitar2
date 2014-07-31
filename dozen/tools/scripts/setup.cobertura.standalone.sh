#!/bin/bash

cob_loc="`dirname $0`/../cobertura-standalone"
java -jar $ivy_loc/ivy-2.3.0.jar -settings $ivy_loc/ivysettings.xml -dependency edu.umd.cs.guitar cobertura 1.10-SNAPSHOT -retrieve "$cob_loc/lib/[artifact]-[revision](-[classifier]).[ext]"


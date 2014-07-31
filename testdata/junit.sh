#!/bin/bash

props=$1
lib=lib

cmd="java -Dguitar.properties=$props -cp target/testdata-1.0.8-SNAPSHOT.jar org.junit.runner.JUnitCore edu.umd.cs.guitar.testdata.guitar.GUITARTests"
echo $cmd
eval $cmd

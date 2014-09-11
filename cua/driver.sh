#!/bin/bash

groovyroot=src/main/groovy/edu/umd/cs/guitar/testdata/weblog
cmd="groovy $groovyroot/WeblogDataHandler.groovy"


# Arguments:
# args[0] : MongoDB host
# args[1] : MongoDB port
# args[2] : "true" for resetting data from disk
# args[3] : Database ID for Mongo; if reset true, add timestamp tag and use as DB for saving data. If reset NOT true, use as DB directly
# args[4] : Path to config file; if reset true, this is used to save file; otherwise, used to load file
# args[5] : root directory of Keyword files to use if reset true

$cmd cuamongo 27017 true weblog_inputs keywords.ini Keywords_7

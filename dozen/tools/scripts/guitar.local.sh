#!/bin/bash

script_root_dir=`dirname $0`
source "$script_root_dir/tools.cfg"
source "$script_root_dir/utils.sh"

cp -r /home/vagrant/data/guitar/* $guitar_dir

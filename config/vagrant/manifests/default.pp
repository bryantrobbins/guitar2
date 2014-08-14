# create a new run stage to ensure certain modules are included first
stage { 'pre':
  before => Stage['main']
}

# add the baseconfig module to the new 'pre' run stage
class { 'baseconfig':
  stage => 'pre'
}

# set defaults for file ownership/permissions
File {
  owner => 'root',
  group => 'root',
  mode  => '0644',
}

if $hostname == 'jenkins' {
	include jenkins
	include jenkins::master
}

if $hostname =~ /^slave/ {
  class { 'jenkins::slave':
    masterurl => 'http://192.168.33.10:8080',
  }
}

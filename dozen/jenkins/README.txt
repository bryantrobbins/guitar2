This directory contains scripts for executing sanity tests on the "Maryland Dozen" set of AUTs and GUITAR. Some of the tests may be hooks into test harnesses such as Jenkins and might not be executable from the command line.


*** Jenkins jobs ***

build-smoke-test.sh      - Jenkins "dozen" build smoke test
cluster-smoke-test.sh    - Jenkins skoll cluster smoke test
cluster-run-testsuite.sh - Run a testsuite for an AUT on the
                              skoll cluster and collect results
                              in a specified archive location

*** Jenkins configurations ***

config/
 - jenkins configuration files backup

*** SSH keys ***

ssh_keys/
 - preconfigured SSH keys usable on specific testbed

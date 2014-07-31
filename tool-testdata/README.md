guitar-testdata
===============

mongodb-backed tool for managing artifacts from GUITAR tools (guitar.sf.net)

GUITAR is a framework for model-based testing of GUI-driven software. I am using GUITAR to perform-large scale
empirical studies of modeling and testing alternatives, and would like to be able to systematically index and
query results.

The code consists of several components:
* TestDataManager - tools for indexing basic GUITAR artifacts into test suitesand deriving 
suite-level artifacts (e.g., N-gram models, coverage reports).
* Reducer - tools for reducing test suites based on various algorithms.
* Analyzer - tools for profiling test suites.


The codebase also contains various utilities for working with GUITAR, MongoDB, Cobertura, BerkeleyLM, and Jenkins,
and depends directly on a number of these popular libraries.

* GUITAR: http://guitar.sf.net/
* MongoDB: http://www.mongodb.org/
* Cobertura: http://cobertura.github.io/cobertura/
* BerkeleyLM: https://code.google.com/p/berkeleylm/
* Jenkins: http://jenkins-ci.org/

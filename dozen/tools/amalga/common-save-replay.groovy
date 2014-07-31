#!/usr/bin/env groovy

@GrabResolver('http://gollum.cs.umd.edu:8080/archiva/repository/snapshots/')
@GrabResolver('http://gollum.cs.umd.edu:8080/archiva/repository/internal/')
@Grab(group='edu.umd.cs.guitar', module='testdata', version='1.0.7-SNAPSHOT')
import edu.umd.cs.guitar.testdata.TestDataManager;
import edu.umd.cs.guitar.testdata.guitar.*;

def dbId = args[0]
def testId = args[1]
def execId = args[2]
def covLoc = args[3]
def logLoc = args[4]

println(args)
println("Let's save!")
TestDataManager tdm = new TestDataManager("gollum.cs.umd.edu", 27017)
tdm.addArtifactToExecution(dbId, testId, execId, covLoc, CoverageProcessor.class);
tdm.addArtifactToExecution(dbId, testId, execId, logLoc, LogProcessor.class);

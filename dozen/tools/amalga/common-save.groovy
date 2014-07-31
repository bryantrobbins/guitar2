#!/usr/bin/env groovy

@GrabResolver('http://gollum.cs.umd.edu:8080/archiva/repository/snapshots/')
@GrabResolver('http://gollum.cs.umd.edu:8080/archiva/repository/internal/')
@Grab(group='edu.umd.cs.guitar', module='testdata', version='1.0.7-SNAPSHOT')
import edu.umd.cs.guitar.testdata.TestDataManager;

/* Arguments needed
 * dbId
 * suiteId
 * testCaseId
 * coverageFileLoc
 * userHome dir
 * GUITAR args
 */

def dbId = args[0]
def suiteId = args[1]
def testId = args[2]
def testLoc = args[3]
def mapLoc = args[4]
def guiLoc = args[5]
def covLoc = args[6]

println(args)
println("Let's save!")
TestDataManager tdm = new TestDataManager("gollum.cs.umd.edu", 27017)
tdm.addTestCaseFromFiles(dbId, suiteId, testId, testLoc, mapLoc, guiLoc, covLoc)

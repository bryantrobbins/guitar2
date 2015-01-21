// HOST, PORT, DB_ID, BUNDLE_ID, SUITE_ID

import edu.umd.cs.guitar.main.TestDataManager
import edu.umd.cs.guitar.main.TestDataManagerCollections
import edu.umd.cs.guitar.processors.guitar.LogProcessor
import edu.umd.cs.guitar.artifacts.ArtifactCategory
import edu.umd.cs.guitar.processors.applog.TextObject

String host = args[0]
String port = args[1]
String dbId = args[2]
String bundleId = args[3]
String suiteId = args[4]

// TestDataManager
def manager = new TestDataManager(host, port, dbId)
def logProc = new LogProcessor()

println "Analyzing bundle " + bundleId
println "The collection is " + TestDataManagerCollections.idsInBundle(bundleId)

List<String> failing = []
List<String> passing = []
List<String> missingLog = []
List<String> missingExec = []

def testIds = manager.getTestIdsInSuite(suiteId)
println "There are " + testIds.size() + " tests in suite " + suiteId

for(String testId : manager.getTestIdsInSuite(suiteId)){
	String execId = manager.getExecutionIdForTestIdInBundle(bundleId, testId)
	
	if(execId == null){
		missingExec.add(testId)
		continue
	}

	TextObject logObject = manager.getArtifactByCategoryAndOwnerId(ArtifactCategory.TEST_OUTPUT,
																																 execId, logProc)
	if(logObject == null){
		missingLog.add(testId)
	} else {
		boolean didPass = pass(logObject)
		if(didPass){
			passing.add(testId)
		} else{
			failing.add(testId)
		}
	}
}

def pass(obj){
	for(int i=0; i<obj.size(); i++){
		String line = obj.getLine(i)
		if (line.toUpperCase().contains("ERROR")){
			return false
		}
	}
	return true
}

println "========FAILING========"
println "Failing test count=" + failing.size()
println failing
println "========ERROR-E========"
println "Error execution missing count=" + missingExec.size()
println missingExec
println "========ERROR-L========"
println "Error log missing count=" + missingLog.size()
println missingLog
println "========PASSING========"
println "Passing test count=" + passing.size()
println passing

println "DONE with analysis"

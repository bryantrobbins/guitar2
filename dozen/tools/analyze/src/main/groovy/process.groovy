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
List<String> componentDisabled = []
List<String> componentNotFound = []
List<String> stepTimeout = []

def testIds = manager.getTestIdsInSuite(suiteId)
println "There are " + testIds.size() + " tests in suite " + suiteId

int count = 0
for(String testId : manager.getTestIdsInSuite(suiteId)){
	count++
	if(count % 50 == 0){
		println "."
	}
	String execId = manager.getExecutionIdForTestIdInBundle(bundleId, testId)
	
	if(execId == null){
		missingExec.add(testId)
		continue
	}

	TextObject logObject = manager.getArtifactByCategoryAndOwnerId(ArtifactCategory.TEST_OUTPUT,
																																 execId, logProc)
	if(logObject == null){
		missingLog.add(testId)
		continue
	}

	def reason = pass(logObject)
	if(reason == null){
			passing.add(testId)
			continue
	}

	def record = [testId: testId, execId: execId, reason: reason]

	if(reason.contains("ComponentDisabled")){
		componentDisabled.add(record)
		continue
	}

	if(reason.contains("ComponentNotFound")){
		componentNotFound.add(record)
		continue
	}

	if(reason.contains("TIMEOUT")){
		stepTimeout.add(record)
		continue
	}

	failing.add(record)

}

def pass(obj){
	def pattern = ~/((?:[a-zA-Z]{3} \d{1,2}, \d{4,4} \d{1,2}:\d{2}:\d{2} (AM|PM) (\(SEVERE\)|\(ERROR\))).*\r(?:(.*Exception.*(\r.*)(\tat.*\r)+)))|((?:[a-zA-Z]{3} \d{1,2}, \d{4,4} \d{1,2}:\d{2}:\d{2} (AM|PM) (\(SEVERE\)|\(ERROR\))).*)/
	for(int i=0; i<obj.size(); i++){
		String line = obj.getLine(i)
		if (line.toUpperCase().contains("COMPONENTDISABLED")){
			return line
		}
		if (line.toUpperCase().contains("COMPONENTNOTFOUND")){
			return line
		}
		if (line.toUpperCase().contains("STEP TIMER: TIMEOUT!!!")){
			return line
		}
		if (pattern.matcher(line).matches()){
			return line
		}
		if (line.toUpperCase().contains(":REPLAY FAILED")){
			return line
		}
	}
	return null
}

println "========PASSING========"
println "Passing test count=" + passing.size()
println "========FAILING========"
println "Failing test count=" + failing.size()
println failing
println "========ERROR-E========"
println "Error execution missing count=" + missingExec.size()
println "========ERROR-L========"
println "Error log missing count=" + missingLog.size()
println "========ERROR-D========"
println "Error component disabled count=" + componentDisabled.size()
println "========ERROR-C========"
println "Error component not found count=" + componentNotFound.size()
println "========ERROR-T========"
println "Error step timeout count=" + stepTimeout.size()

println "DONE with analysis"

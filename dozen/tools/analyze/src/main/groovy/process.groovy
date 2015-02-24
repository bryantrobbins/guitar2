// HOST, PORT, DB_ID, SUITE_ID, BUNDLE_ID_1, BUNDLE_ID_2, BUNDLE_ID_3

import edu.umd.cs.guitar.main.TestDataManager
import edu.umd.cs.guitar.main.TestDataManagerCollections
import edu.umd.cs.guitar.processors.guitar.LogProcessor
import edu.umd.cs.guitar.processors.guitar.TestcaseProcessor
import edu.umd.cs.guitar.processors.guitar.EFGProcessor
import edu.umd.cs.guitar.processors.guitar.GUIProcessor
import edu.umd.cs.guitar.artifacts.ArtifactCategory
import edu.umd.cs.guitar.processors.applog.TextObject

import edu.umd.cs.guitar.model.data.TestCase

String host = args[0]
String port = args[1]
String dbId = args[2]
String suiteId = args[3]
String bundleId_1 = args[4]
String bundleId_2 = args[5]
String bundleId_3 = args[6]

// TestDataManager
def manager = new TestDataManager(host, port, dbId)
def logProc = new LogProcessor()

def results1 = analyzeBundle(manager, logProc, suiteId, bundleId_1)
def results2 = analyzeBundle(manager, logProc, suiteId, bundleId_2)
def results3 = analyzeBundle(manager, logProc, suiteId, bundleId_3)

def resultsCombined = [:]

resultsCombined["feasible"] = []
resultsCombined["infeasible"] = []
resultsCombined["inconsistent"] = []

// Loop over Ids in suite
for(String testId : manager.getTestIdsInSuite(suiteId)){
	int passCount = 0
	if(results1["passing"].contains(testId)){
		passCount++
	}
	if(results2["passing"].contains(testId)){
		passCount++
	}
	if(results3["passing"].contains(testId)){
		passCount++
	}
	if(passCount == 3){
		resultsCombined["feasible"].add(testId)
	} else if(passCount == 0){
		resultsCombined["infeasible"].add(testId)
	} else {
		resultsCombined["inconsistent"].add(testId)
	}
}

nFeasible = resultsCombined["feasible"].size()
nInfeasible = resultsCombined["infeasible"].size()
nInconsistent = resultsCombined["inconsistent"].size()

println "Feasible,Infeasible,Inconsistent"
println "${nFeasible},${nInfeasible},${nInconsistent}"

createPassingSuite(manager, resultsCombined["feasible"], suiteId)
createCombinedSuite(manager, resultsCombined["feasible"], suiteId)

def analyzeBundle(manager, logProc, suiteId, bundleId){
	println "Analyzing bundle " + bundleId
	def results = [:]
	results["failing"] = []
	results["passing"] = []
	results["missingLog"] = []
	results["missingExec"] = []
	results["componentDisabled"] = []
	results["componentNotFound"] = []
	results["stepTimeout"] = []

	int count = 0
	for(String testId : manager.getTestIdsInSuite(suiteId)){
		count++
		if(count % 50 == 0){
			println "."
		}
		String execId = manager.getExecutionIdForTestIdInBundle(bundleId, testId)
	
		if(execId == null){
			results["missingExec"].add(testId)
			continue
		}

		TextObject logObject = manager.getArtifactByCategoryAndOwnerId(ArtifactCategory.TEST_OUTPUT,
																																 execId, logProc)
		if(logObject == null){
			results["missingLog"].add(testId)
			continue
		}

		def reason = pass(logObject)
		if(reason == null){
				results["passing"].add(testId)
				continue
		}

		def record = [testId: testId, execId: execId, reason: reason]

		if(reason.contains("ComponentDisabled")){
			results["componentDisabled"].add(record)
			continue
		}

		if(reason.contains("ComponentNotFound")){
			results["componentNotFound"].add(record)
			continue
		}

		if(reason.contains("TIMEOUT")){
			results["stepTimeout"].add(record)
			continue
		}

		results["failing"].add(record)
	}
	return results
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

def createPassingSuite(manager, passing, suiteId){
	String psId = "${suiteId}_passing"
	manager.createNewSuite(psId)
	println "Passing suite ${psId} created"

	EFGProcessor efgProc = new EFGProcessor(manager.getDb());
	GUIProcessor guiProc = new GUIProcessor(manager.getDb());

	// Save over the EFG and GUI
	manager.copyArtifact(ArtifactCategory.SUITE_INPUT, psId, suiteId, efgProc, null)
	manager.copyArtifact(ArtifactCategory.SUITE_INPUT, psId, suiteId, guiProc, null)

	for(String id : passing){
		manager.addTestCaseToSuite(id, psId)
	}
}

def createCombinedSuite(manager, passing, suiteId){
	// Build processors for these objects
	EFGProcessor efgProc = new EFGProcessor(manager.getDb());
	GUIProcessor guiProc = new GUIProcessor(manager.getDb());
	TestcaseProcessor tcProc = new TestcaseProcessor()

	String cbId = "${suiteId}_combined"
	manager.createNewSuite(cbId)
	println "Combined suite ${cbId} created"

	// Save the EFG and GUI
	manager.copyArtifact(ArtifactCategory.SUITE_INPUT, cbId, suiteId, efgProc, null)
	manager.copyArtifact(ArtifactCategory.SUITE_INPUT, cbId, suiteId, guiProc, null)

	// Create all possible combinations
	for(String aid : passing){
		for(String bid : passing){
			if(!aid.equals(bid)){
				// Build a concat
				TestCase a = manager.getArtifactByCategoryAndOwnerId(ArtifactCategory.TEST_INPUT,
                                                  aid, tcProc)
				TestCase b = manager.getArtifactByCategoryAndOwnerId(ArtifactCategory.TEST_INPUT,
                                                  bid, tcProc)
				TestCase c = TestcaseProcessor.concat(a, b)
				String cid = "${aid}_CONCAT_${bid}"
				manager.saveArtifact(ArtifactCategory.TEST_INPUT, tcProc, c, cid)
				manager.addTestCaseToSuite(cid, cbId)
			}
		}
	}
}


// HOST, PORT, DB_ID, BUNDLE_ID

import edu.umd.cs.guitar.main.TestDataManager
import edu.umd.cs.guitar.processors.guitar.LogProcessor
import edu.umd.cs.guitar.artifacts.ArtifactCategory
import edu.umd.cs.guitar.processors.applog.TextObject

String host = args[0]
String port = args[1]
String dbId = args[2]
String bundleId = args[3]

// TestDataManager
def manager = new TestDataManager(host, port, dbId)
def logProc = new LogProcessor()

println "Analyzing bundle " + bundleId

List<String> failing = []
List<String> passing = []
for(String execId : manager.getExecutionIdsInBundle(bundleId)){
	// Get log object
	TextObject logObject = manager.getArtifactByCategoryAndOwnerId(ArtifactCategory.TEST_OUTPUT,
                                                  execId, logProc)
	if(!pass(logObject)){
		failing.add(execId)
	} else{
		passing.add(execId)
	}
}

def pass(obj){
	for(int i=0; i<obj.size(); i++){
		String line = obj.getLine(i)
		if (line.toUpper().contains("ERROR")){
			return false
		}
	}
	return true
}

println "========FAILING========"
println "Failing test count=" + failing.size()
println failing
println "========PASSING========"
println "Passing test count=" + passing.size()
println passing

println "DONE with analysis"

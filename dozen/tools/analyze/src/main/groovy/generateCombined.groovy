// HOST, PORT, DB_ID, SUITE_ID, RESULT_ID, SIZE

import edu.umd.cs.guitar.main.ExperimentManager
import edu.umd.cs.guitar.main.TestDataManager

String host = args[0]
String port = args[1]
String dbId = args[2]
String suiteId = args[3]
String resultId = args[4]
int size = Integer.parseInt(args[5])
println args

// Build a manager
def tdm = new TestDataManager(host, port, dbId)

// Generate combined suite
ExperimentManager.generateCombinationSuite(tdm, suiteId, resultId, size)

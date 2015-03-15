// HOST, PORT, DB_ID, SUITE_ID

import edu.umd.cs.guitar.main.ExperimentManager

String host = args[0]
String port = args[1]
String dbId = args[2]
String suiteId = args[3]
println args

// Post results
ExperimentManager.addFeaturesToSuite(host, port, dbId, suiteId)

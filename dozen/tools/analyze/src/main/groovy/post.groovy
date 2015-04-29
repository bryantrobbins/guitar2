// HOST, PORT, DB_ID, SUITE_ID, BUNDLE_LIST

import edu.umd.cs.guitar.main.ExperimentManager

String host = args[0]
String port = args[1]
String dbId = args[2]
String suiteId = args[3]
def bundles = Arrays.asList(args[4].split(","))
println args

// Post results
ExperimentManager.postResults(host, port, dbId, suiteId, bundles)

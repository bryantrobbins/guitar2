// HOST, PORT, DB_ID, SUITE_ID

import edu.umd.cs.guitar.main.ExperimentManager

String host = args[0]
String port = args[1]
String dbId = args[2]
def suites = Arrays.asList(args[3].split(","))
println args

// Post results
ExperimentManager.addGlobalFeaturesForSuites(host, port, dbId, suites)

// HOST, PORT, DB_ID, SUITE_ID

import edu.umd.cs.guitar.main.ExperimentManager

String host = args[0]
String port = args[1]
String dbId = args[2]
def suites = Arrays.asList(args[3].split(","))
int maxN = Integer.parseInt(args[4])
println args

// Save some properties of this execution
Properties groupIds = new Properties();

for(n in 0..maxN+1) {
	println "N=${n}"

	// Extract features and post to DB
	def id = ExperimentManager.addGlobalFeaturesForSuites(host, port, dbId, suites, n)

	// Save the group ID for future reference
	groupIds.setProperty("features_n_${n}", id)	
}

// Dump properties
groupIds.store(new FileOutputStream("groups.properties"), null);

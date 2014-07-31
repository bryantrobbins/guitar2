#!/usr/bin/env groovy

@GrabResolver('http://guitar04.cs.umd.edu:9999/archiva/repository/snapshots/')
@GrabResolver('http://guitar04.cs.umd.edu:9999/archiva/repository/internal/')
@Grab(group='edu.umd.cs.guitar', module='testdata', version='1.0.7-SNAPSHOT')

import edu.umd.cs.guitar.testdata.TestDataManager
import edu.umd.cs.guitar.testdata.Reducer
import edu.umd.cs.guitar.testdata.jenkins.JenkinsClient

dbId = args[0]
autId = args[1]
numGroups=Integer.parseInt(args[2])
numTrials=Integer.parseInt(args[3])
maxOrderReduce=Integer.parseInt(args[4])
maxOrderAnalyze=Integer.parseInt(args[5])

mongoHost="guitar04.cs.umd.edu"
mongoPort=27017
TestDataManager tdm = new TestDataManager(mongoHost, mongoPort)

List<String> suites = [];
println "Getting suite IDs"
// Get list of all suite IDs
for(int i=0; i<numGroups; i++){
	String groupId = autId + "_sampled_" + i
	String hgsPrefix = groupId + "_hgs_code"
	String probTrialPrefix = groupId + "_prob_order_"

	String suiteId = ""
	for(int k=0; k<numTrials; k++){
		// Schedule HGS Code Coverage Reduction Job
		suiteId = hgsPrefix + "_trial_" + k
		suites.add(suiteId)
		
		// Schedule Probability Reduction Job	
		for(int j=2; j<=maxOrderReduce; j++){
			suiteId = probTrialPrefix + j + "_trial_" + k
			suites.add(suiteId)
		}
	}
}

// Build a list of the props stored during analysis
// These are the ones we want for the report
wanted = ["reduction_time", "size", "coverage_score"]
for(int i=2; i<=maxOrderAnalyze; i++){
	wanted.add("model_" + i + "_score")
}


// Build a list of props stored with each line
// These are props which ID a group * reduction * trial
per = ["suiteId", "autId", "input", "type", "trial"]

allProps = []

println "We have " + suites.size() + " suites"

// Get the property values
for(String suiteId : suites){
	// Get properties
	
	// Generate a couple of additional, identifying properties
	// [0] = autId
	// [1] = sampled
	// [2] = group #
	// [3] = hgs/prob
	// [4] = code/order #
	// [5] = trial
	// [6] = trial #

	Map<String, String> properties = new HashMap<String, String>();

	ids = suiteId.split("_")

	properties.put("suiteId", suiteId)
	properties.put("autId", ids[0])
	properties.put("input", ids[2])
	if(ids[3].equals("hgs")){
		properties.put("type", "COV")
		properties.put("trial", ids[6])
	} else {
		properties.put("type", "N" + ids[5])
		properties.put("trial", ids[7])
	}

	for(String w : wanted){
		val = tdm.getSuiteProperty(dbId,suiteId,w)
		properties.put(w, val)
	}

	allProps.add(properties)
}

// #TODO: Build the header row

println "Writing CSV file"
println allProps.size()

// Build a CSV file, one line per each sample/trial/reduction combo
new File("report.csv").withWriter { out ->
	// Build header row
	out.writeLine("SUITE_ID,AUT_ID,GROUP_ID,REDUCTION_TYPE,TRIAL_ID,METRIC_ID,VALUE")
	for(Map<String, String> props : allProps){
		for (String key : props.keySet()){
			if(!per.contains(key)){
				out.writeLine(props.get("suiteId")+","+props.get("autId")+","+props.get("input")+","+props.get("type")+","+props.get("trial")+","+key+","+props.get(key))
			}
		}
	}
}

println("Done")

#!/usr/bin/env groovy

@GrabResolver('http://guitar04.cs.umd.edu:9999/archiva/repository/snapshots/')
@GrabResolver('http://guitar04.cs.umd.edu:9999/archiva/repository/internal/')
@Grab(group='edu.umd.cs.guitar', module='testdata', version='1.0.7-SNAPSHOT')

import edu.umd.cs.guitar.testdata.TestDataManager
import edu.umd.cs.guitar.testdata.Reducer
import edu.umd.cs.guitar.testdata.jenkins.JenkinsClient

dbId = args[0]
autId = args[1]
poolId = args[2]
sampleSize=Integer.parseInt(args[3])
numGroups=Integer.parseInt(args[4])
numTrials=Integer.parseInt(args[5])
maxOrderReduce=Integer.parseInt(args[6])
maxOrderAnalyze=Integer.parseInt(args[7])
threshold=args[8]

mongoHost="guitar04.cs.umd.edu"
mongoPort=27017
jenkinsHost="guitar04.cs.umd.edu"
jenkinsPort="8888"
jenkinsPath="jenkins"
jenkinsUser="runner"
jenkinsPass="holdyourpeace"
jenkinsJob="Reduce_And_Analyze"

TestDataManager tdm = new TestDataManager(mongoHost, mongoPort)
Reducer red = new Reducer(dbId,mongoHost, mongoPort)
jenkinsClient = new JenkinsClient(jenkinsHost, jenkinsPort, jenkinsPath, jenkinsUser, jenkinsPass)

// These properties will be written out
def results = new Properties()
results.setProperty("dbId", dbId)
results.setProperty("autId", autId)
results.setProperty("poolId", poolId)
results.setProperty("sampleSize", "" + sampleSize)
results.setProperty("numGroups", "" + numGroups)
results.setProperty("numTrials", "" + numTrials)
results.setProperty("maxOrderReduce", "" + maxOrderReduce)
results.setProperty("maxOrderAnalyze", "" + maxOrderAnalyze)
results.setProperty("threshold", threshold)

// For each sampled group
for(int i=0; i<numGroups; i++){
	String groupId = autId + "_sampled_" + i
	String hgsPrefix = groupId + "_hgs_code"
	String probTrialPrefix = groupId + "_prob_order_"
	tdm.clearTestSuite(dbId, groupId)

	// Create the sampled group
	println("Creating sampled group " + i)
	red.reduceSuiteBySampling(poolId, groupId, sampleSize)

	String suiteId = ""
	for(int k=0; k<numTrials; k++){
		// Schedule HGS Code Coverage Reduction Job
		suiteId = hgsPrefix + "_trial_" + k
		jenkinsClient.submitJob(jenkinsJob, [DB_ID: dbId, INPUT_SUITE_ID: groupId, OUTPUT_SUITE_ID: suiteId, ORDER: "-1", THRESHOLD: "-1.0", MAX_ORDER_ANALYZE: "" + maxOrderAnalyze, TYPE: "hgs-code"])
		
		// Schedule Probability Reduction Job	
		for(int j=2; j<=maxOrderReduce; j++){
			println("Trial " + k + ": Reducing by Probability Order " + j)
			suiteId = probTrialPrefix + j + "_trial_" + k
			jenkinsClient.submitJob(jenkinsJob, [DB_ID: dbId, INPUT_SUITE_ID: groupId, OUTPUT_SUITE_ID: suiteId, ORDER: ""+j, THRESHOLD: threshold, MAX_ORDER_ANALYZE: "" + maxOrderAnalyze, TYPE: "prob"])
		}
	}
}

// Write out properties file
results.store(new FileOutputStream("samples.properties"), "Sample and schedule groups for reduction and analysis")
println("Done")

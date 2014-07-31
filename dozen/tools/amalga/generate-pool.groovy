#!/usr/bin/env groovy

@GrabResolver('http://gollum.cs.umd.edu:8080/archiva/repository/snapshots/')
@GrabResolver('http://gollum.cs.umd.edu:8080/archiva/repository/internal/')
@Grab(group='edu.umd.cs.guitar', module='testdata', version='1.0.7-SNAPSHOT')

import edu.umd.cs.guitar.testdata.jenkins.JenkinsClient;
import edu.umd.cs.guitar.testdata.TestDataManager;

dbId = args[0]
aut=args[1]
suiteLabel = args[2]
poolSize=Integer.parseInt(args[3])
testLength=args[4]
clearPool=args[5]

jenkinsHost="gollum.cs.umd.edu"
jenkinsPort="7777"
jenkinsPath="jenkins"
jenkinsUser="testrunner"
jenkinsPass="holdyourpeace"

TestDataManager tdm = new TestDataManager("gollum.cs.umd.edu", 27017)
jenkinsClient = new JenkinsClient(jenkinsHost, jenkinsPort, jenkinsPath, jenkinsUser, jenkinsPass)

suiteId = aut + "_" + suiteLabel

// These properties will be written out
def poolProps = new Properties()
poolProps.setProperty("aut", aut)
poolProps.setProperty("dbId", dbId)
poolProps.setProperty("suiteId", suiteId)

if(clearPool.equalsIgnoreCase("yes")){
	println("Clearing existing test suite " + suiteId)
	tdm.clearTestSuite(dbId, suiteId)
}

for(int i=0; i<poolSize; i++){
	testId = TestDataManager.generateId(aut + "_test_")
	poolProps.setProperty("test." + i, testId)

	def jobParams = new HashMap<String, String>();
	jobParams.put("AUT_NAME", aut)
	jobParams.put("DB_ID", dbId)
	jobParams.put("SUITE_ID", suiteId)
	jobParams.put("TEST_ID", testId)
	jobParams.put("TEST_LENGTH", testLength)
	
	//jobParams = [AUT_NAME: aut, DB_ID: dbId, SUITE_ID: suiteId, TEST_ID: testId, TEST_LENGTH: testLength]
	println jobParams
	
	jenkinsClient.submitJob("Generate_Test", jobParams)
}

// Write out properties file
poolProps.store(new FileOutputStream("pool.properties"), "Properties used in execution of generate-pool.groovy")

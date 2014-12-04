// ARGS: AUT_NAME, DB_ID, SUITE_ID

// Jenkins client
def jenkinsClient = new JenkinsClient("jenkins", "8080", "", "admin", "amalga84go")

// TestDataManager
def manager = new TestDataManager("mongo", "27017", args[1])
 
for(String id : manager.getIdsInSuite(args[2])){
        
        // update/obtain job-specific params
        
        // build Map of params
        // I have only used text params, but perhaps others supported via Jenkins Remote API
        def jobParams = new HashMap<String, String>();
        jobParams.put("AUT_NAME", args[0])
        jobParams.put("DB_ID", args[1])
        jobParams.put("SUITE_ID", args[2])
        jobParams.put("TEST_ID", id)
        
        // Use Jenkins client to launch job
        jenkinsClient.submitJob("replay-test", jobParams)
}

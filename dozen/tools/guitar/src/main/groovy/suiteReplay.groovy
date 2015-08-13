// ARGS: AUT_NAME, DB_ID, SUITE_ID, BUNDLE_ID

import edu.umd.cs.guitar.util.JenkinsClient
import edu.umd.cs.guitar.main.TestDataManager

def master = "guitar05.cs.umd.edu"

// Jenkins client
def jenkinsClient = new JenkinsClient(master, "8888", "", "admin", "amalga84go")

// TestDataManager
def manager = new TestDataManager(master, "37017", args[1])
 
def tests = manager.getTestIdsInSuite(args[2])
println "Size is ${tests.size()}"

int count = 0
int start_buffer_count = 50
for(String id : manager.getTestIdsInSuite(args[2])){
				count++

        // build Map of params
        // I have only used text params, but perhaps others supported via Jenkins Remote API
        def jobParams = new HashMap<String, String>();
        jobParams.put("AUT_NAME", args[0])
        jobParams.put("DB_ID", args[1])
        jobParams.put("SUITE_ID", args[2])
        jobParams.put("TEST_ID", id)
        jobParams.put("BUNDLE_ID", args[3])
        
        // Use Jenkins client to launch job
        jenkinsClient.submitJob("replay-test", jobParams)

				// ZZZ to let the master recover
				if(count < start_buffer_count) {
					sleep(3000)
				} else {
					sleep(10000)
				}
}

// ARGS: DATASET, MIN_GAMMA, MAX_GAMMA, MIN_COST, MAX_COST

import edu.umd.cs.guitar.util.JenkinsClient
import edu.umd.cs.guitar.main.TestDataManager

def master = "guitar05.cs.umd.edu"
def dataset = args[0]
def minGammaExp = Integer.parseInt(args[1])
def maxGammaExp = Integer.parseInt(args[2])
def minCostExp = Integer.parseInt(args[3])
def maxCostExp = Integer.parseInt(args[4])

// Jenkins client
def jenkinsClient = new JenkinsClient(master, "8888", "", "admin", "amalga84go")

for (int gamma=minGammaExp; gamma<=maxGammaExp; i++) {
	for (int cost=minCostExp; cost<=maxCostExp; i++) {
   	// build Map of params
   	// I have only used text params, but perhaps others supported via Jenkins Remote API
   	def jobParams = new HashMap<String, String>();
   	jobParams.put("DATASET", dataset.toString())
   	jobParams.put("GAMMA_EXPONENT", gamma.toString())
   	jobParams.put("COST_EXPONENT", cost.toString())

  	// Use Jenkins client to launch job
   	jenkinsClient.submitJob("train-model", jobParams)

		// ZZZ to let the master recover
		sleep(3000)
	}
}

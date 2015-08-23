// ARGS: DATASET, MIN_GAMMA, MAX_GAMMA, MIN_COST, MAX_COST

import AwsUtils
import edu.umd.cs.guitar.util.JenkinsClient
import edu.umd.cs.guitar.main.TestDataManager
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.model.GetObjectRequest

def master = "guitar05.cs.umd.edu"
def dataset = args[0]
def minGammaExp = Integer.parseInt(args[1])
def maxGammaExp = Integer.parseInt(args[2])
def minCostExp = Integer.parseInt(args[3])
def maxCostExp = Integer.parseInt(args[4])
def accessKey = "AKIAI2AFH2XMCMMIST7A"
def secretKey = "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"

// Clients
def jenkinsClient = new JenkinsClient(master, "8888", "", "admin", "amalga84go")
def awsClient = new AwsUtils(accessKey, secretKey)

println "Removing old reports for this dataset"
awsClient.deleteOldReports("reports/${dataset}_report")

println "Train models with various parameters"
int reportCount = 0
for (int gamma=minGammaExp; gamma<=maxGammaExp; gamma++) {
	for (int cost=minCostExp; cost<=maxCostExp; cost++) {
		reportCount++
   	// build Map of params
   	// I have only used text params, but perhaps others supported via Jenkins Remote API
   	def jobParams = new HashMap<String, String>();
   	jobParams.put("DATASET", dataset.toString())
   	jobParams.put("GAMMA_EXPONENT", gamma.toString())
   	jobParams.put("COST_EXPONENT", cost.toString())
		jobParams.put("ACCESS_KEY", accessKey)
		jobParams.put("SECRET_KEY", secretKey)

  	// Use Jenkins client to launch job
   	jenkinsClient.submitJob("train-model", jobParams)

		// ZZZ to let the master recover
		sleep(1000)
	}
}

// Wait for report files to show up in S3
println "Waiting for reports"
def utils = new AwsUtils(accessKey, secretKey)
def reportKeys = utils.waitForReports("reports/${dataset}_report", reportCount)

// Find best accuracy from among reports
println "Finding the best model"
def bestAcc = 0.0
def modelKey = null
reportKeys.each {
	def text = utils.getTextRow(it)
	def chunks = text.split(",")
	def summary = [:]
	def acc = Double.parseDouble(chunks[3])
	if (acc > bestAcc) {
		bestAcc = acc
		modelKey = chunks[2]
	}
}

println "Best accuracy is ${bestAcc} by model ${modelKey}"
utils.writeBestFile(dataset, modelKey)

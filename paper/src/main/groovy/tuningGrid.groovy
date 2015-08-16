// ARGS: DATASET, MIN_GAMMA, MAX_GAMMA, MIN_COST, MAX_COST

import edu.umd.cs.guitar.util.JenkinsClient
import edu.umd.cs.guitar.main.TestDataManager
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.auth.BasicAWSCredentials

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
client.deleteOldReports("reports/${dataset}_report")

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
utils.putBestFile(dataset, modelKey)

class AwsUtils {

	def client
	static final long POLL_INTERVAL= 30000
	static final long POLL_MAX = 1200000
	static final String BUCKET = "com.btr3.research"

	def AwsUtils(accessKey, secretKey) {
		client = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey))
	}

	def getReportKeys(String prefix){
		def allObjects = client.listObjects(BUCKET, prefix).getObjectSummaries()
		allObjects.collect {
			it.getKey()	
		}
	}

	def waitForReports(String prefix, int desiredCount) {
		int waited = 0
		def actualKeys = getReportKeys(prefix)
		while ((actualKeys.size() < desiredCount) && (waited < POLL_MAX)) {
			if (actualKeys.size() == desiredCount) {
				break;
			} else if (actualKeys.size() > desiredCount) {
				throw new RuntimeException("Asked for ${desiredCount} reports, but already found ${actualKeys.size()}")
			} else {
				println "Waiting for ${desiredCount} reports, found ${actualKeys.size()}"
				Thread.sleep(POLL_INTERVAL)
				waited += POLL_INTERVAL
				actualKeys = getReportKeys(prefix)	
			}
		}
		if (waited >= POLL_MAX) {
			throw new RuntimeException("Did not find ${desiredCount} reports after waiting max of ${POLL_MAX}ms")
		} else {
			actualKeys
		}
	}

	def getTextRow(String key) {
		client.getObject(BUCKET, key).getObjectContent().getText()
	}

	def deleteOldReports(String prefix) {
		def keys = getReportKeys(prefix)
		keys.each {
			client.deleteObject(BUCKET, it)
		}
	}
	
	def putBestFile(String dataset, String modelKey) {
		def bestFile = new File("${dataset}_best")
		bestFile.withWriter('UTF-8') { writer ->
			writer.write("models/${modelKey}")
		}

		client.putObject(BUCKET, "best/${dataset}_best", bestFile)
	}
}

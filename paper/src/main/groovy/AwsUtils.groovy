// ARGS: DATASET, MIN_GAMMA, MAX_GAMMA, MIN_COST, MAX_COST

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.model.GetObjectRequest

class AwsUtils {

	def client
	static final long POLL_INTERVAL= 120000
	static final long POLL_MAX = 2400000
	static final String BUCKET = "com.btr3.research"
	static final String MAX_RETRIES = 5

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
	
	def writeBestFile(String dataset, String modelKey) {
		def bestFile = new File("${dataset}_best")
		bestFile.withWriter('UTF-8') { writer ->
			writer.write("${modelKey}")
		}

	}

//	def getObjectWithRetries(String key, String localPath) {
//		int retries = 0
//		while (retries <= MAX_RETRIES) {
//			def result = getObjectWrapper(key, localPath)	
//			if (result == null) { 
//				retries++
//			} else {
//				return result
//			}
//		}
//}
	
//	def getObjectWrapper(String key, String localPath) {
//		def gor = new GetObjectRequest(BUCKET, key)
//		try {
//			return client.getObject(gor, new File(localPath))
//		} catch (AmazonClientException ace | AmazonServiceException ase) {
//			return null
//		}
//	}	

}

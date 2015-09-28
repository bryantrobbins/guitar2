// ARGS: DATASET, MIN_GAMMA, MAX_GAMMA, MIN_COST, MAX_COST

import AwsUtils
def accessKey = "AKIAI2AFH2XMCMMIST7A"
def secretKey = "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"

def prefix = "reports/amalga_ArgoUML_sq_l_1_testCaseFeatures_n_4_data_report"

// Clients
def awsClient = new AwsUtils(accessKey, secretKey)
awsClient.getReportKeys(prefix).sort(false).each {
	println it
}

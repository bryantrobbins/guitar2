// ARGS: DATASET, MIN_GAMMA, MAX_GAMMA, MIN_COST, MAX_COST

import AwsUtils
def key = args[0]
def localPath = args[1]
def accessKey = "AKIAI2AFH2XMCMMIST7A"
def secretKey = "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"

// Clients
def awsClient = new AwsUtils(accessKey, secretKey)
awsClient.getObjectWithRetries(key, localFile)

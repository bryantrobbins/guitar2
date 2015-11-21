# I want commands printed in output
options(echo=TRUE, warning.length=8170)

# Load libs
library("e1071")
library("rmongodb")
library("RS3")

# Connect to mongo
m <- mongo.create(host = "guitar05.cs.umd.edu:37017")

# Verify connectivity
mongo.is.connected(m)

# LOAD THESE VALUES FROM COMMAND LINE
args <- commandArgs(trailingOnly = TRUE)
groupDb <- args[1]
groupId <- args[2]
trainingDb <- args[3]
trainingId <- args[4]
testDb <- args[5]
testId <- args[6]
accessKey <- args[7]
secretKey <- args[8]

cat("Group Object", "\n")
cat(groupDb, "\n")
cat(groupId, "\n")

cat("Training Object", "\n")
cat(trainingDb, "\n")
cat(trainingId, "\n")

cat("Test Object", "\n")
cat(testDb, "\n")
cat(testId, "\n")

####################################################
# IF YOU EDIT SOMETHING BELOW THIS LINE YOU BETTER #
# HAVE A REALLY GOOD REASON                        #
####################################################

# Collections
trainingCollection <- sprintf('%s.results', trainingDb)
testCollection <- sprintf('%s.results', testDb)
groupsCollection <- sprintf('%s.groups', groupDb)

# Get global features
cat('Loading group object\n')
group.query <- sprintf('{"groupId": "%s"}', groupId)
bson <- mongo.bson.from.JSON(group.query)
value <- mongo.findOne(m, groupsCollection, bson)
list <- mongo.bson.to.list(value)
featureKey <- sprintf('testCaseFeatures_n_%s', list[['maxN']])
input.suite <- list[['suiteId']]
global.features <- list[['featuresList']]
input.suite
length(global.features)

# Get training data
train.query <- sprintf('{"resultId": "%s"}', trainingId)
cat('Loading training data', '\n')
bson <- mongo.bson.from.JSON(train.query)
value <- mongo.findOne(m, trainingCollection, bson)
rlist <- mongo.bson.to.list(value)
train.passing <- rlist[['results']][['passingResults']]
train.failing <- rlist[['results']][['failingResults']]
train.all <- c(train.passing, train.failing)

# Get test data
test.query <- sprintf('{"resultId": "%s"}', testId)
cat('Loading test data', '\n')
bson <- mongo.bson.from.JSON(test.query)
value <- mongo.findOne(m, testCollection, bson)
rlist <- mongo.bson.to.list(value)
test.passing <- rlist[['results']][['passingResults']]
test.failing <- rlist[['results']][['failingResults']]
test.all <- c(test.passing, test.failing)

global.all <- c(train.all, test.all)
length(global.all)

# Build data frame for all examples
cat('Initializing global data frame\n')
cna <- c(list('isInfeas', 'isTraining'),global.features)
global.df <- data.frame(matrix("0", length(global.all), length(cna)))
rownames(global.df) <- global.all
colnames(global.df) <- cna

for (tid in global.all){
	if(tid %in% train.failing){
		global.df[tid, 'isInfeas'] <- "1"
	}

	if(tid %in% test.failing){
		global.df[tid, 'isInfeas'] <- "1"
	}

  # Set 'isTraining' value for splitting later
  # init artifactsCollection for loading of features
	if(tid %in% train.all){
		global.df[tid, 'isTraining'] <- "1"
    artifactsCollection <- sprintf('%s.artifacts', trainingDb)
	} else {
    artifactsCollection <- sprintf('%s.artifacts', testDb)
  }
  
  # Load features
  features.query <- sprintf('{"artifactType": "%s", "ownerId": "%s"}', featureKey, tid)
	cat('Loading', tid, '\n')
  queryBson <- mongo.bson.from.JSON(features.query)
  resultBson<- mongo.findOne(m, artifactsCollection, queryBson)
	resultList <- mongo.bson.to.list(resultBson)
	for (feat in resultList[['artifactData']][['features']]){
    if(feat %in% global.features){
	    global.df[tid, feat] <- "1"
    }
	}
}

output.file <- sprintf('data/%s_%s_data.csv', input.suite, featureKey)
cat('Writing out data frame\n')
write.csv(global.df, file = output.file)

# Upload to S3 location
bucket <- 'com.btr3.research'
S3_connect(accessKey, secretKey)
S3_put_object(bucket, output.file, output.file, "text/csv")

# LibSVM
library("e1071", lib.loc="/opt/Rpackages/")

# MongoDB
library("rmongodb", lib.loc="/opt/Rpackages/")

# Connect to mongo
m <- mongo.create(host = "guitar05.cs.umd.edu:37017")

# Verify connectivity
mongo.is.connected(m)

# LOAD THESE VALUES FROM COMMAND LINE
args <- commandArgs(trailingOnly = TRUE)
dbId <- args[1]
input.suite <- args[2]
groupId <- args[3]
featureKey <- args[4]

####################################################
# IF YOU EDIT SOMETHING BELOW THIS LINE YOU BETTER #
# HAVE A REALLY GOOD REASON                        #
####################################################

# Collections
resultsCollection <- sprintf('%s.results', dbId)
artifactsCollection <- sprintf('%s.artifacts', dbId)
groupsCollection <- sprintf('%s.groups', dbId)

# Query jsons
input.query <- sprintf('{"suiteId": "%s"}', input.suite)
combined.query <- sprintf('{"suiteId": "%s_combined"}', input.suite)
group.query <- sprintf('{"groupId": "%s"}', groupId)

# Build lists of test ids in various categories
cat('Loading example ids\n')
cat('Loading input suite\n')
bson <- mongo.bson.from.JSON(input.query)
value <- mongo.findOne(m, resultsCollection, bson)
list <- mongo.bson.to.list(value)
input.passing <- list[['results']][['passingResults']]
input.failing <- list[['results']][['failingResults']]
input.all <- c(input.passing, input.failing)

cat('Loading combined suite\n')
bson <- mongo.bson.from.JSON(combined.query)
value <- mongo.findOne(m, resultsCollection, bson)
list <- mongo.bson.to.list(value)
combined.passing <- list[['results']][['passingResults']]
combined.failing <- list[['results']][['failingResults']]
combined.all <- c(combined.passing, combined.failing)

global.all <- c(combined.all, input.all)
length(global.all)

# Get global features
cat('Loading global feature list\n')
bson <- mongo.bson.from.JSON(group.query)
value <- mongo.findOne(m, groupsCollection, bson)
list <- mongo.bson.to.list(value)
global.features <- list[['featuresList']]
length(global.features)

# Build data frame for all examples
cat('Initializing global data frame\n')
cna <- c(list('isFeas', 'isInput'),global.features)
global.df <- data.frame(matrix(0, length(global.all), length(cna)))
rownames(global.df) <- global.all
colnames(global.df) <- cna

for (tid in global.all){
	# Set isInput and isFeas
	if(tid %in% input.all){
		global.df[tid, 'isInput'] <- 1
		if(tid %in% input.passing){
			global.df[tid, 'isFeas'] <- 1
		}
	}else {
		if(tid %in% combined.passing){
			global.df[tid, 'isFeas'] <- 1
		}
	}

  features.query <- sprintf('{"artifactType": "%s", "ownerId": "%s"}', featureKey, tid)
	cat('Loading ')
	cat(tid)
	cat('\n')
  queryBson <- mongo.bson.from.JSON(features.query)
  resultBson<- mongo.findOne(m, artifactsCollection, queryBson)
	resultList <- mongo.bson.to.list(resultBson)
	for (feat in resultList[['artifactData']][['features']]){
		global.df[tid, feat] <- 1
	}
}

output.file <- sprintf('data/%s_data.csv', input.suite)
cat('Writing out data frame\n')
write.csv(global.df, file = output.file)

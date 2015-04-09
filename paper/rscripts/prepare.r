# LibSVM
library("e1071", lib.loc="/opt/Rpackages/")

# MongoDB
library("rmongodb", lib.loc="/opt/Rpackages/")

# Connect to mongo
m <- mongo.create(host = "guitar05.cs.umd.edu:37017")

# Verify connectivity
mongo.is.connected(m)

# Some collections
resultsCollection <- 'amalga_jenkins-generate-sl1-14.results'
artifactsCollection <- 'amalga_jenkins-generate-sl1-14.artifacts'
groupsCollection <- 'amalga_jenkins-generate-sl1-14.groups'

# Some JSON objects
input.suite <- 'amalga_JabRef_sq_l_1'
groupId <- '551894310364984789d6237f'
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

# Get global features
cat('Loading global feature list\n')
bson <- mongo.bson.from.JSON(group.query)
value <- mongo.findOne(m, groupsCollection, bson)
list <- mongo.bson.to.list(value)
global.features <- list[['featuresList']]

# Build data frame for all examples
cat('Initializing global data frame\n')
cna <- c(list('isFeas', 'isInput'),global.features)
global.df <- data.frame(mat.or.vec(length(global.all), length(cna)))
rownames(global.df) <- global.all
colnames(global.df) <- cna

for (tid in global.all){
	# Set isInput and isFeasible
	if(tid %in% input.all){
		global.df[tid, 'isInput'] <- 1
		if(tid %in% input.passing){
			global.df[tid, 'isFeasible'] <- 1
		}
	}else {
		if(tid %in% combined.passing){
			global.df[tid, 'isFeasible'] <- 1
		}
	}

  features.query <- sprintf('{"artifactType": "testCaseFeatures", "ownerId": "%s"}', tid)
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

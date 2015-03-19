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
dfCollection <- 'amalga_jenkins-generate-sl1-14.frames'

# Some JSON objects
input.suite <- 'amalga_JabRef_sq_l_1'
input.query <- sprintf('{"suiteId": "%s"}', input.suite)
combined.suite <- 'amalga_JabRef_sq_l_1_combined'
combined.query <- sprintf('{"suiteId": "%s_combined"}', combined.suite)

# Get example ids
bson <- mongo.bson.from.JSON(input.query)
value <- mongo.findOne(m, resultsCollection, bson)
list <- mongo.bson.to.list(value)
input.passing <- list[['results']][['passingResults']]
input.failing <- list[['results']][['failingResults']]
input.all <- c(input.passing, input.failing)

bson <- mongo.bson.from.JSON(combined.query)
value <- mongo.findOne(m, resultsCollection, bson)
list <- mongo.bson.to.list(value)
combined.passing <- list[['results']][['passingResults']]
combined.failing <- list[['results']][['failingResults']]
combined.all <- c(combined.passing, combined.failing)

global.all <- c(combined.all, input.all)

# Build global feature list
global.features <- list()
for (tid in global.all){
  myFeaturesQuery <- sprintf('{"ownerId": "%s", "artifactType": "testCaseFeatures"}', tid)
	myFeatsBson <- mongo.bson.from.JSON(myFeaturesQuery)
	myFeatsValue <- mongo.findOne(m, artifactsCollection, myFeatsBson)
	myFeatsList <- mongo.bson.to.list(myFeatsValue)[['artifactData']][['features']]
	global.features <- unique(c(global.features, myFeatsList))
}

# Build data frame for all examples
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

	# Set feature values
	myFeaturesQuery <- sprintf('{"ownerId": "%s", "artifactType": "testCaseFeatures"}', tid)
  myFeatsBson <- mongo.bson.from.JSON(myFeaturesQuery)
  myFeatsValue <- mongo.findOne(m, artifactsCollection, myFeatsBson)
  myFeatsList <- mongo.bson.to.list(myFeatsValue)[['artifactData']][['features']]
	
	for(feat in myFeatsList){
		global.df[tid, feat] <- 1	
	}
}

# Convert df to bson for storage
list <- list(a=2, b=3, c=list(d=4, e=5))
bson <- mongo.bson.from.df(global.df)

# Add some metadata
dfList <- mongo.bson.to.list(bson)
sprintf('{"inputSuiteId": "%s", "combinedSuiteId": "%s", "frame": "%s"}', input.suite, combined.suite, dfJson)
mdBson <- mongo.bson.from.list(mdList)
mongo.insert(m, dfCollection, mdBson)

# Add data frame of examples
mongo.update(m, dfCollection, query)
global.df

# LibSVM
library("e1071", lib.loc="/opt/Rpackages/")

# MongoDB
library("rmongodb", lib.loc="/opt/Rpackages/")

# Connect to data source
mongo <- mongo.create(host = "guitar03.cs.umd.edu:37017")

# The collection to use
coll <- 'amalga_jenkins-generate-sl1-14.bundle_27'

# Query
mongo.find.one(mongo, coll, '{"city":"COLORADO CITY"}')

#input <- data.frame(read.csv("data/xor.csv"))
#y <- input[,2]
#x <- input
#x$id <- NULL
#x$y <- NULL
#model <- svm(x,y,type="C-classification",kernel="radial")
#print(model)
#predict(model,x)

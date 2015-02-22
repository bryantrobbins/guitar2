# LibSVM
library("e1071", lib.loc="/opt/Rpackages/")

# MongoDB
library("rmongodb", lib.loc="/opt/Rpackages/")

# Java
library("rJava", lib.loc="/opt/Rpackages/")

# Initialize Java stuff
.jinit()
obj=.jnew("TestDataManager")

# Connect to data source
mongo <- mongo.create(host = "guitar03.cs.umd.edu:37017")

# Collections to use
suiteColl <- 'amalga_jenkins-generate-sl1-14.suite_amalga_JabRef_sq_l_1'
bundleColl <- 'amalga_jenkins-generate-sl1-14.bundle_27'

# Loop over tests in suite
tests <- mongo.distinct(mongo, suiteColl, "testId")
for (test in tests ) {
	foo.squared[i] = foo[i]^2
}

	# Get execution ID
	# Get log artifact
	# Scan for infeasible

#input <- data.frame(read.csv("data/xor.csv"))
#y <- input[,2]
#x <- input
#x$id <- NULL
#x$y <- NULL
#model <- svm(x,y,type="C-classification",kernel="radial")
#print(model)
#predict(model,x)

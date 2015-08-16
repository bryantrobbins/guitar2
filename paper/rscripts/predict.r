# I want commands printed in output
options(echo=TRUE, warning.length=8170)

# svm and s3 libraries
library("e1071")
library("RS3")

# Grab arguments
args <- commandArgs(trailingOnly = TRUE)
dataset <- args[1]
modelFile <- args[2]
accessKey <- args[3]
secretKey <- args[4]

# Get data from S3
data.key <- sprintf('data/%s.csv', dataset)
bucket <- 'com.btr3.research'
S3_connect(accessKey, secretKey)
S3_get_object(bucket, data.key, data.key)
data <- data.frame(read.csv(data.key))

# Load model from model file
load(modelFile)

# Prepare test data
testing <- subset(data, isInput==0)
x <- subset(testing, select = -isFeas)
x <- data.frame(lapply(x,as.numeric))
x <- as.matrix(x)
y <- factor(testing[['isFeas']])

# Predict
pred <- predict(model, x)

# Evaluate
table(pred, y)

# Re-print any warnings
warnings()

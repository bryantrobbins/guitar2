# I want commands printed in output
options(echo=TRUE, warning.length=8170)

# LibSVM
library("glmnet")
library("data.table")
library("ROCR")
library("RS3")

# Grab arguments
args <- commandArgs(trailingOnly = TRUE)
dataset <- args[1]
accessKey <- args[2]
secretKey <- args[3]

# Get data CSV file from S3
data.key <- sprintf('data/%s.csv', dataset)
bucket <- 'com.btr3.research'
S3_connect(accessKey, secretKey)
S3_get_object(bucket, data.key, data.key)

# Load massive data file from csv
data=fread(input.file, stringsAsFactors=TRUE)

# Cut into x and y
x=model.matrix(isInfeas~.,data=data)
y=data$isInfeas

# Run the lasso
cvfit=cv.glmnet(x,y)
coef(cv.lasso)
plot(cvfit)

# Re-print any warnings
warnings()

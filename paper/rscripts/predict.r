# I want commands printed in output
options(echo=TRUE, warning.length=8170)

# LibSVM
library("e1071")
library("RS3")

# Grab arguments
args <- commandArgs(trailingOnly = TRUE)
dataset <- args[1]

# Get data CSV file from S3
data.key <- sprintf('data/%s.csv', dataset)
bucket <- 'com.btr3.research'
S3_connect(accessKey, secretKey)
S3_get_object(bucket, data.key, data.key)

data <- data.frame(read.csv(data.key))

training <- subset(data, isInput==1)
x <- subset(training, select = -isFeas)
x <- data.frame(lapply(x,as.numeric))
x <- as.matrix(x)
y <- factor(training[['isFeas']])
model <- svm(x, y, type = "C-classification", kernel = "radial", cost=2^myCostExp, gamma=2^myGammaExp, probability=TRUE, scale=FALSE, cross = 3)
modelFile <- sprintf("%s_model_%s_%s.rda", dataset, myCostExp, myGammaExp)
reportFile <- sprintf("%s_report_%s_%s.txt", dataset, myCostExp, myGammaExp)

# Write out report
report <- sprintf("%s,%s,%s,%s", myCostExp, myGammaExp, modelFile, model$tot.accuracy)
fileConn<-file(reportFile)
writeLines(c(report), fileConn)
close(fileConn)

# Write out model
save(model, file = modelFile)

# Upload files to S3
reportKey <- sprintf("reports/%s", reportFile)
modelKey <- sprintf("models/%s", modelFile)
S3_put_object(bucket,reportKey,reportFile,"text/csv")
S3_put_object(bucket,modelKey,modelFile,"")

# Re-print any warnings
warnings()

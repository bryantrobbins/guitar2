# I want commands printed in output
options(echo=TRUE, warning.length=8170)

# Needed for portable installs
cran <- "http://cran.rstudio.com/"

# LibSVM
install.packages("e1071", repos=cran)
library("e1071")

# ROCR
install.packages("ROCR", repos=cran)
library("ROCR")

# S3
install.packages("devtools", repos=cran)
require(devtools)
install_github("RS3", "Gastrograph")
library("RS3")

# Grab arguments
args <- commandArgs(trailingOnly = TRUE)
dataset <- args[1]
myGammaExp <- as.numeric(args[2])
myCostExp <- as.numeric(args[3])
accessKey <- args[4]
secretKey <- args[5]

# Get data CSV file from S3
cat('Fetching data file from S3\n')
data.key <- sprintf('data/%s.csv', dataset)
bucket <- 'com.btr3.research'
S3_connect(accessKey, secretKey)
S3_get_object(bucket, data.key, data.key)

cat('Reading data frame from csv\n')
data <- data.frame(read.csv(data.key))

# Prepare training data
training <- subset(data, isInput==1)
training$isInput <- NULL
training_x <- subset(training, select = -isFeas)

# Construct model from training data
fit <- svm(isFeas~., data = training, type = "C-classification", kernel = "radial", cost=2^myCostExp, gamma=2^myGammaExp, probability=TRUE, scale=FALSE, cross = 5)

# Describe the fit
print(fit)
summary(fit)

# Predict vs training data
actual <- t(training['isFeas'])
pred <- predict(fit, training, probability = TRUE)

table(pred, actual)

# Re-print any warnings
warnings()

# Save classifier perf to file
#performanceFileName <- sprintf("tune_%s_%s_%s.result", dataset, myGammaExp, myCostExp)
#headers <- sprintf("dataset,gamma,cost,fnc,fpc")
#results <- sprintf("%s,%s,%s,%s,%s", dataset, myGammaExp,myCostExp,pred)
#fileConn<-file(filename)
#writeLines(c(headers, results), fileConn)
#close(fileConn)

# Upload files to S3

# I want commands printed in output
options(echo=TRUE, warning.length=8170)

# Library loading
library('data.table')
library('glmnet')
library('methods')
library('mda')
library("RS3")

# Grab arguments
args <- commandArgs(trailingOnly = TRUE)
model <- args[1]
accessKey <- args[2]
secretKey <- args[3]

# Get data from S3
data.key <- sprintf('data/%s.dat', model)
bucket <- 'com.btr3.research'
S3_connect(accessKey, secretKey)
S3_get_object(bucket, data.key, data.key)

# Load model from exported file
data <- readRDS(data.key)

# Run the lasso
cvfit = cv.glmnet(data$trainMatrix, data$trainY, family = "binomial", type.measure = "class")

# Plot the fit
plot(cvfit)

# Run predictions, printing confusion matrix of t1 and t2 errors
confusion(predict(cvfit, newx = data$testMatrix, type = "class", s = c(cvfit$lambda.min)), data$testY)

# Inspect fit
lix <- which(cvfit$lambda == cvfit$lambda.min)
cfs <- coef(cvfit, s = "lambda.min")
sorted <- sort(cfs[which(cfs != 0)], decreasing=TRUE, index.return=TRUE)
cat('Lambda is', cvfit$lambda.min, 'index=', lix, '\n')
cat('Model has', cvfit$nzero[lix], 'non-zero coefficients:', rownames(cfs)[sorted$ix], '\n')

# Re-print any warnings
warnings()

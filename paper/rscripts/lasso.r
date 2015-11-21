# I want commands printed in output
options(echo=TRUE, warning.length=8170)

# Library loading
library('data.table')
library('glmnet')
library('methods')
library('mda')

# Grab arguments
args <- commandArgs(trailingOnly = TRUE)
dataset <- args[1]
accessKey <- args[2]
secretKey <- args[3]

# Get data from S3
data.key <- sprintf('data/%s.csv', dataset)
bucket <- 'com.btr3.research'
S3_connect(accessKey, secretKey)
S3_get_object(bucket, data.key, data.key)
data <- data.frame(read.csv(data.key))

# Load massive data file from csv
data=fread(data.key, stringsAsFactors=TRUE)
train.data=data[isTraining=="1"]
test.data=data[isTraining=="0"]

cat('Before filtering:', length(names(train.data)), ' features in training set', '\n')
# Drop event-related features
#drop = grep("e[1-9]+", names(train.data), value=TRUE)
#train.data=train.data[,(drop):=NULL]
#length(names(train.data))

#drop = grep("before", names(train.data), value=TRUE)
#train.data=train.data[,(drop):=NULL]
#length(names(train.data))

# Drop missing factors
filter = c('V1')
for (ix in names(train.data)) {
  str <- sprintf("train.data$\"%s\"", ix)
  if(nlevels(eval(parse(text=str))) < 2) {
    filter <- c(filter, ix)
  }
}

train.data=train.data[,(filter):=NULL]
cat('After filtering:', length(names(train.data)), ' features in training set', '\n')

# Filter features of test data given training features
cat('Before filtering:', length(names(test.data)), ' features in test set', '\n')
filter=c()
for (ix in names(test.data)) {
  if(! (ix %in% names(train.data))) {
    filter <- c(filter, ix)
  }
}
test.data=test.data[,(filter):=NULL]
cat('After filtering:', length(names(test.data)), ' features in test set', '\n')

# Prepare training data
xm=model.matrix(isInfeas~., data=train.data)
x=apply(xm, 2, as.numeric)
y=as.numeric(train.data$isInfeas)

# Prepare test data
xtm=model.matrix(isInfeas~., data=test.data)
xt=apply(xtm, 2, as.numeric)
actual=as.numeric(test.data$isInfeas)

# Run the lasso
cvfit = cv.glmnet(x, y, family = "binomial", type.measure = "class")

# Run predictions, printing confusion matrix of t1 and t2 errors
confusion(predict(cvfit, newx = xt, type = "class", s = c(cvfit$lambda.min)), actual)

# Re-print any warnings
warnings()

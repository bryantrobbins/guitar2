# I want commands printed in output
options(echo=TRUE, warning.length=8170)

# Library loading
library('data.table')
library('glmnet')
library('methods')

# Input files
input.file="amalga_JabRef_efg-random_testCaseFeatures_n_2_data.csv"
test.file="amalga_JabRef_efg-random_testCaseFeatures_n_2_data-TEST.csv"

# Load massive data file from csv
data=fread(input.file, stringsAsFactors=TRUE)
test.data=fread(test.file, stringsAsFactors=TRUE)

cat('Before filtering:', length(names(data)), ' features in training set', '\n')
# Drop event-related features
#drop = grep("e[1-9]+", names(data), value=TRUE)
#data=data[,(drop):=NULL]
#length(names(data))

#drop = grep("before", names(data), value=TRUE)
#data=data[,(drop):=NULL]
#length(names(data))

# Drop missing factors
filter = c('V1')
for (ix in names(data)) {
  str <- sprintf("data$\"%s\"", ix)
  if(nlevels(eval(parse(text=str))) < 2) {
    filter <- c(filter, ix)
  }
}
data=data[,(filter):=NULL]
cat('After filtering:', length(names(data)), ' features in training set', '\n')

# Filter features of test data given training features
cat('Before filtering:', length(names(test.data)), ' features in test set', '\n')
filter=c()
for (ix in names(test.data)) {
  if(! (ix %in% names(data))) {
    filter <- c(filter, ix)
  }
}
test.data=test.data[,(filter):=NULL]
cat('After filtering:', length(names(test.data)), ' features in test set', '\n')

# Add missing cols to test data
missing=setdiff(names(data), names(test.data))
test.data=test.data[,(missing):="0"]

# Prepare training data
xm=model.matrix(isInfeas~., data=data)
x=apply(xm, 2, as.numeric)
y=as.numeric(data$isInfeas)

# Prepare test data
xm=model.matrix(isInfeas~., data=data)
x=apply(xm, 2, as.numeric)
y=as.numeric(data$isInfeas)

# Run the lasso
cvfit = cv.glmnet(x, y, family = "binomial", type.measure = "class")
plot(cvfit)

# Prepare test set
xmt=model.matrix(isInfeas~., data=test.data)
xt=apply(xmt, 2, as.numeric)
actual=as.numeric(test.data$isInfeas)

# Run predictions
expected=predict(cvfit, newx = xt, type = "class", s = c(cvfit$lambda.min))
expected
actual

# Re-print any warnings
warnings()

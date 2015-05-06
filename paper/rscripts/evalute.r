# LibSVM
library("e1071", lib.loc="/opt/Rpackages/")
library("gplots", lib.loc="/opt/Rpackages/")
library("ROCR", lib.loc="/opt/Rpackages/")

data.suite <- 'amalga_JabRef_sq_l_1'
data.file <- sprintf('data/%s_data.csv', data.suite)

cat('Reading from csv\n')
data <- data.frame(read.csv(data.file))

# Use input suite as training data, combined suite as test
train <- subset(data, isInput == 1)
test <- subset(data, isInput == 0)

# Build training data structures
y <- train$isFeas
train$isFeas <- NULL
train$isInput <- NULL
train$id <- NULL

# Build test data structures
actual <- test$isFeas
test$isFeas <- NULL
test$isInput <- NULL
test$id <- NULL

# Run predictions
bestg = 0.0078125
bestc = 2
model <- svm(train,y,type="C-classification",kernel="radial", probability = TRUE, gamma=bestg, cost=bestc, cross=10)
pred <- predict(model, test, probability = TRUE)
rocr <- prediction(attr(pred, "probabilities")[,2], actual)
perf <- performance(rocr, "tpr", "fpr")

# Table
table(pred, actual)

# Plot
png(filename="roc_curve.png", width=700, height=700)
plot(perf, col=2, main="ROC Curve for SVM")

# Compute AUC
performance(rocr, "auc")

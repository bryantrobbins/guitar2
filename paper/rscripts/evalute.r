# I want commands printed in output
options(echo=TRUE)

# Grab arguments
args <- commandArgs(trailingOnly = TRUE)
aut <- args[1]
bestGamma <- args[2]
bestCost <- args[3]

# LibSVM
library("e1071", lib.loc="/opt/Rpackages/")

data.suite <- sprintf('amalga_%s_sq_l_1', aut)
data.file <- sprintf('data/%s_data.csv', data.suite)

cat('Reading from csv\n')
data <- data.frame(read.csv(data.file))

# Use input suite as training data, combined suite as test
tr <- subset(data, isInput == 1)
tr$isInput <- NULL
tr$id <- NULL

# Run predictions
model <- svm(isFeas~.,data=tr,type="C-classification",kernel="radial", probability = TRUE, gamma=bestg, cost=bestc, cross=5)
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

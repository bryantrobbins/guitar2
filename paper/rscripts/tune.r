# LibSVM
library("e1071", lib.loc="/opt/Rpackages/")

data.suite <- 'amalga_JabRef_sq_l_1'
data.file <- sprintf('data/%s_data.csv', data.suite)

cat('Reading from csv\n')
data <- data.frame(read.csv(data.file))

# Use input suite as training data, combined suite as test
tr <- subset(data, isInput == 1)
tr$isInput <- NULL
tr$id <- NULL
#y <- tr$isFeas

# Tune SVM on training data
cat('Tuning model parameters')
obj <- tune.svm(isFeas~.,
    data = tr,
	gamma = 2^(-8:3),
    cost = 2^(-8:3),
	tunecontrol = tune.control(sampling = "cross")
)

# Print tuning results
summary(obj)
plot(obj)
obj.performance

#te <- subset(data, isInput == 0)
#te$isInput <- NULL
#te$id <- NULL

# Run predictions
#bestg = 0.0078125
#bestc = 2
#model <- svm(train,y,type="C-classification",kernel="radial", probability = TRUE, gamma=bestg, cost=bestc, cross=10)
#pred <- predict(model, test, probability = TRUE)
#rocr <- prediction(attr(pred, "probabilities")[,2], actual)
#perf <- performance(rocr, "tpr", "fpr")

# Table
#table(pred, actual)

# Plot
#png(filename="roc_curve.png", width=700, height=700)
#plot(perf, col=2, main="ROC Curve for SVM")

# Compute AUC
#performance(rocr, "auc")

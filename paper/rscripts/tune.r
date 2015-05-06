# LibSVM
library("e1071", lib.loc="/opt/Rpackages/")

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

# Tune SVM on training data
obj <- tune(svm,
	train,
	y,
	ranges = list(gamma = 2^(-10:10), cost = 2^(-5:5)),
	tunecontrol = tune.control(sampling = "cross")
)

# Print tuning results
summary(obj)
plot(obj)
obj.performance

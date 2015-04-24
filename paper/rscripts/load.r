# LibSVM
library("e1071", lib.loc="/opt/Rpackages/")

input.suite <- 'amalga_JabRef_sq_l_1'
input.file <- sprintf('data/%s_data.csv', input.suite)

cat('Reading from csv\n')
input <- data.frame(read.csv(input.file))

# Build training data
train <- subset(input, isInput = '1')
train$isFeas <- NULL
train$isInput <- NULL
train
y <- train$isFeas

#test <- subset(input, isInput = '0')
#test$isFeas <- NULL
#test$isInput <- NULL
#test

# Start some SVM analysis
#model <- svm(train,y,type="C-classification",kernel="radial")
#print(model)
#predict(model,test)

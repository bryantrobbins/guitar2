# I want commands printed in output
options(echo=TRUE)

# Grab arguments
args <- commandArgs(trailingOnly = TRUE)
aut <- args[1]

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
#y <- tr$isFeas

# Tune SVM on training data
cat('Tuning model parameters')
obj <- tune.svm(isFeas~.,
	data = tr,
	gamma = 2^(-1:1),
	cost = 2^(-1:1),
	tunecontrol = tune.control(sampling = "cross", cross = 5)
)

# Print tuning results
summary(obj)
plot(obj)

# Print any warnings
warnings()

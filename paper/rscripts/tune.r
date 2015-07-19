# I want commands printed in output
options(echo=TRUE, warning.length=8170)

# Grab arguments
args <- commandArgs(trailingOnly = TRUE)
suiteId <- args[1]
minGammaExp <- args[2]
maxGammaExp <- args[3]
minCostExp <- args[4]
maxCostExp <- args[5]

# LibSVM
install.packages("e1071")
library("e1071")

data.file <- sprintf('data/%s_data.csv', suiteId)

cat('Reading from csv\n')
data <- data.frame(read.csv(data.file))

# Use input suite as training data, combined suite as test
tr <- subset(data, isInput == 1)
tr$isInput <- NULL
tr$id <- NULL

# Tune SVM on training data
cat('Tuning model parameters')
obj <- tune.svm(isFeas~.,
	data = tr,
	gamma = 2^(minGammaExp:maxGammaExp),
	cost = 2^(minCostExp:maxCostExp),
	tunecontrol = tune.control(sampling = "cross", cross = 5)
)

# Print tuning results
summary(obj)
plot(obj)

# Print any warnings
warnings()

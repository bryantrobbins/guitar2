# I want commands printed in output
options(echo=TRUE, warning.length=8170)

# Needed for portable installs
cran <- "http://cran.rstudio.com/"

# LibSVM
install.packages("e1071")
library("e1071")

# S3
install.packages("devtools", repos=cran)
require(devtools)
install_github("RS3", "Gastrograph")
library("RS3")

# Grab arguments
args <- commandArgs(trailingOnly = TRUE)
suiteId <- args[1]
myGammaExp <- args[2]
myCostExp <- args[3]

data.file <- sprintf('data/%s_data.csv', suiteId)
cat('Reading from csv\n')
data <- data.frame(read.csv(data.file))

# Build training data input set
tr <- subset(data, isInput == 1)
tr$isInput <- NULL
tr$id <- NULL

fit <- svm(isFeas~., data = tr, kernel = "rbf", cost=2^myCost, gamma=2^myGamma, tunecontrol = tune.control(sampling = "cross", cross = 5)

# Print tuning results
summary(fit)
plot(fit)

# Print any warnings
warnings()

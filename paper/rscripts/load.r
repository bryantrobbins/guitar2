# LibSVM
library("e1071", lib.loc="/opt/Rpackages/")

input.suite <- 'amalga_JabRef_sq_l_1'
input.file <- sprintf('data/%s_data.csv', input.suite)

cat('Reading from csv\n')
input <- data.frame(read.csv(input.file))



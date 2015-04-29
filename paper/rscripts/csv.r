library("e1071", lib.loc="/opt/Rpackages/")
input <- data.frame(read.csv("data/xor.csv"))
y <- input[,2]
x <- input
x$id <- NULL
x$y <- NULL
model <- svm(x,y,type="C-classification",kernel="radial")
print(model)
predict(model,x)

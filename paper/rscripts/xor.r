library("e1071", lib.loc="/opt/Rpackages/")
x <- array(data = c(0,0,1,1,0,1,0,1),dim=c(4,2))
y <- factor(c(1,-1,-1,1))
model <- svm(x,y,type="C-classification",kernel="radial")
pred <- predict(model,x)
pred
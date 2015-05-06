library("e1071", lib.loc="/opt/Rpackages/")
x <- array(data = c(0,0,1,1,0,1,0,1),dim=c(4,2))
y <- factor(c(1,-1,-1,1))
cat('This is x:\n')
x
cat('This is y:\n')
y
model <- svm(x,y,type="C-classification",kernel="radial")
pred <- predict(model,x)
pred
plot(x)
points(x, pred, col = 4)
#points(x[model$index, ], col = 2)


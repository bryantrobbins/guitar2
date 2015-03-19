p <- list('this', 'is', 'a', 'list')
p <- c(list('y', 'input'), p)
d <- data.frame(mat.or.vec(4, length(p)))
class(d)
colnames(d) <- p
rownames(d) <- list('t_whatever', 't_something', 't_ehhhhh', 't_nabrah')
d['t_whatever', 'input'] <- 1
d['t_nabrah', 'input'] <- 1
d

# Play with mongodb
library("rmongodb", lib.loc="/opt/Rpackages/")
bs <- mongo.bson.from.df(d)
md <- c(list('inputSuite', 'amagla_sl_1'), list('examples', bs))
md

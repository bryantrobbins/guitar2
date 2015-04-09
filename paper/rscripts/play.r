p <- list('this', 'is', 'a', 'list')
p <- c(list('y', 'input'), p)
d <- data.frame(mat.or.vec(4, length(p)))
class(d)
colnames(d) <- p
rownames(d) <- list('t_whatever', 't_something', 't_ehhhhh', 't_nabrah')
d['t_whatever', 'input'] <- 1
d['t_nabrah', 'input'] <- 1
d

cat('Writing to csv\n')
prefix <- 'bryan'
output.file<- sprintf('data/%s_data.csv', prefix)
write.csv(d, file = output.file)

cat('Reading from csv\n')
input <- data.frame(read.csv(output.file))
class(input)
input

library(data.table)

loadData <- function(file) {
  # Load massive data file from csv
  data=fread(file, stringsAsFactors=TRUE)
  cat('Before filtering:', length(names(data)), 'features in dataset', '\n')

  # Drop event-related features
  #drop = grep("e[1-9]+", names(data), value=TRUE)
  #data=.data[,(drop):=NULL]

  #drop = grep("before", names(data), value=TRUE)
  #data=data[,(drop):=NULL]

  # Drop any factors which do not have more than 1 level
  filter = c('V1')
  for (ix in names(data)) {
    str <- sprintf("data$\"%s\"", ix)
   if(nlevels(eval(parse(text=str))) < 2) {
     filter <- c(filter, ix)
   }
  }
  data=data[,(filter):=NULL]
  cat('After filtering:', length(names(data)), 'features in dataset', '\n')

  train.data=data[isTraining=="1"]
  test.data=data[isTraining=="0"]
 
  # Drop filter var
  train.data=train.data[,isTraining:=NULL] 
  test.data=test.data[,isTraining:=NULL] 
 
  # Prepare training matrix
  cat('Creating training matrix', '\n')
  xm=model.matrix(isInfeas~. - 1, data=train.data, contrasts.arg = lapply(train.data[sapply(train.data, is.factor)], contrasts, contrasts=FALSE))
  x=apply(xm, 2, as.numeric)
  y=as.numeric(train.data$isInfeas)

  # Prepare test data
  cat('Creating test matrix', '\n')
  xtm=model.matrix(isInfeas~. - 1, data=test.data, contrasts.arg = lapply(test.data[sapply(test.data, is.factor)], contrasts, contrasts=FALSE))
  xt=apply(xtm, 2, as.numeric)
  actual=as.numeric(test.data$isInfeas)

  cat('Data loading complete.', '\n')
  
  ret = list( trainData=train.data,
                testData=test.data,
                trainMatrix=x,
                testMatrix=xt,
                trainY=y,
                testY=actual
          )

  return (ret)

}

loadAndWriteData <- function(fileIn, fileOut) {
  dd <- loadData(fileIn)
  saveRDS(dd, file = fileOut) 
  return(dd)
}

getFromS3 <- function(accessKey, secretKey, objectKey) {
  bucket <- 'com.btr3.research'
  S3_connect(accessKey, secretKey)
  S3_get_object(bucket, objectKey, objectKey)
}



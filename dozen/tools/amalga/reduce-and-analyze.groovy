#!/usr/bin/env groovy

@GrabResolver('http://guitar04.cs.umd.edu:9999/archiva/repository/snapshots/')
@GrabResolver('http://guitar04.cs.umd.edu:9999/archiva/repository/internal/')
@Grab(group='edu.umd.cs.guitar', module='testdata', version='1.0.7-SNAPSHOT')

import edu.umd.cs.guitar.testdata.TestDataManager
import edu.umd.cs.guitar.testdata.Reducer
import edu.umd.cs.guitar.testdata.Analyzer
import java.util.concurrent.TimeUnit

dbId = args[0]
inputSuiteId = args[1]
outputSuiteId = args[2]
type=args[3]
maxOrder=Integer.parseInt(args[4])

if(args.size() > 5){
	order=Integer.parseInt(args[5])
	threshold=Float.parseFloat(args[6])
}
else{
	order = -1
	threshold = -1.0
}

mongoHost="guitar04.cs.umd.edu"
mongoPort=27017

TestDataManager tdm = new TestDataManager(mongoHost, mongoPort)
Reducer red = new Reducer(dbId,mongoHost, mongoPort)
Analyzer anz = new Analyzer(dbId, mongoHost, mongoPort)

// These properties will be written out
def results = new Properties()
results.setProperty("dbId", dbId)
results.setProperty("inputSuiteId", inputSuiteId)
results.setProperty("outputSuiteId", outputSuiteId)
results.setProperty("type", type)
results.setProperty("maxOrder", "" + maxOrder)
results.setProperty("order", "" + order)

println("Clearing output suite " + outputSuiteId + " before reduciton")
tdm.clearTestSuite(dbId, outputSuiteId)

// Reduce
long startTime = System.nanoTime()
if(type.equalsIgnoreCase("hgs-code")){
	println("Reducing suite " + inputSuiteId + " by HGS code coverage into suite " + outputSuiteId)
	tdm.computeCoverage(dbId, inputSuiteId)
	red.reduceSuiteByHGSCoverage(inputSuiteId, outputSuiteId)
} else if(type.equalsIgnoreCase("prob")){
	println("Reducing suite " + inputSuiteId + " by probability into suite " + outputSuiteId)
	red.reduceSuiteByProbability(inputSuiteId, outputSuiteId, order, threshold)
} else {
	println("Unknown reduction type " + type + " in reduce-and-analyze.groovy")
	return;
}
long endTime = System.nanoTime()

// Record approx running time of reduction
long durationNano = endTime - startTime
duration = TimeUnit.SECONDS.convert(durationNano, TimeUnit.NANOSECONDS)
tdm.addSuiteProperty(dbId, outputSuiteId, "reduction_time", "" + duration)
results.setProperty(outputSuiteId + ".reduction.s", "" + duration)

// Record size of reduced suite
suiteSize = tdm.getTestIdsInSuite(dbId, outputSuiteId).size()
tdm.addSuiteProperty(dbId, outputSuiteId, "size", "" + suiteSize)
results.setProperty(outputSuiteId + ".size", "" + suiteSize)

// Analyze coverage
cScoreKey = outputSuiteId + ".coverage.score"
tdm.computeCoverage(dbId, outputSuiteId)
cScore = "" + anz.getPercentCovered(inputSuiteId, outputSuiteId)
tdm.addSuiteProperty(dbId, outputSuiteId, "coverage_score", cScore)
results.setProperty(cScoreKey, cScore)

// Analyze similarity
for(int j=2; j<=maxOrder; j++){
	mScoreKey = outputSuiteId + ".model." + j + ".score"
	tdm.computeModel(dbId, outputSuiteId, j)
	mScore = "" + anz.scoreModel(inputSuiteId, outputSuiteId)
	tdm.addSuiteProperty(dbId, outputSuiteId, "model_" + j + "_score", mScore)
	results.setProperty(mScoreKey, mScore)
}

// Write out properties file
results.store(new FileOutputStream("reduce.properties"), "Reduction and analysis")
println("Done")

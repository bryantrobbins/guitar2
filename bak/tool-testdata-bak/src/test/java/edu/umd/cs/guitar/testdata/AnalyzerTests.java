package edu.umd.cs.guitar.testdata;


import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class AnalyzerTests {

	private String dbId;
	private TestDataManager tdm;
	private Reducer red;
	private Analyzer ann;
	
	@Before
	public void setupSuites(){
		this.tdm = new TestDataManager("localhost", 37017);

		this.dbId = tdm.createDb();
		System.out.println("Using DB " + dbId);

		this.red = new Reducer("localhost", 37017, dbId);
		this.ann = new Analyzer(dbId, "localhost", 37017);
		
		for (int i = 1; i <= 10; i++) {
			String testId = "random-" + i;
			tdm.addTestCaseFromFiles(dbId, testId + "-suite", testId,
					"src/test/resources/random-" + i + ".tst",
					"src/test/resources/random-" + i + ".map",
					"src/test/resources/random-" + i + ".gui",
					"src/test/resources/random-" + i + ".ser");
			tdm.addTestToSuite(dbId, testId, "combined-small");
		}

		for (int i = 1; i <= 10; i++) {
			String testId = "same-" + i;
			tdm.addTestCaseFromFiles(dbId, testId + "-suite", testId,
					"src/test/resources/random-1.tst",
					"src/test/resources/random-1.map",
					"src/test/resources/random-1.gui",
					"src/test/resources/random-1.ser");
			tdm.addTestToSuite(dbId, testId, "combined-duplicate");
		}
	}
	

	@Test
	public void testGetPercentCovered() {
		tdm.computeCoverage(dbId, "combined-small");
		
		red.reduceSuiteByHGSCoverage("combined-small",
				"combined-coverage-reduced-hgs");
		
		tdm.computeCoverage(dbId, "combined-coverage-reduced-hgs");
		
		Assert.assertTrue(ann.getPercentCovered("combined-small", "combined-coverage-reduced-hgs") == 1.0);
	}
	
	@Test
	public void testScoreModel(){
		red.reduceSuiteByHGSCoverage("combined-small",
				"combined-coverage-reduced-hgs");
		
		tdm.computeModel(dbId, "combined-coverage-reduced-hgs", 2);
		
		float score = ann.scoreModel("combined-small", "combined-coverage-reduced-hgs");
		
		System.out.println("Score for HGS coverage-reduced suite is " + score);
	}
	
	@Test
	public void compareMeasures(){
		red.reduceSuiteByHGSCoverage("combined-small",
				"combined-coverage-reduced-hgs");
		red.reduceSuiteByProbability("combined-small", "combined-prob-reduced", 2, 0.05);

		tdm.computeCoverage(dbId, "combined-small");
		
		tdm.computeModel(dbId, "combined-coverage-reduced-hgs", 2);
		tdm.computeCoverage(dbId, "combined-coverage-reduced-hgs");
		
		tdm.computeModel(dbId, "combined-coverage-reduced-hgs", 2);
		tdm.computeCoverage(dbId, "combined-prob-reduced");
		
		float coverageScore = ann.scoreModel("combined-small", "combined-coverage-reduced-hgs");
		float probScore = ann.scoreModel("combined-small", "combined-prob-reduced");
		
		float coveragePercent = ann.getPercentCovered("combined-small", "combined-coverage-reduced-hgs");
		float probPercent = ann.getPercentCovered("combined-small", "combined-prob-reduced");
		
		int coverageSize = tdm.getTestIdsInSuite(dbId, "combined-coverage-reduced-hgs").size();
		int probSize = tdm.getTestIdsInSuite(dbId, "combined-prob-reduced").size();

		System.out.println("Score for HGS coverage-reduced suite is " + coverageScore + " and it covers " + coveragePercent + " with " + coverageSize + " tests.");
		System.out.println("Score for prob-reduced suite is " + probScore + " and it covers " + probPercent + " with " + probSize + " tests.");
	}
	
	

	

}

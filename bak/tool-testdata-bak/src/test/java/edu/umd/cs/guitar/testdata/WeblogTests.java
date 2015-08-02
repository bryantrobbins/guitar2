package edu.umd.cs.guitar.testdata;

import org.junit.Assert;

import org.junit.Test;

import edu.berkeley.nlp.lm.ArrayEncodedProbBackoffLm;
import edu.umd.cs.guitar.testdata.weblog.Weblog;
import edu.umd.cs.guitar.testdata.weblog.WeblogProcessor;

public class WeblogTests {

	@Test
	public void testAddArtifact() {
		TestDataManager tdm = new TestDataManager("localhost", 37017);

		String dbId = tdm.createDb();
		String testId = "weblogRecordedTest";
		String artifactPath = "src/test/resources/keywords-9-9";
		
		try {
			tdm.addArtifactToTest(dbId, testId, artifactPath, WeblogProcessor.class);
			Object logObj = tdm.getTestArtifact(dbId, testId, WeblogProcessor.class);
			Weblog wlog = (Weblog) logObj;
			Assert.assertEquals("\"GET__HTTP_1.1\"_80", wlog.getLines().get(0));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Caught exception during test");
		}
	}
	
	@Test
	public void testComputeModel(){
		TestDataManager tdm = new TestDataManager("localhost", 37017);

		String dbId = tdm.createDb();
		String suiteId = "weblogRecordedSuite";

		
		String testId = "weblogRecordedTest";
		String artifactPath = "src/test/resources/keywords-9-9";
		
		try {
			tdm.addArtifactToTest(dbId, testId, artifactPath, WeblogProcessor.class);
			tdm.addTestToSuite(dbId, testId, suiteId);
			ArrayEncodedProbBackoffLm<String> model = tdm.computeArtifactModel(dbId, suiteId, 2, WeblogProcessor.class);
			Assert.assertEquals(2, model.getLmOrder());
			Assert.assertEquals(38, model.getNgramMap().getNumNgrams(0));
			Assert.assertEquals(75, model.getNgramMap().getNumNgrams(1));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Caught exception during test");
		}
	}
}

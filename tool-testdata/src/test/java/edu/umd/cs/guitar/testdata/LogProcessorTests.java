package edu.umd.cs.guitar.testdata;

import edu.umd.cs.guitar.testdata.guitar.LogProcessor;
import edu.umd.cs.guitar.testdata.processor.TextObject;
import org.junit.Assert;
import org.junit.Test;

public class LogProcessorTests {

	@Test
	public void testAddArtifactToExecution() {
		TestDataManager tdm = new TestDataManager("localhost", 37017);

		String dbId = tdm.createDb();
        System.out.println("Using db id " + dbId);
		String testId = "unitTestTest";
        String execId = "e002";
		String artifactPath = "src/test/resources/keywords-9-9";
		
		try {
			tdm.addArtifactToExecution(dbId, testId, execId, artifactPath, LogProcessor.class);
            Object logObj = tdm.getExecutionArtifact(dbId, testId, execId, LogProcessor.class);
            TextObject log = (TextObject) logObj;
            Assert.assertEquals("\"GET__HTTP_1.1\"_80", log.getLine(0));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Caught exception during test");
		}
	}
}

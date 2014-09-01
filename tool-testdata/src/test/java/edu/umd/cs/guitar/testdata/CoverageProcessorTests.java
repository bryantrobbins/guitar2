package edu.umd.cs.guitar.testdata;

import edu.umd.cs.guitar.testdata.guitar.CoverageProcessor;
import edu.umd.cs.guitar.testdata.guitar.LogProcessor;
import edu.umd.cs.guitar.testdata.processor.TextObject;
import net.sourceforge.cobertura.coveragedata.ProjectData;
import org.junit.Assert;
import org.junit.Test;

public class CoverageProcessorTests {

	@Test
	public void testAddArtifactToExecution() {
		TestDataManager tdm = new TestDataManager("localhost", 37017);

		String dbId = tdm.createDb();
        System.out.println("Using db id " + dbId);
		String testId = "unitTestTest";
        String execId = "e002";
		String artifactPath = "src/test/resources/random-1.ser";
		
		try {
			tdm.addArtifactToExecution(dbId, testId, execId, artifactPath, CoverageProcessor.class);
            Object logObj = tdm.getExecutionArtifact(dbId, testId, execId, CoverageProcessor.class);
            ProjectData data = (ProjectData) logObj;
            int cNum = data.getNumberOfClasses();
            Assert.assertEquals(26, cNum);
   		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Caught exception during test");
		}
	}
}

package edu.umd.cs.guitar.testdata;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import net.sourceforge.cobertura.coveragedata.CoverageDataFileHandler;
import net.sourceforge.cobertura.coveragedata.ProjectData;

import org.junit.Assert;

import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.testdata.berkeleylm.ArpaObjectLmReader;
import edu.umd.cs.guitar.testdata.guitar.TestCaseIterator;
import edu.umd.cs.guitar.testdata.util.CoberturaUtils;

import org.junit.Test;

public class CoberturaUtilsTests {

	@Test
	public void testAddedCoveredLines() throws URISyntaxException{
		URL serUrlA = ArpaObjectLmReader.class.getResource("/random-1.ser");
		String pathA = serUrlA.toURI().getPath();
		
		URL serUrlB = ArpaObjectLmReader.class.getResource("/random-2.ser");
		String pathB = serUrlB.toURI().getPath();
		
		ProjectData dataA = CoverageDataFileHandler.loadCoverageData(new File(pathA));
		ProjectData dataB = CoverageDataFileHandler.loadCoverageData(new File(pathB));
		
		// How many lines would dataB add to dataA?
		int added = CoberturaUtils.addedCoveredLines(dataA, dataB);
		
		System.out.println("File B adds " + added + " newly covered lines.");
		Assert.assertEquals(18, added);
	}
	
	@Test
	public void testDoesCoverageMeetGoal(){
		TestDataManager tdm = new TestDataManager("localhost", 37017);

		String dbId = tdm.createDb();
		System.out.println("Using DB " + dbId);

		for (int i = 1; i <= 10; i++) {
			String testId = "random-" + i;
			tdm.addTestCaseFromFiles(dbId, testId + "-suite", testId,
					"src/test/resources/random-" + i + ".tst",
					"src/test/resources/random-" + i + ".map",
					"src/test/resources/random-" + i + ".gui",
					"src/test/resources/random-" + i + ".ser");
			tdm.addTestToSuite(dbId, testId, "combined");
		}
		
		ProjectData combined = tdm.computeCoverage(dbId, "combined");
		
		for(int i=1; i<=10; i++){
			String testId = "random-" + i;
			ProjectData singleCov = tdm.getCoverageObjectForTest(dbId, testId);
			Assert.assertFalse(CoberturaUtils.doesCoverageMeetGoal(combined, singleCov));
		}
		
		Assert.assertTrue(CoberturaUtils.doesCoverageMeetGoal(combined, combined));
	}
	
}

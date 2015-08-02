package edu.umd.cs.guitar.testdata;

import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.berkeley.nlp.lm.ArrayEncodedProbBackoffLm;
import edu.berkeley.nlp.lm.ContextEncodedProbBackoffLm;
import edu.berkeley.nlp.lm.map.NgramMap;
import edu.berkeley.nlp.lm.values.ProbBackoffPair;
import edu.umd.cs.guitar.model.data.StepType;
import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.testdata.guitar.TestCaseIterator;
import net.sourceforge.cobertura.coveragedata.ProjectData;

import org.apache.commons.io.FileUtils;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.DifferenceListener;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class TestDataManagerTests {

	@Test
	public void testConstruction() {
		TestDataManager tdm = new TestDataManager("localhost", 37017);
	}

	@Test
	public void testAddTestCase() {
		TestDataManager tdm = new TestDataManager("localhost", 37017);

		String dbId = tdm.createDb();
		String testId = "random_1";
		tdm.addTestCaseFromFiles(dbId, "input", testId,
				"src/test/resources/random-1.tst",
				"src/test/resources/random-1.map",
				"src/test/resources/random-1.gui",
				"src/test/resources/random-1.ser");

		TestCase tc = tdm.getTestCase(dbId, "random_1");
		List<StepType> stepList = tc.getStep();

		for (StepType st : stepList) {
			System.out.println(st.getEventId());
		}
	}

	@Test
	public void testAddTestToSuite() {
		TestDataManager tdm = new TestDataManager("localhost", 37017);

		String dbId = tdm.createDb();
		String suiteId = "input";

		for (int i = 0; i < 5; i++) {
			String testId = "random" + "_" + i;
			tdm.addTestCaseFromFiles(dbId, null, testId,
					"src/test/resources/random-1.tst",
					"src/test/resources/random-1.map",
					"src/test/resources/random-1.gui",
					"src/test/resources/random-1.ser");
			if (i < 3) {
				tdm.addTestToSuite(dbId, testId, suiteId);
			}
		}

		TestCase tc = tdm.getTestCase(dbId, "random_1");
		List<StepType> stepList = tc.getStep();

		for (StepType st : stepList) {
			System.out.println(st.getEventId());
		}
	}

	@Test
	public void testComputeCoverage() {
		TestDataManager tdm = new TestDataManager("localhost", 37017);

		String dbId = tdm.createDb();
		System.out.println("Using DB " + dbId);
		String combinedSuite = "total";

		for (int i = 1; i <= 10; i++) {
			String testId = "random-" + i;
			tdm.addTestCaseFromFiles(dbId, testId + "-suite", testId,
					"src/test/resources/random-" + i + ".tst",
					"src/test/resources/random-" + i + ".map",
					"src/test/resources/random-" + i + ".gui",
					"src/test/resources/random-" + i + ".ser");
			tdm.addTestToSuite(dbId, testId, combinedSuite);
			tdm.computeCoverage(dbId, testId + "-suite");

			// Get and compare XML strings
			try {
				URL url = Resources.getResource("random-" + i + ".cov");
				String expected = Resources.toString(url, Charsets.UTF_8);
				String actual = tdm.getCoverageReport(dbId, testId + "-suite");
				
				DifferenceListener mine = new IgnoreTimestampAndVersionDifferenceListener();
				Diff myDiff = new Diff(expected, actual);
				myDiff.overrideDifferenceListener(mine);
				
				Assert.assertTrue("XML similar " + myDiff.toString(),
			               myDiff.similar());
			} catch (IOException e) {
				Assert.fail("IOException when comparing xml docs");
			} catch (SAXException e) {
				Assert.fail("SAXException when parsing xml docs");
			}
		}

		ProjectData pd = tdm.computeCoverage(dbId, combinedSuite);

		if (pd == null) {
			Assert.fail("No object returned from computeCoverage");
		}

		URL url = Resources.getResource("combined.cov");
		try {
			String expected = Resources.toString(url, Charsets.UTF_8);
			String actual = tdm.getCoverageReport(dbId, combinedSuite);
			
			DifferenceListener mine = new IgnoreTimestampAndVersionDifferenceListener();
			Diff myDiff = new Diff(expected, actual);
			myDiff.overrideDifferenceListener(mine);
			
			Assert.assertTrue("XML similar " + myDiff.toString(),
		               myDiff.similar());

		} catch (IOException e) {
			Assert.fail("Problem retrieving expected coverage report");
		} catch (SAXException e) {
			Assert.fail("Problem parsing XML files during comparison");
		}
	}
	
	@Test
	public void testComputeModel(){
		String combinedSuite = "combined";
		TestDataManager tdm = new TestDataManager("localhost", 37017);
		String dbId = tdm.createDb();

		for(int i=1; i<=10; i++){
			String testId = "random-" + i;
			tdm.addTestCaseFromFiles(dbId, testId + "-suite", testId,
					"src/test/resources/random-" + i + ".tst",
					"src/test/resources/random-" + i + ".map",
					"src/test/resources/random-" + i + ".gui",
					"src/test/resources/random-" + i + ".ser");
			tdm.addTestToSuite(dbId, testId, combinedSuite);
		}
		
		// Now it's on
		ArrayEncodedProbBackoffLm<String> model = tdm.computeModel(dbId, combinedSuite, 5);
		
		// Assert the max order (as reported by the raw model)
		Assert.assertEquals(5, model.getLmOrder());
		
		NgramMap<ProbBackoffPair> grams = model.getNgramMap();

		// Assert number of ngrams counts in model
		Assert.assertEquals(25, grams.getNumNgrams(0));
		Assert.assertEquals(66, grams.getNumNgrams(1));
		Assert.assertEquals(115, grams.getNumNgrams(2));
		Assert.assertEquals(29, grams.getNumNgrams(3));
		Assert.assertEquals(7, grams.getNumNgrams(4));

		// Assert number of events in model
		Assert.assertEquals(25, model.getWordIndexer().numWords());
		System.out.println("Model has " + model.getWordIndexer().numWords() + " words indexed");
	}
	
	@Test
	public void testComputeModelSimple(){
		TestDataManager tdm = new TestDataManager("localhost", 37017);
		String dbId = tdm.createDb();
		String combinedSuite = "simple-combined";
		
		for (int i = 1; i <= 100; i++) {
			String testId = "simple-" + i;
			tdm.addTestCaseFromFiles(dbId, testId + "-suite", testId,
					"src/test/resources/simple.tst",
					"src/test/resources/random-1.map",
					"src/test/resources/random-1.gui",
					"src/test/resources/random-1.ser");
			tdm.addTestToSuite(dbId, testId, combinedSuite);
		}
		
		// Now it's on
		ArrayEncodedProbBackoffLm<String> model = tdm.computeModel(dbId, combinedSuite, 5);
		
		NgramMap<ProbBackoffPair> grams = model.getNgramMap();
	}
	
	@Test
	public void testAddSuiteProperty(){
		TestDataManager tdm = new TestDataManager("localhost", 37017);
		String dbId = tdm.createDb();
		String combinedSuite = "combined-for-property";
		
		for(int i=1; i<=10; i++){
			String testId = "random-" + i;
			tdm.addTestCaseFromFiles(dbId, testId + "-suite", testId,
					"src/test/resources/random-" + i + ".tst",
					"src/test/resources/random-" + i + ".map",
					"src/test/resources/random-" + i + ".gui",
					"src/test/resources/random-" + i + ".ser");
			tdm.addTestToSuite(dbId, testId, combinedSuite);
		}
		
		tdm.addSuiteProperty(dbId, combinedSuite, "myProp", "myVal");
		Assert.assertEquals("myVal", tdm.getSuiteProperty(dbId, combinedSuite, "myProp"));

	}
	
	

}

package edu.umd.cs.guitar.testdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.cobertura.coveragedata.ProjectData;

import org.junit.Test;
import org.junit.Assert;

import edu.berkeley.nlp.lm.ArrayEncodedProbBackoffLm;
import edu.berkeley.nlp.lm.ConfigOptions;
import edu.berkeley.nlp.lm.ContextEncodedProbBackoffLm;
import edu.berkeley.nlp.lm.StringWordIndexer;
import edu.berkeley.nlp.lm.WordIndexer;
import edu.berkeley.nlp.lm.io.LmReaders;
import edu.berkeley.nlp.lm.map.NgramMap;
import edu.berkeley.nlp.lm.map.NgramMap.Entry;
import edu.berkeley.nlp.lm.values.ProbBackoffPair;
import edu.umd.cs.guitar.testdata.berkeleylm.ArpaObject;
import edu.umd.cs.guitar.testdata.berkeleylm.ArpaObjectLmReader;
import edu.umd.cs.guitar.testdata.util.BerkeleyLMUtils;
import edu.umd.cs.guitar.testdata.util.CoberturaUtils;

public class ReducerTests {

	private ContextEncodedProbBackoffLm<String> getModelFromResource(
			String urlString) throws IOException {
		ArpaObject ao = new ArpaObject();

		// This idiom based on
		// http://stackoverflow.com/questions/5868369/how-to-read-a-large-text-file-line-by-line-in-java
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(
					ReducerTests.class.getResourceAsStream(urlString)));
			String line;
			while ((line = br.readLine()) != null) {
				ao.addLine(line);
			}
		} finally {
			br.close();
		}

		ao.setMaxOrder(5);
		WordIndexer<String> ix = new StringWordIndexer();
		ArpaObjectLmReader<String> arpaReader = new ArpaObjectLmReader<String>(
				ao, ix);
		ContextEncodedProbBackoffLm<String> actual = LmReaders
				.readContextEncodedLmFromArpa(arpaReader, ix,
						new ConfigOptions());

		return actual;
	}

	private ArrayEncodedProbBackoffLm<String> getDupModelFromSimpleTestCase(
			TestDataManager tdm, String dbId) {
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

		return tdm.computeModel(dbId, combinedSuite, 2);
	}

	private ArrayEncodedProbBackoffLm<String> getModelFromTestCases(
			TestDataManager tdm, String dbId) {
		String combinedSuite = "combined";

		for (int i = 1; i <= 10; i++) {
			String testId = "random-" + i;
			tdm.addTestCaseFromFiles(dbId, testId + "-suite", testId,
					"src/test/resources/random-" + i + ".tst",
					"src/test/resources/random-" + i + ".map",
					"src/test/resources/random-" + i + ".gui",
					"src/test/resources/random-" + i + ".ser");
			tdm.addTestToSuite(dbId, testId, combinedSuite);
		}

		return tdm.computeModel(dbId, combinedSuite, 5);
	}

	// @Test
	// public void testNgramReductionSimple() throws IOException{
	// TestDataManager tdm = new TestDataManager();
	// String dbId = tdm.createDb();
	// ArrayEncodedProbBackoffLm<String> model =
	// getDupModelFromSimpleTestCase(tdm, dbId);
	//
	// tdm.getNgramModel(dbId, "simple-combined");
	// BerkeleyLMUtils.printAllNgrams(model.getNgramMap(),
	// model.getWordIndexer());
	//
	// Reducer reducer = new Reducer(dbId);
	// reducer.reduceSuiteByNgrams("simple-combined", "simple-ngram-reduced",
	// 2);
	//
	// int size = tdm.getTestIdsInSuite(dbId, "simple-ngram-reduced").size();
	// System.out.println("Simple reduced suite has " + size + " tests");
	// }
	//
	//
	// @Test
	// public void testNgramReduction() throws IOException{
	// TestDataManager tdm = new TestDataManager();
	// String dbId = tdm.createDb();
	// getModelFromTestCases(tdm, dbId);
	// Reducer reducer = new Reducer(dbId);
	//
	// reducer.reduceSuiteByNgrams("combined", "ngram-reduced", 1);
	// int size = tdm.getTestIdsInSuite(dbId, "ngram-reduced").size();
	// System.out.println("Reduced suite has " + size + " tests");
	//
	// reducer.reduceSuiteByNgrams("combined", "ngram-reduced", 2);
	// size = tdm.getTestIdsInSuite(dbId, "ngram-reduced").size();
	// System.out.println("Reduced suite has " + size + " tests");
	//
	// reducer.reduceSuiteByNgrams("combined", "ngram-reduced", 3);
	// size = tdm.getTestIdsInSuite(dbId, "ngram-reduced").size();
	// System.out.println("Reduced suite has " + size + " tests");
	//
	// reducer.reduceSuiteByNgrams("combined", "ngram-reduced", 4);
	// size = tdm.getTestIdsInSuite(dbId, "ngram-reduced").size();
	// System.out.println("Reduced suite has " + size + " tests");
	// }

	@Test
	public void testCoverageReductionDuplicates() throws IOException {
		TestDataManager tdm = new TestDataManager("localhost", 37017);

		String dbId = tdm.createDb();
		System.out.println("Using DB " + dbId);

		for (int i = 1; i <= 10; i++) {
			String testId = "same-" + i;
			tdm.addTestCaseFromFiles(dbId, testId + "-suite", testId,
					"src/test/resources/random-1.tst",
					"src/test/resources/random-1.map",
					"src/test/resources/random-1.gui",
					"src/test/resources/random-1.ser");
			tdm.addTestToSuite(dbId, testId, "combined");
		}

		tdm.computeCoverage(dbId, "combined");
		Reducer red = new Reducer(dbId);
		// red.reduceSuiteByRandomCoverage("combined",
		// "combined-coverage-reduced");
		red.reduceSuiteByHGSCoverage("combined", "combined-coverage-reduced");

		List<String> ids = tdm.getTestIdsInSuite(dbId,
				"combined-coverage-reduced");
		Assert.assertEquals(1, ids.size());
	}

	@Test
	public void testCoverageReductionSmall() throws IOException {
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

		tdm.computeCoverage(dbId, "combined");
		Reducer red = new Reducer(dbId);
		red.reduceSuiteByRandomCoverage("combined",
				"combined-coverage-reduced-random");
		red.reduceSuiteByHGSCoverage("combined",
				"combined-coverage-reduced-hgs");

		List<String> idsHgs = tdm.getTestIdsInSuite(dbId,
				"combined-coverage-reduced-hgs");
		List<String> idsRandom = tdm.getTestIdsInSuite(dbId,
				"combined-coverage-reduced-random");
		System.out.println("Random reduced suite has " + idsRandom.size()
				+ ", HGS suite has " + idsHgs.size());

//		Assert.assertTrue(idsRandom.size() >= idsHgs.size());

		// for(String id : ids){
		// System.out.println(id);
		// }
	}

	@Test
	public void testProbabilityReductionDuplicate() {
		TestDataManager tdm = new TestDataManager("localhost", 37017);

		String dbId = tdm.createDb();
		System.out.println("Using DB " + dbId);

		for (int i = 1; i <= 10; i++) {
			String testId = "same-" + i;
			tdm.addTestCaseFromFiles(dbId, testId + "-suite", testId,
					"src/test/resources/random-1.tst",
					"src/test/resources/random-1.map",
					"src/test/resources/random-1.gui",
					"src/test/resources/random-1.ser");
			tdm.addTestToSuite(dbId, testId, "combined");
		}
		
		Reducer red = new Reducer(dbId);

		red.reduceSuiteByProbability("combined", "combined-reduced-prob-2", 2, 0.05);
		Assert.assertEquals(1, tdm.getTestIdsInSuite(dbId, "combined-reduced-prob-2").size());
		
		red.reduceSuiteByProbability("combined", "combined-reduced-prob-3", 3, 0.05);
		Assert.assertEquals(1, tdm.getTestIdsInSuite(dbId, "combined-reduced-prob-3").size());

	}
	
	@Test
	public void testProbabilityReductionSmall(){
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

		Reducer red = new Reducer(dbId);
		
		red.reduceSuiteByProbability("combined", "combined-small-reduced-prob-2", 2, 0.05);
		System.out.println(tdm.getTestIdsInSuite(dbId, "combined-small-reduced-prob-2").size());
		red.reduceSuiteByProbability("combined", "combined-small-reduced-prob-3", 3, 0.05);
		System.out.println(tdm.getTestIdsInSuite(dbId, "combined-small-reduced-prob-3").size());
		red.reduceSuiteByProbability("combined", "combined-small-reduced-prob-4", 4, 0.05);
		System.out.println(tdm.getTestIdsInSuite(dbId, "combined-small-reduced-prob-4").size());
	}
	
	@Test
	public void testRandomReduction(){
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
			tdm.addTestToSuite(dbId, testId, "combinedForRandom");
		}
		
		Reducer red = new Reducer(dbId);
		red.reduceSuiteBySampling("combinedForRandom", "sampled", 5);
		Assert.assertEquals(5, tdm.getTestIdsInSuite(dbId, "sampled").size());
	}
}

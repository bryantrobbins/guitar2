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

public class BerkeleyLMUtilsTests {

	private ArrayEncodedProbBackoffLm<String> getModelFromResource(
			String urlString) throws IOException {
		ArpaObject ao = new ArpaObject();

		// This idiom based on
		// http://stackoverflow.com/questions/5868369/how-to-read-a-large-text-file-line-by-line-in-java
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(
					BerkeleyLMUtilsTests.class.getResourceAsStream(urlString)));
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
		ArrayEncodedProbBackoffLm<String> actual = LmReaders
				.readArrayEncodedLmFromArpa(arpaReader, false, ix,
						new ConfigOptions());

		return actual;
	}

	private ArrayEncodedProbBackoffLm<String> getModelFromTestCases(
			TestDataManager tdm, String dbId) throws IOException {
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

	@Test
	public void testGetMaxOrderDuplicate() throws IOException {
		ArrayEncodedProbBackoffLm<String> model = getModelFromResource("/kneserNeyFromText.arpa");
		ArrayEncodedProbBackoffLm<String> modelCopy = getModelFromResource("/kneserNeyFromText.arpa");

		Assert.assertEquals(5, BerkeleyLMUtils.getFunctionalMaxOrder(model, modelCopy));
	}

	@Test
	public void testGetMaxOrderOneShort() throws IOException {
		TestDataManager tdm = new TestDataManager("localhost", 37017);
		String dbId = tdm.createDb();
		ArrayEncodedProbBackoffLm<String> model = getModelFromResource("/kneserNeyFromText.arpa");
		ArrayEncodedProbBackoffLm<String> modelB = getModelFromTestCases(tdm,
				dbId);
		Assert.assertEquals(5, BerkeleyLMUtils.getFunctionalMaxOrder(model, modelB));
	}

//	@Test
//	public void testAreNgramsSubsumedDuplicate() throws IOException {
//		ArrayEncodedProbBackoffLm<String> model = getModelFromResource("/kneserNeyFromText.arpa");
//		ArrayEncodedProbBackoffLm<String> modelCopy = getModelFromResource("/kneserNeyFromText.arpa");
//
//		Assert.assertTrue(BerkeleyLMUtils
//				.areNgramsSubsumed(model, modelCopy, 1) == 0);
//		Assert.assertTrue(BerkeleyLMUtils
//				.areNgramsSubsumed(model, modelCopy, 2) == 0);
//		Assert.assertTrue(BerkeleyLMUtils
//				.areNgramsSubsumed(model, modelCopy, 3) == 0);
//		Assert.assertTrue(BerkeleyLMUtils
//				.areNgramsSubsumed(model, modelCopy, 4) == 0);
//		Assert.assertTrue(BerkeleyLMUtils
//				.areNgramsSubsumed(model, modelCopy, 5) == 0);
//	}

//	@Test
//	public void testAreNgramsSubsumedMismatch() throws IOException {
//		TestDataManager tdm = new TestDataManager("localhost", 37017);
//		String dbId = tdm.createDb();
//
//		ArrayEncodedProbBackoffLm<String> model = getModelFromResource("/kneserNeyFromText.arpa");
//		ArrayEncodedProbBackoffLm<String> modelB = getModelFromTestCases(tdm,
//				dbId);
//		Assert.assertFalse(BerkeleyLMUtils.areNgramsSubsumed(model, modelB, 1) == 0);
//		Assert.assertFalse(BerkeleyLMUtils.areNgramsSubsumed(model, modelB, 2) == 0);
//		Assert.assertFalse(BerkeleyLMUtils.areNgramsSubsumed(model, modelB, 3) == 0);
//		Assert.assertFalse(BerkeleyLMUtils.areNgramsSubsumed(model, modelB, 4) == 0);
//		Assert.assertFalse(BerkeleyLMUtils.areNgramsSubsumed(model, modelB, 5) == 0);
//
//	}
//
//	@Test
//	public void testAreNgramsSubsumedWithinTCM() throws IOException {
//		TestDataManager tdm = new TestDataManager("localhost", 37017);
//		String dbId = tdm.createDb();
//		ArrayEncodedProbBackoffLm<String> model = getModelFromTestCases(tdm,
//				dbId);
//
//		boolean achieved = false;
//		int number = -1;
//		int order = 2;
//
//		for (int i = 10; i >= 1; i--) {
//			tdm.addTestToSuite(dbId, "random-" + i, "candidate");
//			tdm.computeModel(dbId, "candidate", 2);
//			ArrayEncodedProbBackoffLm<String> candidate = tdm.getNgramModel(
//					dbId, "candidate", TestDataManagerDefaults.KEY_MODEL);
//			if (BerkeleyLMUtils.areNgramsSubsumed(model, candidate, 1) == 0) {
//				achieved = true;
//				number = i;
//				break;
//			}
//		}
//
//		if (achieved) {
//			Assert.assertTrue(number == 2);
//		} else {
//			Assert.fail("Model N-grams never subsumed");
//		}
//
//
//
//	}

	@Test
	public void testDoesSequenceAddNewNgrams() throws IOException {
		ArrayEncodedProbBackoffLm<String> model = getModelFromResource("/kneserNeyFromText.arpa");
		System.out.println(model.getWordIndexer().numWords());

		// Adds a new word
		Assert.assertTrue(BerkeleyLMUtils.doesSequenceAddNewNgrams(
				Arrays.asList("foo bar baz".split(" ")), model, 1));
		Assert.assertTrue(BerkeleyLMUtils.doesSequenceAddNewNgrams(
				Arrays.asList("foo bar baz".split(" ")), model, 2));
		Assert.assertTrue(BerkeleyLMUtils.doesSequenceAddNewNgrams(
				Arrays.asList("foo bar baz".split(" ")), model, 3));
		Assert.assertTrue(BerkeleyLMUtils.doesSequenceAddNewNgrams(
				Arrays.asList("foo bar baz".split(" ")), model, 4));
		Assert.assertTrue(BerkeleyLMUtils.doesSequenceAddNewNgrams(
				Arrays.asList("foo bar baz".split(" ")), model, 5));

		// Adds a new n-gram
		Assert.assertFalse(BerkeleyLMUtils.doesSequenceAddNewNgrams(
				Arrays.asList("service blaze australia".split(" ")), model, 1));
		Assert.assertTrue(BerkeleyLMUtils.doesSequenceAddNewNgrams(
				Arrays.asList("service blaze australia".split(" ")), model, 2));
		Assert.assertTrue(BerkeleyLMUtils.doesSequenceAddNewNgrams(
				Arrays.asList("service blaze australia".split(" ")), model, 3));
		Assert.assertTrue(BerkeleyLMUtils.doesSequenceAddNewNgrams(
				Arrays.asList("service blaze australia".split(" ")), model, 4));
		Assert.assertTrue(BerkeleyLMUtils.doesSequenceAddNewNgrams(
				Arrays.asList("service blaze australia".split(" ")), model, 5));

		// Adds nothing
		Assert.assertFalse(BerkeleyLMUtils.doesSequenceAddNewNgrams(
				Arrays.asList("ordinary citizens have their doubts".split(" ")),
				model, 1));
		Assert.assertFalse(BerkeleyLMUtils.doesSequenceAddNewNgrams(
				Arrays.asList("ordinary citizens have their doubts".split(" ")),
				model, 2));
		Assert.assertFalse(BerkeleyLMUtils.doesSequenceAddNewNgrams(
				Arrays.asList("ordinary citizens have their doubts".split(" ")),
				model, 3));
		Assert.assertFalse(BerkeleyLMUtils.doesSequenceAddNewNgrams(
				Arrays.asList("ordinary citizens have their doubts".split(" ")),
				model, 4));
		Assert.assertFalse(BerkeleyLMUtils.doesSequenceAddNewNgrams(
				Arrays.asList("ordinary citizens have their doubts".split(" ")),
				model, 5));
	}

}

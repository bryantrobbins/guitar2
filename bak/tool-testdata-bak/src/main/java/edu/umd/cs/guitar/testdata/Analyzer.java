package edu.umd.cs.guitar.testdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.umd.cs.guitar.testdata.loader.ArtifactProcessor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import edu.berkeley.nlp.lm.ArrayEncodedProbBackoffLm;
import edu.berkeley.nlp.lm.collections.Iterators;
import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.testdata.guitar.TestCaseIterator;
import edu.umd.cs.guitar.testdata.util.CoberturaUtils;
import edu.umd.cs.guitar.testdata.weblog.WeblogProcessor;
import net.sourceforge.cobertura.coveragedata.ProjectData;

public class Analyzer {

	private TestDataManager manager;
	private String dbId;
	
	private static Logger logger = LogManager.getLogger(Analyzer.class);

	public Analyzer(String dbId, String host, int port) {
		this.dbId = dbId;
		this.manager = new TestDataManager(host, port);
	}

	public Analyzer(String dbId) {
		this.manager = new TestDataManager();
		this.dbId = dbId;
	}

	public float getPercentCovered(String inputSuiteId, String outputSuiteId) {

		ProjectData inputCoverage = manager.getCoverageObject(dbId,
				inputSuiteId);
		ProjectData outputCoverage = manager.getCoverageObject(dbId,
				outputSuiteId);

		Set<String> attainableLines = CoberturaUtils
				.getIdsForLinesCovered(inputCoverage);
		Set<String> acheivedLines = CoberturaUtils
				.getIdsForLinesCovered(outputCoverage);

		int attainedLineCount = 0;
		int totalLineCount = 0;
		for (String attainableLine : attainableLines) {
			if (acheivedLines.contains(attainableLine)) {
				attainedLineCount++;
			}
			totalLineCount++;
		}

		return (float) ((attainedLineCount * 1.0) / (totalLineCount * 1.0));
	}

	public float scoreLine(String line,
			ArrayEncodedProbBackoffLm<String> outputSuiteModel,
			boolean useWrapperSymbols) {
		final String[] split = line.trim().split(" ");

		final int[] sent;
		int k;

		if (useWrapperSymbols) {
			sent = new int[split.length + 2];
			sent[0] = outputSuiteModel.getWordIndexer()
					.getOrAddIndexFromString(
							outputSuiteModel.getWordIndexer().getStartSymbol());
			sent[sent.length - 1] = outputSuiteModel.getWordIndexer()
					.getOrAddIndexFromString(
							outputSuiteModel.getWordIndexer().getEndSymbol());
			k = 1;
		}

		else {
			sent = new int[split.length];
			k = 0;
		}

		for (final String s : split) {
			sent[k++] = outputSuiteModel.getWordIndexer()
					.getIndexPossiblyUnk(s);
		}

		float sentScore = 0.0f;
		for (int i = 2; i <= Math.min(outputSuiteModel.getLmOrder(),
				sent.length); ++i) {
			final float score = outputSuiteModel.getLogProb(sent, 0, i);
			sentScore += score;
		}

		for (int i = 1; i <= sent.length - outputSuiteModel.getLmOrder(); ++i) {
			final float score = outputSuiteModel.getLogProb(sent, i, i
					+ outputSuiteModel.getLmOrder());
			sentScore += score;
		}

		return sentScore;

	}

	public float scoreSequence(String testId,
			ArrayEncodedProbBackoffLm<String> model,
			Class<? extends ArtifactProcessor<?>> processorClass,
			boolean useWrapperSymbols) throws InstantiationException,
			IllegalAccessException {
		return scoreSequence(testId, model, processorClass.newInstance(),
				useWrapperSymbols);
	}

	public float scoreSequence(String testId,
			ArrayEncodedProbBackoffLm<String> outputSuiteModel,
			ArtifactProcessor<?> proc, boolean useWrapperSymbols)
			throws InstantiationException, IllegalAccessException {

		List<Object> aList = new ArrayList<Object>();
		aList.add(manager.getTestArtifact(dbId, testId, proc));
		float logScore = 0.0f;

		for (final String line : Iterators.able(proc.getIterator(aList))) {
			logScore += scoreLine(line, outputSuiteModel, useWrapperSymbols);
		}

		return logScore;
	}

	/*
	 * This function scores a model based on the probability of the input suite
	 * according to a model of the output suite. The approach here is to sum the
	 * probability of each input test case x, according to a model of the output
	 * suite q. Because each test case appears once, p(x) in the entropy
	 * calculation would be 1 / N, where N is the number of test cases in the
	 * input suite. Therefore, we are computing -N * H(p, q). A higher value
	 * represents a better model, as it means the model of the output suite
	 * assigns higher probabilities to original test cases.
	 * 
	 * The code in this function is directly based on a JUnit test from the
	 * BerkeleyLM codebase which computes the probability of each sentence in a
	 * provided input and sums to compute a quantity which would be -N * H(p, q)
	 * (the negative of the entropy between p and q without having divided over
	 * all N in the input suite, where p in our case is the original input suite
	 * and q is the output suite).
	 * 
	 * Note that the order used by this computation depends on the order used to
	 * compute the output model prior to calling this function.
	 */

	public float scoreModel(String inputSuiteId, String outputSuiteId) {

		List<TestCase> inputTestCases = new ArrayList<TestCase>();

		// Get input sequences
		for (String testId : manager.getTestIdsInSuite(dbId, inputSuiteId)) {
			inputTestCases.add(manager.getTestCase(dbId, testId));
		}

		TestCaseIterator inputIterator = new TestCaseIterator(inputTestCases);

		ArrayEncodedProbBackoffLm<String> outputSuiteModel = manager
				.getNgramModel(dbId, outputSuiteId,
						TestDataManagerDefaults.KEY_MODEL);

		float logScore = 0.0f;

		for (final String line : Iterators.able(inputIterator)) {
			final String[] split = line.trim().split(" ");
			final int[] sent = new int[split.length + 2];
			sent[0] = outputSuiteModel.getWordIndexer()
					.getOrAddIndexFromString(
							outputSuiteModel.getWordIndexer().getStartSymbol());
			sent[sent.length - 1] = outputSuiteModel.getWordIndexer()
					.getOrAddIndexFromString(
							outputSuiteModel.getWordIndexer().getEndSymbol());
			int k = 1;
			for (final String s : split) {
				sent[k++] = outputSuiteModel.getWordIndexer()
						.getIndexPossiblyUnk(s);

			}
			float sentScore = 0.0f;
			for (int i = 2; i <= Math.min(outputSuiteModel.getLmOrder(),
					sent.length); ++i) {
				final float score = outputSuiteModel.getLogProb(sent, 0, i);
				sentScore += score;
			}
			for (int i = 1; i <= sent.length - outputSuiteModel.getLmOrder(); ++i) {
				final float score = outputSuiteModel.getLogProb(sent, i, i
						+ outputSuiteModel.getLmOrder());
				sentScore += score;
			}
			logScore += sentScore;
		}

		return logScore;
	}

	public String categorizeStoredSequence(String testId,
			List<String> suiteIds, int order,
			Class<? extends ArtifactProcessor<?>> processorClass,
			boolean useWrapperSymbols) throws InstantiationException,
			IllegalAccessException {
		Map<String, ArrayEncodedProbBackoffLm<String>> models = new HashMap<String, ArrayEncodedProbBackoffLm<String>>();

		for (String suiteId : suiteIds) {
			ArrayEncodedProbBackoffLm<String> model = manager
					.computeArtifactModel(dbId, suiteId, order,
							WeblogProcessor.class);
			models.put(suiteId, model);
		}

		float max = -Integer.MAX_VALUE;
		String category = null;
		for (Entry<String, ArrayEncodedProbBackoffLm<String>> pair : models
				.entrySet()) {
			ArrayEncodedProbBackoffLm<String> model = pair.getValue();
			float score = this.scoreSequence(testId, model, processorClass,
					false);
			if (score > max) {
				max = score;
				category = pair.getKey();
			}
		}

		return category;
	}

	public String categorizeRawSequence(String test, List<String> suiteIds,
			int order, Class<? extends ArtifactProcessor<?>> processorClass,
			boolean useWrapperSymbols) throws InstantiationException,
			IllegalAccessException {
		Map<String, ArrayEncodedProbBackoffLm<String>> models = new HashMap<String, ArrayEncodedProbBackoffLm<String>>();

		for (String suiteId : suiteIds) {
			ArrayEncodedProbBackoffLm<String> model = manager
					.computeArtifactModel(dbId, suiteId, order,
							WeblogProcessor.class);
			models.put(suiteId, model);
		}

		float max = -Integer.MAX_VALUE;
		String category = null;
		for (Entry<String, ArrayEncodedProbBackoffLm<String>> pair : models
				.entrySet()) {
			ArrayEncodedProbBackoffLm<String> model = pair.getValue();
			float score = this.scoreLine(test, model, false);
			if (score > max) {
				max = score;
				category = pair.getKey();
			}
		}

		return category;
	}

	public boolean acceptRawSequenceGivenModel(String test, String suiteId,
			int order, Class<? extends ArtifactProcessor<?>> processorClass,
			boolean useWrapperSymbols, float threshold) throws InstantiationException,
			IllegalAccessException {

		ArrayEncodedProbBackoffLm<String> model = manager
				.computeArtifactModel(dbId, suiteId, order,
						WeblogProcessor.class);

		float score = this.scoreLine(test, model, false);
		
		// Adding normalization for length of sequence
		score = score / test.split(" ").length;
		
		if (score > threshold) {
			logger.info("Score of " + score + " accepts");
			return true;
		}
		
		logger.info("Score of " + score + " rejects");
		return false;
	}

}

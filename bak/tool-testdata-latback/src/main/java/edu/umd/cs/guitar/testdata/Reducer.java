package edu.umd.cs.guitar.testdata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import net.sourceforge.cobertura.coveragedata.ProjectData;
import edu.berkeley.nlp.lm.ArrayEncodedProbBackoffLm;
import edu.berkeley.nlp.lm.ContextEncodedProbBackoffLm;
import edu.berkeley.nlp.lm.WordIndexer;
import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.testdata.hgs.CoverageReducer;
import edu.umd.cs.guitar.testdata.util.BerkeleyLMUtils;
import edu.umd.cs.guitar.testdata.util.CoberturaUtils;
import edu.umd.cs.guitar.testdata.util.GUITARUtils;

public class Reducer {

	private static Logger logger = LogManager.getLogger(Reducer.class);
	private String dbId;
	private TestDataManager manager;

	public Reducer(String host, int port, String dbId) {
		this.dbId = dbId;
		this.manager = new TestDataManager(host, "" + port);
	}

	public Reducer(String dbId) {
		this.dbId = dbId;
		this.manager = new TestDataManager();
	}

	public Reducer(String dbId, String host, int port) {
		this.dbId = dbId;
		this.manager = new TestDataManager(host, "" + port);
	}
//
//	public void reduceSuiteByNgrams(String inputSuiteId, String outputSuiteId,
//			int order) {
//		ArrayEncodedProbBackoffLm<String> inputModel = manager.getNgramModel(
//				dbId, inputSuiteId, TestDataManagerDefaults.KEY_MODEL);
//
//		List<String> tried = new ArrayList<String>();
//		ArrayEncodedProbBackoffLm<String> outputModel = null;
//		boolean check = false;
//
//		while ((outputModel == null)
//				|| (check && (BerkeleyLMUtils.areNgramsSubsumed(inputModel,
//						outputModel, order) > 0))) {
//			check = false;
//			String nextTest = manager.selectRandomTestFromSuite(dbId,
//					inputSuiteId, tried);
//			logger.debug("Adding test " + nextTest + " to reduced suite.");
//			tried.add(nextTest);
//
//			List<String> testAsSteps = GUITARUtils.getEventIdsFromTest(manager
//					.getTestCase(dbId, nextTest));
//			if ((outputModel == null)
//					|| BerkeleyLMUtils.doesSequenceAddNewNgrams(testAsSteps,
//							outputModel, order)) {
//				manager.addTestToSuite(dbId, nextTest, outputSuiteId);
//				outputModel = manager.computeModel(dbId, outputSuiteId,
//						order + 1);
//
//				check = true;
//			}
//		}
//	}
//
//	public void reduceSuiteByRandomCoverage(String inputSuiteId,
//			String outputSuiteId) throws IOException {
//		ProjectData inputCoverage = manager.getCoverageObject(dbId,
//				inputSuiteId);
//
//		List<String> tried = new ArrayList<String>();
//		ProjectData outputCoverage = null;
//		boolean check = false;
//
//		while ((outputCoverage == null)
//				|| (check && (!CoberturaUtils.doesCoverageMeetGoal(
//						inputCoverage, outputCoverage)))) {
//			check = false;
//			String nextTest = manager.selectRandomTestFromSuite(dbId,
//					inputSuiteId, tried);
//			tried.add(nextTest);
//
//			ProjectData newCoverage = manager.getCoverageObjectForTest(dbId,
//					nextTest);
//
//			if ((outputCoverage == null)
//					|| (CoberturaUtils.addedCoveredLines(outputCoverage,
//							newCoverage) > 0)) {
//				manager.addTestToSuite(dbId, nextTest, outputSuiteId);
//				outputCoverage = manager.computeCoverage(dbId, outputSuiteId);
//				check = true;
//			}
//		}
//	}
//
//	public void reduceSuiteByHGSCoverage(String inputSuiteId,
//			String outputSuiteId) {
//		// Initialize test sets, marking array
//		CoverageReducer cr = new CoverageReducer(manager, dbId, outputSuiteId);
//
//		// Initialize requirements, one per each covered line of code in the
//		// input test suite
//		List<String> tids = manager.getTestIdsInSuite(dbId, inputSuiteId);
//		for (String tid : tids) {
//			cr.processTestCase(tid);
//		}
//
//		// Initialize marking array for requirements coverage (all false)
//		Map<String, Boolean> mark = new HashMap<String, Boolean>();
//		Set<String> rids = cr.getAllRequirements();
//		for (String rid : rids) {
//			mark.put(rid, false);
//		}
//
//		// Determine max cardinality
//		int maxCard = -1;
//		for (String rid : mark.keySet()) {
//			Set<String> tsRid = cr.getTestSetForRequirement(rid);
//			if (tsRid.size() > maxCard) {
//				maxCard = tsRid.size();
//			}
//
//			if (tsRid.size() == 1) {
//				// Add all testing sets of size 1 to output suite
//				manager.addTestToSuite(dbId, cr.getTestSetForRequirement(rid)
//						.iterator().next(), outputSuiteId);
//
//				// mark as true
//				mark.put(rid, true);
//			}
//		}
//
//		int currentCard = 1;
//
//		// while cur_card != max_card
//		while (currentCard < maxCard) {
//			// Increment currentCard
//			currentCard += 1;
//
//			// while there are unmarked testing sets with cardinality ==
//			// cur_card
//			Set<String> matchingReqs = cr
//					.getRequirementsWithSetsMatchingCardinality(currentCard,
//							null);
//			Set<String> unmarked = getUnmarkedRequirements(mark, matchingReqs);
//			while (unmarked.size() > 0) {
//
//				// Select the next test case
//				List<String> candidateSet = cr.getTestsFromSetsWithCardinality(
//						currentCard, mark);
//				String nextTest = selectNextTest(candidateSet, cr, currentCard,
//						maxCard, mark);
//				manager.addTestToSuite(dbId, nextTest, outputSuiteId);
//
//				boolean mayReduce = false;
//				// mark all testing sets which contain nextTest as true
//				// Also track maxSize of any affect test set
//				int maxSize = 0;
//				for (String rid : cr.getMetRequirementsForTestCase(nextTest)) {
//					mark.put(rid, true);
//
//					if (cr.getTestSetForRequirement(rid).size() > maxSize) {
//						maxSize = cr.getTestSetForRequirement(rid).size();
//						if (maxSize == maxCard) {
//							mayReduce = true;
//						}
//					}
//				}
//
//				// Update maxCard if a test set of maxSize was covered by
//				// nextTest
//				if (mayReduce) {
//					maxCard = maxSize;
//				}
//
//				// Update unmarked set
//				// Note that matchingReqs only needs update when currentCard
//				// changes (outer loop)
//				unmarked = getUnmarkedRequirements(mark, matchingReqs);
//			}
//		}
//	}
//
//	private static Set<String> getUnmarkedRequirements(
//			Map<String, Boolean> marked, Set<String> matchingReqs) {
//		Set<String> ret = new HashSet<String>();
//		for (String rid : matchingReqs) {
//			if (!marked.get(rid)) {
//				ret.add(rid);
//			}
//		}
//
//		return ret;
//	}
//
//	private String selectNextTest(List<String> candidateSet,
//			CoverageReducer cr, int currentCard, int maxCard,
//			Map<String, Boolean> marked) {
//
//		Map<String, Integer> count = new HashMap<String, Integer>();
//
//		// Compute for all t count(t), the number of test sets of cardinality
//		// currentCard containing a test t
//		// Also compute the max count, and a set of tests which achieve the max
//		// count
//		int max = 0;
//		List<String> testList = new ArrayList<String>();
//		for (String tid : candidateSet) {
//			int cc = 0;
//			for (String rid : cr.getRequirementsWithSetsMatchingCardinality(
//					currentCard, marked)) {
//				if (cr.getTestSetForRequirement(rid).contains(tid)) {
//					cc++;
//				}
//			}
//
//			count.put(tid, cc);
//			if (cc > max) {
//				max = cc;
//				testList.clear();
//				testList.add(tid);
//			} else if (cc == max) {
//				testList.add(tid);
//			}
//		}
//
//		if (testList.size() == 1) {
//			return testList.get(0);
//		} else if (currentCard == maxCard) {
//			int random = new Random().nextInt(testList.size());
//			return testList.get(random);
//		}
//
//		return selectNextTest(testList, cr, currentCard + 1, maxCard, marked);
//	}
//
//	public void reduceSuiteByProbability(String inputSuiteId,
//			String outputSuiteId, int order, double threshold) {
//
//		// Obtain input model
//		ArrayEncodedProbBackoffLm<String> inputModel = manager.computeModel(
//				dbId, inputSuiteId, order + 1);
//
//		// Initialize list of seen tests
//		List<String> seen = new ArrayList<String>();
//
//		// Initialize output model with random test case(s)
//		String firstTest = manager.selectRandomTestFromSuite(dbId,
//				inputSuiteId, new ArrayList<String>());
//		manager.addTestToSuite(dbId, firstTest, outputSuiteId);
//		seen.add(firstTest);
//		ArrayEncodedProbBackoffLm<String> outputModel = manager.computeModel(
//				dbId, outputSuiteId, order);
//
//		// Make sure there are n-grams of the appropriate order for each model
//		if ((BerkeleyLMUtils.getFunctionalMaxOrder(inputModel) < order)
//				|| (BerkeleyLMUtils.getFunctionalMaxOrder(inputModel) < order)) {
//			logger.fatal("Models have no n-grams available at the desired order in probability-based reduction");
//			manager.clearTestSuite(dbId, outputSuiteId);
//			return;
//		}
//
//		double percentRemaining = BerkeleyLMUtils.areNgramsSubsumed(inputModel,
//				outputModel, order - 1);
//
//		// while ngrams of input model not subsumed by ngrams of output model
//		while (percentRemaining > threshold) {
//
//			// Select least probable test case
//			String nextTest = selectLeastProbableTestCase(inputSuiteId, seen,
//					outputModel);
//
//			if (nextTest == null) {
//				logger.fatal("No tests remaining, but N-grams not subsumed in reduceSuiteByProbability");
//				manager.clearTestSuite(dbId, outputSuiteId);
//				break;
//			}
//
//			// Add least probable test case(s) to output suite
//			manager.addTestToSuite(dbId, nextTest, outputSuiteId);
//			outputModel = manager.computeModel(dbId, outputSuiteId, order);
//
//			System.out.println("Selected test " + nextTest
//					+ " in probability-based reduction method");
//
//			// Add to seen list so we won't select this test again
//			seen.add(nextTest);
//
//			// Update remaining ngram count
//			percentRemaining = BerkeleyLMUtils.areNgramsSubsumed(inputModel,
//					outputModel, order - 1);
//		}
//	}
//
//	private String selectLeastProbableTestCase(String inputSuiteId,
//			List<String> seen, ArrayEncodedProbBackoffLm<String> outputModel) {
//
//		// We are working with log probabilities, so minimum of reported
//		// correlates to minimum probability
//		// See KneserNeyLmReader.getProbBackoff(...) for computation of original
//		// prob for each word
//		float least = Float.MAX_VALUE;
//		String id = null;
//
//		List<String> candidates = manager.getTestIdsInSuite(dbId, inputSuiteId);
//		candidates.removeAll(seen);
//
//		for (String testId : candidates) {
//			TestCase tc = manager.getTestCase(dbId, testId);
//			List<String> events = GUITARUtils.getEventIdsFromTest(tc);
//
//			int[] ngram = BerkeleyLMUtils.getNgramIndexArrayFromWordList(
//					outputModel.getWordIndexer(), events);
//			float prob = outputModel.getLogProb(ngram);
//			if (prob < least) {
//				least = prob;
//				id = testId;
//			}
//		}
//
//		return id;
//
//	}
//
//	public void reduceSuiteBySampling(String inputSuiteId,
//			String outputSuiteId, int size) {
//
//		List<String> tried = new ArrayList<String>();
//		while (tried.size() < size) {
//			String nextTest = manager.selectRandomTestFromSuite(dbId,
//					inputSuiteId, tried);
//			manager.addTestToSuite(dbId, nextTest, outputSuiteId);
//			tried.add(nextTest);
//		}
//	}
}
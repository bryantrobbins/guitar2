package edu.umd.cs.guitar.testdata.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import edu.berkeley.nlp.lm.ArrayEncodedProbBackoffLm;
import edu.berkeley.nlp.lm.ContextEncodedProbBackoffLm;
import edu.berkeley.nlp.lm.WordIndexer;
import edu.berkeley.nlp.lm.map.NgramMap;
import edu.berkeley.nlp.lm.map.NgramMap.Entry;
import edu.berkeley.nlp.lm.map.NgramsForOrderMapWrapper;
import edu.berkeley.nlp.lm.values.ProbBackoffPair;
import edu.umd.cs.guitar.model.data.GUIMap;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.TestCase;

public class BerkeleyLMUtils {

	private static Logger logger = LogManager.getLogger(BerkeleyLMUtils.class);

	public static int max(int a, int b) {
		if (a > b) {
			return a;
		}

		return b;
	}

	public static int min(int a, int b) {
		if (a < b) {
			return a;
		}

		return b;
	}

	public static int getFunctionalMaxOrder(
			ArrayEncodedProbBackoffLm<String> model) {
		int functionalMax = 0;
		int maxOrder = model.getLmOrder();

		for (int i = 0; i < maxOrder; i++) {
			if (((model.getLmOrder() > i) && (model.getNgramMap().getNumNgrams(
					i) == 0))) {
				return functionalMax + 1;
			}

			functionalMax = i;
		}

		return functionalMax + 1;

	}

	public static int getFunctionalMaxOrder(
			ArrayEncodedProbBackoffLm<String> inputModel,
			ArrayEncodedProbBackoffLm<String> outputModel) {
		int functionalMax = 0;
		int maxOrder = max(inputModel.getLmOrder(), outputModel.getLmOrder());

		for (int i = 0; i < maxOrder; i++) {
			if (((inputModel.getLmOrder() > i) && (inputModel.getNgramMap()
					.getNumNgrams(i) == 0))
					|| ((outputModel.getLmOrder() > i) && (outputModel
							.getNgramMap().getNumNgrams(i) == 0))) {
				return functionalMax + 1;
			}

			functionalMax = i;
		}

		return functionalMax + 1;
	}

	public static double areNgramsSubsumed(
			ArrayEncodedProbBackoffLm<String> inputModel,
			ArrayEncodedProbBackoffLm<String> outputModel, int order) {
		int falseCount = 0;
		int total = 0;
		Iterator<Entry<ProbBackoffPair>> iterGramsA = inputModel.getNgramMap()
				.getNgramsForOrder(order).iterator();

		while (iterGramsA.hasNext()) {
			total++;
			Entry<ProbBackoffPair> ngram = iterGramsA.next();
			if (!doesModelContainNgramFromWordIndexer(outputModel, order,
					inputModel.getWordIndexer(), ngram.key)) {
				falseCount++;
			}
		}

		logger.debug(falseCount + " remaining n-grams out of " + total
				+ " total in areNgramsSubsumed");

		return (falseCount * 1.0) / total;
	}

	public static int[] getTrimmedNgram(int[] ngram, int startPos, int endPos) {
		int[] ret = new int[endPos - startPos];
		int retPos = 0;

		for (int i = startPos; i < endPos; i++) {
			ret[retPos] = ngram[i];
		}

		return ret;
	}

	public static boolean doesModelContainNgramFromWordIndexer(
			ArrayEncodedProbBackoffLm<String> model, int order,
			WordIndexer<String> ix, int[] ngram) {
		Iterator<Entry<ProbBackoffPair>> iter = model.getNgramMap()
				.getNgramsForOrder(order).iterator();

		while (iter.hasNext()) {
			int[] ngramMine = iter.next().key;
			if (areNgramsEquivalent(ngram, ix, ngramMine,
					model.getWordIndexer())) {
				return true;
			}
		}

		return false;
	}

	public static boolean areNgramsEquivalent(int[] a, WordIndexer<String> ixA,
			int[] b, WordIndexer<String> ixB) {
		List<String> gramA = WordIndexer.StaticMethods.toList(ixA, a);
		List<String> gramB = WordIndexer.StaticMethods.toList(ixB, b);

		if (gramA == null || gramB == null) {
			return false;
		}

		if (gramA.size() != gramB.size()) {
			return false;
		}

		for (int i = 0; i < gramA.size(); i++) {
			if (!gramA.get(i).equals(gramB.get(i))) {
				return false;
			}
		}

		return true;

	}

	public static void printAllNgrams(NgramMap inputGrams, WordIndexer ix) {

		for (int i = 0; i < inputGrams.getMaxNgramOrder(); i++) {
			System.out.println("Printing " + i + "-grams");
			printNgramsForOrder(inputGrams, ix, i);
		}
	}

	public static void printNgramsForOrder(NgramMap inputGrams, WordIndexer ix,
			int order) {

		Iterator<Entry<String>> iterGramsA = inputGrams
				.getNgramsForOrder(order).iterator();

		while (iterGramsA.hasNext()) {
			System.out.println("START");
			Entry<String> ngram = iterGramsA.next();
			for (int j = 0; j < ngram.key.length; j++) {
				System.out.println(ix.getWord(ngram.key[j]));

			}
			System.out.println("END");
		}
	}

	public static List<String> getNgramsForOrder(
			ArrayEncodedProbBackoffLm<String> model, int order) {

		Iterator<Entry<ProbBackoffPair>> iterGramsA = model.getNgramMap()
				.getNgramsForOrder(order).iterator();

		List<String> ngramsAsStrings = new ArrayList<String>();

		while (iterGramsA.hasNext()) {
			Entry<ProbBackoffPair> ngram = iterGramsA.next();
			List<String> ngramAsWords = new ArrayList<String>();
			for (int j = 0; j < ngram.key.length; j++) {
				ngramAsWords.add(model.getWordIndexer().getWord(ngram.key[j]));
			}
			
			String ngramAsString = "";
			for(int i = 0; i<ngramAsWords.size(); i++){
				ngramAsString += ngramAsWords.get(i);
				if(i != ngramAsWords.size()-1){
					ngramAsString += " ";
				}
			}
			
			ngramsAsStrings.add(ngramAsString);
		}

		return ngramsAsStrings;
	}

	public static boolean doesSequenceAddNewNgrams(List<String> seq,
			ArrayEncodedProbBackoffLm<String> outputModel, int order) {

		int[] ngram = new int[seq.size()];

		for (int i = 0; i < seq.size(); i++) {
			String step = seq.get(i);
			WordIndexer<String> wi = outputModel.getWordIndexer();
			ngram[i] = wi.getIndexPossiblyUnk(step);

			// This event may be new, if so return
			if (wi.getWord(ngram[i]).equals(wi.getUnkSymbol())) {
				return true;
			}
		}

		// If we have reached this point, the n-gram does not contribute
		// any new events, but may possibly contribute new n-grams of a longer
		// length

		for (int startPos = 0; startPos < ngram.length - 1; startPos++) {
			int maxPos = min(ngram.length, startPos + order);
			for (int endPos = startPos + 1; endPos <= maxPos; endPos++) {
				if (!outputModel.getNgramMap()
						.contains(ngram, startPos, endPos)) {
					return true;
				}
			}
		}

		return false;
	}

	public static int[] getNgramIndexArrayFromWordList(WordIndexer ix,
			List<String> words) {
		int[] ret = new int[words.size()];
		int retPos = 0;
		for (String word : words) {
			ret[retPos] = ix.getIndexPossiblyUnk(word);
			retPos++;
		}

		return ret;
	}

}

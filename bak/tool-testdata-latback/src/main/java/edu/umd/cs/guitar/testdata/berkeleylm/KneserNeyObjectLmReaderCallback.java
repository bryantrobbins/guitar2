package edu.umd.cs.guitar.testdata.berkeleylm;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import edu.berkeley.nlp.lm.ConfigOptions;
import edu.berkeley.nlp.lm.ContextEncodedNgramLanguageModel.LmContextInfo;
import edu.berkeley.nlp.lm.WordIndexer;
import edu.berkeley.nlp.lm.io.ArpaLmReaderCallback;
import edu.berkeley.nlp.lm.map.HashNgramMap;
import edu.berkeley.nlp.lm.map.NgramMap.Entry;
import edu.berkeley.nlp.lm.util.Logger;
import edu.berkeley.nlp.lm.util.LongRef;
import edu.berkeley.nlp.lm.util.StrUtils;
import edu.berkeley.nlp.lm.values.KneserNeyCountValueContainer;
import edu.berkeley.nlp.lm.values.KneserNeyCountValueContainer.KneserNeyCounts;
import edu.berkeley.nlp.lm.values.ProbBackoffPair;

/**
 * Class for producing a Kneser-Ney language model in ARPA format from raw text.
 * 
 * @author adampauls
 * 
 * @param <W>
 */
public class KneserNeyObjectLmReaderCallback<W> implements ArpaLmReaderCallback<ProbBackoffPair>
{
	private WordIndexer<W> wordIndexer;
	private ArpaObject object;
	
	public KneserNeyObjectLmReaderCallback(ArpaObject ao, final WordIndexer<W> wordIndexer) {
		this.wordIndexer = wordIndexer;
		this.object = ao;
		this.object.setLines(new ArrayList<String>());
	}

	@Override
	public void handleNgramOrderFinished(int order) {
		this.object.addLine("");
	}

	@Override
	public void handleNgramOrderStarted(int order) {
		this.object.addLine("\\" + (order) + "-grams:");
	}

	@Override
	public void call(int[] ngram, int startPos, int endPos, ProbBackoffPair value, String words) {
		final String line = StrUtils.join(WordIndexer.StaticMethods.toList(wordIndexer, ngram, startPos, endPos));
		final boolean endsWithEndSym = ngram[ngram.length - 1] == wordIndexer.getIndexPossiblyUnk(wordIndexer.getEndSymbol());
		if (endsWithEndSym || value.backoff == 0.0f)
			this.object.addLine(String.format(Locale.US, "%f\t%s\n", value.prob, line));
		else {
			this.object.addLine(String.format(Locale.US, "%f\t%s\t%f\n", value.prob, line, value.backoff));
		}
	}

	@Override
	public void cleanup() {
		this.object.addLine("\\end\\");
	}

	@Override
	public void initWithLengths(List<Long> numNGrams) {
		this.object.addLine("");
		this.object.addLine("\\data\\");
		for (int ngramOrder = 0; ngramOrder < numNGrams.size(); ++ngramOrder) {
			final long numNgrams = numNGrams.get(ngramOrder);
			this.object.addLine("ngram " + (ngramOrder + 1) + "=" + numNgrams);
		}
		this.object.addLine("");
	}

}
package edu.berkeley.nlp.lm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.berkeley.nlp.lm.collections.Counter;

/**
 * Default implementation of all NGramLanguageModel functionality except
 * {@link #getLogProb(int[], int, int)}.
 * 
 * 
 * 
 * @author adampauls
 * 
 * @param <W>
 */
public abstract class AbstractArrayEncodedNgramLanguageModel<W> extends AbstractNgramLanguageModel<W> implements ArrayEncodedNgramLanguageModel<W>,
	Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AbstractArrayEncodedNgramLanguageModel(final int lmOrder, final WordIndexer<W> wordIndexer, final float oovWordLogProb) {
		super(lmOrder, wordIndexer, oovWordLogProb);
	}

	@Override
	public float scoreSentence(final List<W> sentence) {
		return DefaultImplementations.scoreSentence(sentence, this);
	}

	@Override
	public float getLogProb(final List<W> phrase) {
		return DefaultImplementations.getLogProb(phrase, this);
	}

	@Override
	public float getLogProb(final int[] ngram) {
		return DefaultImplementations.getLogProb(ngram, this);
	}

	@Override
	public abstract float getLogProb(final int[] ngram, int startPos, int endPos);
	
	


}

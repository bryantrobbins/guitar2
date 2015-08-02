package edu.umd.cs.guitar.testdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.junit.Assert;

import edu.berkeley.nlp.lm.ConfigOptions;
import edu.berkeley.nlp.lm.ContextEncodedProbBackoffLm;
import edu.berkeley.nlp.lm.StringWordIndexer;
import edu.berkeley.nlp.lm.collections.Iterators;
import edu.berkeley.nlp.lm.io.ArpaLmReader;
import edu.berkeley.nlp.lm.io.KneserNeyLmReaderCallback;
import edu.berkeley.nlp.lm.io.LmReaders;
import edu.berkeley.nlp.lm.io.TextReader;
import edu.berkeley.nlp.lm.map.NgramMap;
import edu.berkeley.nlp.lm.map.NgramMap.Entry;
import edu.berkeley.nlp.lm.values.ProbBackoffPair;
import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.testdata.berkeleylm.ArpaObject;
import edu.umd.cs.guitar.testdata.berkeleylm.ArpaObjectLmReader;
import edu.umd.cs.guitar.testdata.berkeleylm.KneserNeyObjectLmReaderCallback;
import edu.umd.cs.guitar.testdata.guitar.TestCaseIterator;
import edu.umd.cs.guitar.testdata.util.GUITARUtils;

public class ArpaObjectLmReaderTests {

	private ArpaObject getArpaObjectFromResource(String urlString) throws IOException{
		ArpaObject ao = new ArpaObject();
		
		// This idiom based on 
		// http://stackoverflow.com/questions/5868369/how-to-read-a-large-text-file-line-by-line-in-java
		BufferedReader br = null;
		try{
			br = new BufferedReader(new InputStreamReader(ArpaObjectLmReaderTests.class.getResourceAsStream(urlString)));
			String line;
			while ((line = br.readLine()) != null) {
			   ao.addLine(line);
			}	
		} finally{
			br.close();
		}
		
		return ao;
	}
	
	private ArpaObject getArpaObjectFromDuplicateTests() throws URISyntaxException{
		URL arpaUrl = ArpaObjectLmReader.class.getResource("/random-1.tst");
		String path = arpaUrl.toURI().getPath();
		TestCase tc = GUITARUtils.getTestCaseFromFile(path);
		List<TestCase> tests = new ArrayList<TestCase>();
		for(int i=0; i<1; i++){
			tests.add(tc);
		}
		
		// Set up WordIndexer
		final StringWordIndexer wordIndexer = new StringWordIndexer();
		wordIndexer.setStartSymbol(ArpaLmReader.START_SYMBOL);
		wordIndexer.setEndSymbol(ArpaLmReader.END_SYMBOL);
		wordIndexer.setUnkSymbol(ArpaLmReader.UNK_SYMBOL);

		// Set up config options
		ConfigOptions opts = new ConfigOptions();
		
		// Set up iterable over TestCases
		final Iterable<String> events = Iterators.able(new TestCaseIterator(tests)); 
		
		// Set up reader
		final TextReader<String> reader = new TextReader<String>(events, wordIndexer);
		
		// Set up callback handler
		KneserNeyLmReaderCallback<String> kneserNeyReader = new KneserNeyLmReaderCallback<String>(wordIndexer, 2, opts);
		
		// Construct model (actually counts N-grams, etc.)
		reader.parse(kneserNeyReader);

		// Convert model to ARPA format
		// Instead of storing to file, I'm storing to an object to make JSON conversion easier
		// The object has toJson() and toArpa() methods :)
		ArpaObject arpa = new ArpaObject();
		arpa.setMaxOrder(2);
		kneserNeyReader.parse(new KneserNeyObjectLmReaderCallback<String>(arpa, wordIndexer));
		
		return arpa;
	}
	
	@Test
	public void testCompareModels() throws IOException, URISyntaxException{
		// Construct model using ArpaObject
		ArpaObject ao = getArpaObjectFromResource("/kneserNeyFromText.arpa");
		ao.setMaxOrder(5);
		ArpaObjectLmReader<String> arpaReader = new ArpaObjectLmReader<String>(ao, new StringWordIndexer());
		ContextEncodedProbBackoffLm<String> actual = LmReaders.readContextEncodedLmFromArpa(arpaReader, new StringWordIndexer(), new ConfigOptions());
		NgramMap<ProbBackoffPair> actualGrams = actual.getNgramMap();
		
		// Construct model using LmReaders
		// This idiom based on:
		// http://stackoverflow.com/questions/676097/java-resource-as-file
		URL arpaUrl = ArpaObjectLmReader.class.getResource("/kneserNeyFromText.arpa");
		String path = arpaUrl.toURI().getPath();
		ContextEncodedProbBackoffLm<String> expected = LmReaders.readContextEncodedLmFromArpa(path);
		NgramMap<ProbBackoffPair> expectedGrams = expected.getNgramMap();
		
		long countFromCounts = 0;
		// Assert that N-gram counts are the same
		for(int i=0; i<5; i++){
			countFromCounts +=  expectedGrams.getNumNgrams(i);
			System.out.println("Expected=" + expectedGrams.getNumNgrams(i) + ", Actual=" + actualGrams.getNumNgrams(i));
			Assert.assertEquals(expectedGrams.getNumNgrams(i), actualGrams.getNumNgrams(i));
		}
		
		// Assert that probabilities are the same
		long countFromMaps = 0;
		for(int i=0; i<5; i++){
			Iterator<Entry<ProbBackoffPair>> actualForOrder = actualGrams.getNgramsForOrder(i).iterator();
			Iterator<Entry<ProbBackoffPair>> expectedForOrder = expectedGrams.getNgramsForOrder(i).iterator();
			while(actualForOrder.hasNext()){
				countFromMaps += 1;
				Entry<ProbBackoffPair> actualPair = actualForOrder.next();
				Entry<ProbBackoffPair> expectedPair = expectedForOrder.next();
				Assert.assertEquals(expectedPair.value.prob, actualPair.value.prob, 0.0);
				Assert.assertEquals(expectedPair.value.backoff, actualPair.value.backoff, 0.0);
			}
		}
		System.out.println("Expected=" + countFromCounts + ", Actual=" + countFromMaps);
		Assert.assertEquals(countFromCounts, countFromMaps);
	}
	
	@Test
	public void checkNgramsFromSimple() throws URISyntaxException{
		ArpaObject ao = getArpaObjectFromDuplicateTests();
		for(String line : ao.getLines()){
			System.out.println(line);
		}
	}
}

package edu.umd.cs.guitar.testdata;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Assert;

import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.testdata.guitar.TestCaseIterator;

import org.junit.Test;

public class TestCaseIteratorTests {

	private TestCaseIterator initIterator(){
		TestDataManager tdm = new TestDataManager("localhost", 37017);

		List<TestCase> tests = new ArrayList<TestCase>();
		
		String dbId = tdm.createDb();
		System.out.println("Using DB " + dbId);

		for (int i = 1; i <= 10; i++) {
			String testId = "random-" + i;
			tdm.addTestCaseFromFiles(dbId, testId + "-suite", testId,
					"src/test/resources/random-" + i + ".tst",
					"src/test/resources/random-" + i + ".map",
					"src/test/resources/random-" + i + ".gui",
					"src/test/resources/random-" + i + ".ser");
			tests.add(tdm.getTestCase(dbId, testId));
		}
		
		return new TestCaseIterator(tests);
	}
	
	@Test
	public void testForward(){
		TestCaseIterator tci = initIterator();
		
		for(int i=0; i<10; i++){
			Assert.assertTrue(tci.hasNext());
			String tc = tci.next();
			Assert.assertEquals(20, tc.split(" ").length);
			System.out.println(i + ":" + tc);
		}
		
		Assert.assertFalse(tci.hasNext());
		boolean caught = false;
		
		try{
			tci.next();			
		} catch (NoSuchElementException nsee){
			caught = true;
		}
		
		Assert.assertTrue(caught);
	}
	
}

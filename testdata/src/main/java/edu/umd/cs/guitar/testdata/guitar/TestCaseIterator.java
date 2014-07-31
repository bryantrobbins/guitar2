package edu.umd.cs.guitar.testdata.guitar;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import edu.umd.cs.guitar.model.data.StepType;
import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.testdata.TestDataManager;

public class TestCaseIterator implements Iterator<String> {
	
	private static Logger logger = LogManager.getLogger(TestCaseIterator.class);
	
	private List<TestCase> tests;
	private int currentTest;

	public static String getStringForTestCase(TestCase test){
		String ret = "";
		for(int i=0; i<test.getStep().size(); i++){
			ret += test.getStep().get(i).getEventId();
			
			if(i != test.getStep().size()-1){
				ret += " ";
			}
		}
		
		return ret;
	}
	
	public TestCaseIterator(List<TestCase> tests){
		this.tests = tests;
		this.currentTest = -1;
	}
	
	@Override
	public boolean hasNext() {
		return tests.size() > (currentTest+1);
	}

	@Override
	public String next() {
		if(hasNext()){
			currentTest++;
			logger.debug("Test " + currentTest);
			return getStringForTestCase(tests.get(currentTest));
		}
		
		throw new NoSuchElementException("No more elements in TestCaseIterator");
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException("Remove operation not supported in TestCaseIterator");
	}

}
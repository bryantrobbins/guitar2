/*  
 *  Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland. Names of owners of this group may
 *  be obtained by sending an e-mail to atif@cs.umd.edu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 *  documentation files (the "Software"), to deal in the Software without restriction, including without 
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *  the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
 *  conditions:
 * 
 *  The above copyright notice and this permission notice shall be included in all copies or substantial 
 *  portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *  LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *  EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
package edu.umd.cs.guitar.smut.testcase;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.data.StepType;
import edu.umd.cs.guitar.model.data.TestCase;

/**
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 * 
 */
public class TestCaseGeneratorTest {
	String sInOracleDir = "/media/Data/Ore_no_documents/Research/aut/RadioButton/ProjectSimplified-testcases/oracles";

	String sInTestCaseFile = "test";
	String sInTestCase = sInOracleDir + File.separator + sInTestCaseFile
			+ ".orc";

	String sOutTestCaseDir = "/media/Data/Ore_no_documents/Research/aut/RadioButton/ProjectSimplified-testcases/testcase-incremental/"
			+ sInTestCaseFile+File.separator;

	TestCase guiState;
	TestCaseGenerator generator;
	private int length;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		generator = new TestCaseGenerator();
		length = 2;
		guiState = (TestCase) IO.readObjFromFile(sInTestCase, TestCase.class);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGenerator() {
		System.out.println("testGenerator....");
		List<TestCase> testsuite = generator.genTestSuite(guiState, length);
		int i=0;
		for (TestCase testcase : testsuite) {
			String testcasePath = sOutTestCaseDir + i + ".tst";
			System.out.println("Writting to ..."  +testcasePath);
			IO.writeObjToFile(testcase, testcasePath );
			i++;
		}

		// printTestSuite(testsuite);

	}

	private void printTestSuite(List<TestCase> testsuite) {

		for (TestCase testcase : testsuite) {
			System.out.println("--------------");
			for (StepType step : testcase.getStep()) {
				System.out.print(step.getEventId() + ", ");
			}
			System.out.println();
		}
		System.out.println("Total test case: " + testsuite.size());

	}

}

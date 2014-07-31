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
package edu.umd.cs.guitar.smut.converter;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sun.security.timestamp.TSRequest;

import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.EventEffectType;
import edu.umd.cs.guitar.model.data.EventMapElementType;
import edu.umd.cs.guitar.model.data.EventSetType;
import edu.umd.cs.guitar.model.data.EventStateType;
import edu.umd.cs.guitar.model.data.EventTrace;
import edu.umd.cs.guitar.model.data.GUIMap;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;

/**
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 * 
 */
public class OracleAnalyzerTest {

	GUIStructure gs;
	
	String inHome="/media/Data/Ore_no_documents/Research/aut/RadioButton/ProjectSimplified-testcases/";
	String inGUIStructure = inHome +"ProjectSimplified.GUI";
	OracleAnalyzer analyzer;
	String inOracleDir = "/media/Data/Ore_no_documents/Research/aut/RadioButton/ProjectSimplified-testcases/oracles";
	String inTestCase = inOracleDir + File.separator + "t_e3152277056_e215654788.orc";
	String sInMap =inHome +"ProjectSimplified.MAP"; 
	
		
	// OUTPUT	
	String outEventTraceDir = "/media/Data/Ore_no_documents/Research/aut/RadioButton/ProjectSimplified-testcases/traces";
	String outEventTraceFile = outEventTraceDir + File.separator + "t_e3152277056_e215654788.et";
	String sOutMap =inHome +"ProjectSimplified.out.MAP";
	
	TestCase testcase;

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
		gs = (GUIStructure) IO.readObjFromFile(inGUIStructure,
				GUIStructure.class);
		testcase = (TestCase) IO.readObjFromFile(inTestCase, TestCase.class);
		analyzer = new OracleAnalyzer();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	// @Test
	public void testGetAvailableEventsWithEventMap() {
//		List<ComponentType> lComponent = analyzer.getComponentWithEvent(gs);
//		printComponentList(lComponent);

	}

	@Test
	public void testUpdateMap(){
		System.out.println("testUpdateMap");
		GUIMap inMap = (GUIMap) IO.readObjFromFile(this.sInMap, GUIMap.class);
		GUIMap outMap = analyzer.updateMap(inMap,gs);
		IO.writeObjToFile(outMap, this.sOutMap);
		System.out.println("DONE");
	}
	/**
	 * 
	 */
	public void testGetEventTrace(){
		System.out.println("testGetEventTrace");
		EventTrace trace = analyzer.getEventTrace(testcase);
		IO.writeObjToFile(trace, outEventTraceFile);
	}
	
	
	
	/**
	 * 
	 */
	public void testGetAvailableEventTestSuite() {

		System.out.println("testGetAvailableEventTestSuite");
		File orcDir = new File(inOracleDir);
		File[] orcFiles = orcDir.listFiles();

		System.out.println("Total oracles: " + orcFiles.length);
		if (orcFiles != null) {
			for (int i = 0; i < orcFiles.length; i++) {
				String sFilePath = orcFiles[i].getAbsolutePath();
				testGetAvailableEventTestCase(sFilePath);
			}
		}

		List<ComponentType> lComponent = analyzer.getWidgetWithEvent(testcase);

	}

	// @Test
	public void testGetAvailableEventTestCase(String sFilePath) {

		System.out.println("-------------------");
		System.out.println("File: " + sFilePath);
		try {

			testcase = (TestCase) IO.readObjFromFile(sFilePath, TestCase.class);
			List<ComponentType> lComponent = analyzer
					.getWidgetWithEvent(testcase);
			printComponentList(lComponent);
			System.out.println("TOTAL: " + lComponent.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void printList(Set<String> events) {

		for (String event : events) {
			System.out.println(event);
		}
	}

	private void printComponentList(List<ComponentType> lComponent) {
		for (ComponentType component : lComponent) {
			ComponentTypeWrapper wComponent = new ComponentTypeWrapper(
					component);
			String sEvent = wComponent
					.getFirstValueByName(GUITARConstants.ID_TAG_NAME);
			System.out.println(sEvent);

		}

	}

}

package edu.umd.cs.guitar.smut.converter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.umd.cs.guitar.graph.GUIMapCreator;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.data.GUIMap;
import edu.umd.cs.guitar.model.data.TestCase;

public class GUIMapCreatorTest4 {

	GUIMapCreator creator;
	GUIMap map;
	String sGUIMap = "/media/Data/Ore_no_documents/Research/aut/RadioButton/ProjectSimplified-testcases/ProjectSimplified.MAP";

	TestCase oracle;
	String sOracle = "/media/Data/Ore_no_documents/Research/aut/RadioButton/ProjectSimplified-testcases/oracles/test.orc";

	@Before
	public void setUp() {
		map = (GUIMap) IO.readObjFromFile(sGUIMap, GUIMap.class);
		oracle = (TestCase) IO.readObjFromFile(sOracle, TestCase.class);
		creator = new GUIMapCreator();
	}

	@Test
	public void testGetGUIMapFromOracle() {
		GUIMap newMap = creator.getGUIMapFromOracle(oracle, map);
		System.out.println(newMap);
	}

	@After
	public void tearDown() {
		System.out.println("@After - tearDown");
	}

}

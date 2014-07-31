package edu.umd.cs.guitar.testdata.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import edu.umd.cs.guitar.model.data.GUIMap;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.StepType;
import edu.umd.cs.guitar.model.data.TestCase;

public class GUITARUtils {

	private static Logger logger = LogManager.getLogger(GUITARUtils.class);

	public static TestCase getTestCaseFromFile(String filename) {

		TestCase theTest = null;

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(TestCase.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			theTest = (TestCase) jaxbUnmarshaller.unmarshal(new File(filename));
		} catch (JAXBException je) {
			logger.error("Cannot unmarshall GUITAR test case from file "
					+ filename, je);
		}

		return theTest;
	}

	public static GUIMap getMapFromFile(String filename) {

		GUIMap theMap = null;

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(GUIMap.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			theMap = (GUIMap) jaxbUnmarshaller.unmarshal(new File(filename));
		} catch (JAXBException je) {
			logger.error("Cannot unmarshall GUITAR map from file " + filename,
					je);
		}

		return theMap;
	}

	public static GUIStructure getGuiFromFile(String filename) {

		GUIStructure theGui = null;

		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance(GUIStructure.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			theGui = (GUIStructure) jaxbUnmarshaller.unmarshal(new File(
					filename));
		} catch (JAXBException je) {
			logger.error("Cannot unmarshall GUITAR map from file " + filename,
					je);
		}

		return theGui;
	}
	
	public static List<String> getEventIdsFromTest(TestCase test){
		List<String> ret = new ArrayList<String>();
		
		for(StepType step : test.getStep()){
			ret.add(step.getEventId());
		}
		
		return ret;
	}

}

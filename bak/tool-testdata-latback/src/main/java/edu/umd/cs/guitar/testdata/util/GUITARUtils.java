package edu.umd.cs.guitar.testdata.util;

import edu.umd.cs.guitar.model.data.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    public static EFG getEfgFromFile(String filename) {

        EFG theEfg = null;

        try {
            JAXBContext jaxbContext = JAXBContext
                    .newInstance(EFG.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            theEfg = (EFG) jaxbUnmarshaller.unmarshal(new File(
                    filename));
        } catch (JAXBException je) {
            logger.error("Cannot unmarshall GUITAR EFG from file " + filename,
                    je);
        }

        return theEfg;
    }


    public static List<String> getEventIdsFromTest(TestCase test) {
        List<String> ret = new ArrayList<String>();

        for (StepType step : test.getStep()) {
            ret.add(step.getEventId());
        }

        return ret;
    }

}

package edu.umd.cs.guitar.util;

import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.GUIMap;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.StepType;
import edu.umd.cs.guitar.model.data.TestCase;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A set of utility methods for working with GUITAR objects.
 * <p/>
 * Created by bryan.
 */
public final class GUITARUtils {

    /**
     * Hiding the default public constructor.
     */
    private GUITARUtils() {

    }

    /**
     * A log4j logger.
     */
    private static Logger logger = LogManager.getLogger(GUITARUtils.class);

    /**
     * Read a testcase object from a file.
     *
     * @param filename the path to the file.
     * @return the TestCase object
     */
    public static TestCase getTestCaseFromFile(final String filename) {

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

    /**
     * Read a GUIMap from a file.
     *
     * @param filename the path to the file
     * @return the GUIMap object
     */
    public static GUIMap getMapFromFile(final String filename) {

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

    /**
     * Read a GUIStructure from a file.
     *
     * @param filename the path to the file
     * @return the GUIStructure object
     */
    public static GUIStructure getGuiFromFile(final String filename) {

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

    /**
     * Read an EFG from a file.
     *
     * @param filename the path to the file
     * @return the EFG object
     */
    public static EFG getEfgFromFile(final String filename) {

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


    /**
     * Return the events from a GUITAR test case, in order.
     *
     * @param test the TestCase object
     * @return an ordered list of events
     */
    public static List<String> getEventIdsFromTest(final TestCase test) {
        List<String> ret = new ArrayList<String>();

        for (StepType step : test.getStep()) {
            ret.add(step.getEventId());
        }

        return ret;
    }

}

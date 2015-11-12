package edu.umd.cs.guitar.processors.guitar;

import edu.umd.cs.guitar.artifacts.ArtifactCategory;
import edu.umd.cs.guitar.artifacts.GsonFileProcessor;
import edu.umd.cs.guitar.main.TestDataManager;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.processors.applog.TextObject;
import edu.umd.cs.guitar.processors.features.FeaturesObject;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A processor for generic text files, including log files
 * <p/>
 * Created by bryan on 4/5/14.
 */
public class FeaturesProcessor extends GsonFileProcessor<FeaturesObject> {

    /**
     * The key to use for the test ID in options.
     */
    public static final String TEST_ID_OPTION = "testId";
    /**
     * The key to use for the test ID in options.
     */
    public static final String SUITE_ID_OPTION = "suiteId";
    /**
     * The key to use for the test ID in options.
     */
    public static final String TRIM_OPTION = "trim";
    /**
     * Log4j logger.
     */
    private static Logger logger = LogManager.getLogger(FeaturesProcessor.class);

    /**
     * A TestcaseProcessor instance.
     */
    private static TestcaseProcessor tcProc = new TestcaseProcessor();
    /**
     * A GUIProcessor instance.
     */
    private GUIProcessor guiProc;
    /**
     * A EFGProcessor instance.
     */
    private EFGProcessor efgProc;
    /**
     * A TestcaseProcessor instance.
     */
    private static LogProcessor logProc = new LogProcessor();
    /**
     * The manager instance to use for fetching test input artifacts.
     */
    private TestDataManager manager;

    /**
     * The value of N to use when extracting N-grams from the test case.
     */

    private int n;

    /**
     * Simple constructor passing Gson serializable FeatureObject to superclass.
     *
     * @param managerInstance the manager instance to use for fetching test input artifacts
     * @param maxN            the max value N to use in constructing N-gram features (0 for none)
     */
    public FeaturesProcessor(final TestDataManager managerInstance, final int maxN) {
        super(FeaturesObject.class);
        this.manager = managerInstance;
        this.n = maxN;

        // Initialize db-sensitive processors
        guiProc = new GUIProcessor(manager.getDb());
        efgProc = new EFGProcessor(manager.getDb());
    }

    @Override
    public FeaturesObject objectFromOptions(final Map<String, String> options) {

        String testId = options.get(TEST_ID_OPTION);
        String suiteId = options.get(SUITE_ID_OPTION);
        boolean trim = false;
        String trimString = options.get(TRIM_OPTION);
        if (trimString != null) {
            trim = trimString.toLowerCase().equals("true");
        }

        // Get artifacts
        TestCase testCase = (TestCase) manager.getArtifactByCategoryAndOwnerId(ArtifactCategory.TEST_INPUT,
                testId, tcProc);
        TextObject testLog = (TextObject) manager.getArtifactByCategoryAndOwnerId(ArtifactCategory.TEST_OUTPUT,
                testId, logProc);
        GUIStructure gui = (GUIStructure) manager.getArtifactByCategoryAndOwnerId(ArtifactCategory.SUITE_INPUT,
                suiteId, guiProc);
        EFG efg = (EFG) manager.getArtifactByCategoryAndOwnerId(ArtifactCategory.SUITE_INPUT,
                suiteId, efgProc);

        if (testLog == null) {
            return FeaturesObject.getFeaturesFromTestCase(testCase, n);
        } else {
            return FeaturesObject.getFeaturesFromTestCase(testCase, testLog, gui, efg, n, trim);
        }
    }

    @Override
    public String getKey() {
        return "testCaseFeatures_n_" + n;
    }

    @Override
    public Iterator<String> getIterator(final List<FeaturesObject> objectList) {
        return null;
    }

}

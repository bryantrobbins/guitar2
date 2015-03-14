package edu.umd.cs.guitar.processors.guitar;

import edu.umd.cs.guitar.artifacts.ArtifactCategory;
import edu.umd.cs.guitar.artifacts.GsonFileProcessor;
import edu.umd.cs.guitar.main.TestDataManager;
import edu.umd.cs.guitar.model.data.TestCase;
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
    private static final String TEST_ID_OPTION = "testId";
    /**
     * Log4j logger.
     */
    private static Logger logger = LogManager.getLogger(FeaturesProcessor.class);

    /**
     * A TestcaseProcessor instance.
     */
    private static TestcaseProcessor tcProc = new TestcaseProcessor();
    /**
     * The manager instance to use for fetching test input artifacts.
     */
    private TestDataManager manager;

    /**
     * Simple constructor passing Gson serializable FeatureObject to superclass.
     *
     * @param managerInstance the manager instance to use for fetching test input artifacts
     */
    public FeaturesProcessor(final TestDataManager managerInstance) {
        super(FeaturesObject.class);
        this.manager = managerInstance;
    }

    @Override
    public FeaturesObject objectFromOptions(final Map<String, String> options) {

        String testId = options.get(TEST_ID_OPTION);
        TestCase testCase = (TestCase) manager.getArtifactByCategoryAndOwnerId(ArtifactCategory.TEST_INPUT,
                testId, tcProc);
        return FeaturesObject.getFeaturesFromTestCase(testCase);
    }

    @Override
    public String getKey() {
        return "testCaseFeatures";
    }

    @Override
    public Iterator<String> getIterator(final List<FeaturesObject> objectList) {
        return null;
    }

}

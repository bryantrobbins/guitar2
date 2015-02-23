package edu.umd.cs.guitar.processors.guitar;

import edu.umd.cs.guitar.artifacts.GsonFileProcessor;
import edu.umd.cs.guitar.model.data.StepType;
import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.util.GUITARUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class processes GUITAR TestCase objects using the GsonFileProcessor
 * base class.
 * <p/>
 * Created by bryan on 4/5/14.
 */
public class TestcaseProcessor extends GsonFileProcessor<TestCase> {

    /**
     * Log4j logger.
     */
    private static Logger logger = LogManager.getLogger(TestcaseProcessor
            .class);

    /**
     * Default constructor passes in TestCase object,
     * which is Gson serializable.
     */
    public TestcaseProcessor() {
        super(TestCase.class);
    }

    @Override
    public TestCase objectFromOptions(final Map<String, String> options) {
        return GUITARUtils.getTestCaseFromFile(options.get(FILE_PATH_OPTION));
    }

    @Override
    public String getKey() {
        return "testSteps";
    }

    @Override
    public Iterator<String> getIterator(final List<TestCase> objectList) {
        return null;
    }

    /**
     * Concatenate two test cases into a new test case.
     *
     * @param a First test case
     * @param b Second test case
     * @return Combined test case
     */
    public static TestCase concat(final TestCase a, final TestCase b) {
        TestCase ret = new TestCase();

        // First one
        for (StepType step : a.getStep()) {
            ret.getStep().add(step);
        }

        // Second one
        for (StepType step : a.getStep()) {
            ret.getStep().add(step);
        }

        return ret;
    }

}

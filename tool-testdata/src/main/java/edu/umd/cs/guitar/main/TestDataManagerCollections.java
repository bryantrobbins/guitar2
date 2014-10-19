package edu.umd.cs.guitar.main;

/**
 * Created by bryan on 10/4/14.
 * <p/>
 * This class provides identifiers for the various collections used by
 * TestDataManager.
 */
public final class TestDataManagerCollections {

    /**
     * This empty private default constructor disables the default public
     * constructor.
     */
    private TestDataManagerCollections() {

    }

    /**
     * Use this ID for tests collection.
     */
    public static final String TESTS = "tests";

    /**
     * Use this ID for the suites collection.
     */
    public static final String SUITES = "suites";

    /**
     * Use this ID for the artifacts collection.
     */
    public static final String ARTIFACTS = "artifacts";

    /**
     * This variable holds the prefix for individual suite collections.
     */
    private static final String SUITE_PREFIX = "suite_";

    /**
     * This variable holds the prefix for individual execution collections.
     */
    private static final String EXECUTION_PREFIX = "exec_";

    /**
     * This method builds a String for an individual suite collection
     * given a suite ID.
     *
     * @param suiteId the ID of the suite whose collection ID is needed
     * @return the id of the suite-specific collection
     */
    public static String idsInSuite(final String suiteId) {
        return SUITE_PREFIX + suiteId;
    }

    /**
     * This method builds a String for an individual execution
     * collection given an execution ID.
     *
     * @param executionId the ID of the execution whose collection ID is needed
     * @return the id of the execution-specific collection
     */

    public static String idsInExecution(final String executionId) {
        return EXECUTION_PREFIX + executionId;
    }

}
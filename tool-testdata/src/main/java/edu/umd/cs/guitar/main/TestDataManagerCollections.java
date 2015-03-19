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
     * Use this ID for the bundles collection.
     */
    public static final String BUNDLES = "bundles";

    /**
     * Use this ID for the artifacts collection.
     */
    public static final String ARTIFACTS = "artifacts";

    /**
     * User this ID for the results collection.
     */
    public static final String RESULTS = "results";

    /**
     * Use this ID for the groups collection.
     */
    public static final String GROUPS = "groups";

    /**
     * This variable holds the prefix for individual suite collections.
     */
    private static final String SUITE_PREFIX = "suite_";

    /**
     * This variable holds the prefix for individual execution collections.
     */
    private static final String BUNDLE_PREFIX = "bundle_";

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
     * This method builds a String for an individual bundle
     * collection given a bundle ID.
     *
     * @param bundleId the ID of the execution whose collection ID is needed
     * @return the id of the bundle-specific collection
     */

    public static String idsInBundle(final String bundleId) {
        return BUNDLE_PREFIX + bundleId;
    }

}

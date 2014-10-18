package edu.umd.cs.guitar.artifacts;

/**
 * Created by bryan on 10/5/14
 * <p/>
 * This class provides an enum for the allowed categories for artifact
 * processing.
 */
public enum ArtifactCategory {
    /**
     * The TEST_INPUT artifact type is for input artifacts associated with
     * single tests.
     */
    TEST_INPUT("test_input"),
    /**
     * The TEST_OUTPUT artifact type is for artifacts resulting from test
     * execution.
     */
    TEST_OUTPUT("test_output"),
    /**
     * The SUITE_INPUT artifact type is for input artifacts associated with
     * an entire suite of tests.
     */
    SUITE_INPUT("suite_input"),
    /**
     * The SUITE_OUTPUT artifact type is for artifacts resulting from an
     * entire suite's execution.
     */
    SUITE_OUTPUT("suite_output");

    /**
     * This key is the actual string used to identify the artifact category
     * in relevant JSON objects in the DB.
     */
    private String key;

    /**
     * This is the private constructor for categories given a key.
     *
     * @param keyVal String to be used to identify this category in DB JSON
     *               objects
     */

    private ArtifactCategory(final String keyVal) {
        this.key = keyVal;
    }

    /**
     * For DB-level operations, this method returns the key
     * to be used in DB JSON objects for this artifact category.
     *
     * @return the key for this artifact category
     */

    public String getKey() {
        return key;
    }
}

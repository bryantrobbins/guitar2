package edu.umd.cs.guitar.testdata;

public class TestDataManagerDefaults {

	// Default options for a TestDataManager

	public static final String HOSTNAME = "localhost";
	public static final String PORT = "27017";
	public static final String ID_PREFIX = "bryan_";

	// Collections
	public static final String COLLECTION_TESTS = "tests";
	public static final String COLLECTION_SUITE_METADATA = "suite_metadata";
	public static final String COLLECTION_COVERAGE = "coverage";

	// Keys
	public static final String KEY_TEST_ID = "testId";
    public static final String KEY_SUITE_ID = "suiteId";
    public static final String KEY_EXECUTION_ID = "execId";

    public static final String KEY_TEST_STEPS = "testCase";
	public static final String KEY_SER_FILE = "serId";
	public static final String KEY_GUI_FILE = "gui";
	public static final String KEY_MAP_FILE = "map";


	
	// Keys for computed values
	public static final String KEY_COVERAGE = "coverage_xml";
	public static final String KEY_MODEL = "model";
	
	// Prefixes for custom keys
	public static final String PREFIX_ARTIFACT= "artifact_";
	public static final String PREFIX_MODEL= "model_";
}

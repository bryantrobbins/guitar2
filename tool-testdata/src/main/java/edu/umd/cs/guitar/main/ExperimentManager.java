package edu.umd.cs.guitar.main;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import edu.umd.cs.guitar.artifacts.ArtifactCategory;
import edu.umd.cs.guitar.processors.applog.TextObject;
import edu.umd.cs.guitar.processors.guitar.FeaturesProcessor;
import edu.umd.cs.guitar.processors.guitar.LogProcessor;
import edu.umd.cs.guitar.util.MongoUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bryan on 10/4/14.
 * This class is the entrypoint for storing test data into a MongoDB database
 */
public final class ExperimentManager {
    /**
     * This instance provides logging with log4j.
     */
    private static Logger logger = LogManager.getLogger(ExperimentManager.class);

    /**
     * The interval at which to print progress.
     */
    private static final int PROGRESS_INTERVAL = 100;

    /**
     * Override the default constructor for this utility class.
     */
    private ExperimentManager() {

    }

    /**
     * Add results for a given set of bundles.
     *
     * @param mongoHost the mongodb host to use
     * @param mongoPort the mongodb port to use
     * @param dbId      the db to use
     * @param suiteId   the suite which was executed
     * @param bundleIds the bundles which contain suite executions
     * @return the result id, or null if posting fails
     */
    public static String postResults(final String mongoHost,
                                     final String mongoPort,
                                     final String dbId,
                                     final String suiteId,
                                     final List<String> bundleIds) {
        TestDataManager manager = new TestDataManager(mongoHost, mongoPort, dbId);
        String resultId = manager.generateId();
        if (postResults(manager, resultId, suiteId, bundleIds)) {
            return resultId;
        } else {
            return null;
        }
    }

    /**
     * Add results for a given set of bundles.
     *
     * @param manager   the TestDataManager to use
     * @param resultId  the id of the results
     * @param suiteId   the suite which was executed
     * @param bundleIds the bundles which contain suite executions
     * @return true if update succeeds, false otherwise
     */
    private static boolean postResults(
            final TestDataManager manager,
            final String resultId,
            final String suiteId,
            final List<String> bundleIds) {

        int numBundles = bundleIds.size();

        // Initialize result storage
        BasicDBList pass = new BasicDBList();
        BasicDBList fail = new BasicDBList();
        BasicDBList inconsistent = new BasicDBList();

        // For each test in suite
        for (String testId : manager.getTestIdsInSuite(suiteId)) {
            int passCount = 0;

            // For each bundle
            for (String bundleId : bundleIds) {
                // get execution id
                String execId =
                        manager.getExecutionIdForTestIdInBundle(bundleId, testId);

                // get log artifact
                LogProcessor logProc = new LogProcessor();
                ArtifactCategory output = ArtifactCategory.TEST_OUTPUT;
                TextObject logObject =
                        (TextObject) manager.getArtifactByCategoryAndOwnerId(output,
                                execId, logProc);

                // determine test result
                TextObject.TestResult result =
                        TextObject.TestResult.EXECUTION_MISSING;

                if (logObject != null) {
                    result = logObject.computeResult();
                }

                if (result.equals(TextObject.TestResult.PASS)) {
                    passCount++;
                }
            }

            // If the test passed every time
            if (passCount == numBundles) {
                pass.add(testId);
            } else if (passCount == 0) {
                fail.add(testId);
            } else {
                inconsistent.add(testId);
            }
        }

        // Build the DBObject
        BasicDBObject bdo = new BasicDBObject();
        // Put result id
        bdo.put(TestDataManagerKeys.RESULT_ID, resultId);

        // Put suite id
        bdo.put(TestDataManagerKeys.SUITE_ID, suiteId);

        // Put bundle ids
        BasicDBList bundles = new BasicDBList();
        bundles.addAll(bundleIds);
        bdo.put(TestDataManagerKeys.BUNDLE_ID, bundles);

        // Put results
        BasicDBObject resultBdo = new BasicDBObject();
        resultBdo.put(TestDataManagerKeys.PASSING_RESULTS, pass);
        resultBdo.put(TestDataManagerKeys.FAILING_RESULTS, fail);
        resultBdo.put(TestDataManagerKeys.INCONSISTENT_RESULTS, inconsistent);
        bdo.put(TestDataManagerKeys.RESULTS, resultBdo);

        // Insert the object
        return MongoUtils.addItemToCollection(manager.getDb(),
                TestDataManagerCollections.RESULTS,
                bdo);
    }

    /**
     * Generate and save the features artifact for a given test case.
     *
     * @param manager the TestDataManager to use
     * @param testId  the test id to generate and save features for
     * @return the artifact id
     */
    private static String addFeaturesToTest(
            final TestDataManager manager,
            final String testId) {
        FeaturesProcessor featuresProcessor = new FeaturesProcessor(manager);

        Map<String, String> options = new HashMap<String, String>();
        options.put(FeaturesProcessor.TEST_ID_OPTION, testId);
        return manager.saveArtifact(
                ArtifactCategory.TEST_INPUT,
                featuresProcessor,
                options,
                testId
        );
    }

    /**
     * Generate and save the features artifact for a given test case.
     *
     * @param mongoHost the mongodb host to use
     * @param mongoPort the mongodb port to use
     * @param dbId      the db to use
     * @param testId    the test id to generate and save features for
     * @return the artifact id
     */
    public static String addFeaturesToTest(
            final String mongoHost,
            final String mongoPort,
            final String dbId,
            final String testId) {
        TestDataManager manager = new TestDataManager(mongoHost, mongoPort, dbId);
        FeaturesProcessor featuresProcessor = new FeaturesProcessor(manager);

        Map<String, String> options = new HashMap<String, String>();
        options.put(FeaturesProcessor.TEST_ID_OPTION, testId);
        return manager.saveArtifact(
                ArtifactCategory.TEST_INPUT,
                featuresProcessor,
                options,
                testId
        );
    }

    /**
     * Add features to all test cases currently associated with a given test suite.
     *
     * @param mongoHost the mongodb host to use
     * @param mongoPort the mongodb port to use
     * @param dbId      the db to use
     * @param suiteId   the suite to add features to
     * @return true if all features added successfully, otherwise false
     */
    public static boolean addFeaturesToSuite(
            final String mongoHost,
            final String mongoPort,
            final String dbId,
            final String suiteId) {
        boolean success = true;

        TestDataManager manager = new TestDataManager(mongoHost, mongoPort, dbId);
        int count = 0;
        for (String testId : manager.getTestIdsInSuite(suiteId)) {
            count++;
            if ((count % PROGRESS_INTERVAL) == 0) {
                System.out.println(".");
            }
            if (addFeaturesToTest(manager, testId) == null) {
                return false;
            }
        }
        return true;
    }

}

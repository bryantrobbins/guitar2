package edu.umd.cs.guitar.main;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import edu.umd.cs.guitar.artifacts.ArtifactCategory;
import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.processors.applog.TextObject;
import edu.umd.cs.guitar.processors.features.FeaturesObject;
import edu.umd.cs.guitar.processors.guitar.EFGProcessor;
import edu.umd.cs.guitar.processors.guitar.FeaturesProcessor;
import edu.umd.cs.guitar.processors.guitar.GUIProcessor;
import edu.umd.cs.guitar.processors.guitar.LogProcessor;
import edu.umd.cs.guitar.processors.guitar.TestcaseProcessor;
import edu.umd.cs.guitar.util.MongoUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
     * Get a results object by its ID.
     *
     * @param manager  the TestDataManager to use
     * @param resultId the the id of the results
     * @return a DBObject of the results
     */
    public static DBObject getResultsObject(final TestDataManager manager, final String resultId) {
        DBObject query = new BasicDBObject(TestDataManagerKeys.RESULT_ID, resultId);
        return manager.getDb().getCollection(TestDataManagerCollections.RESULTS).findOne(query);
    }


    /**
     * Generate a test suite of test case combinations given an input test suite and input suite
     * execution results.
     *
     * @param manager  a TestDataManager object to use
     * @param suiteId  the input test suite ID
     * @param resultId the results to use in identifying feasible input test cases
     * @param size     the number of test cases to put in the combined suite
     * @return true upon success, false otherwise
     */
    public static boolean generateCombinationSuite(final TestDataManager manager,
                                                   final String suiteId,
                                                   final String resultId,
                                                   final int size) {

        Random random = new Random();

        // Get passing tests from input suite
        DBObject resultsObject = (DBObject) getResultsObject(manager, resultId).get(TestDataManagerKeys.RESULTS);
        BasicDBList passing = (BasicDBList) resultsObject.get(TestDataManagerKeys.PASSING_RESULTS);

        // Build processors for these objects
        EFGProcessor efgProc = new EFGProcessor(manager.getDb());
        GUIProcessor guiProc = new GUIProcessor(manager.getDb());
        TestcaseProcessor tcProc = new TestcaseProcessor();

        String cbId = suiteId + "_combined";
        manager.createNewSuite(cbId);
        logger.debug("Combined suite " + cbId + " created");

        // Save the EFG and GUI
        manager.copyArtifact(ArtifactCategory.SUITE_INPUT, cbId, suiteId, efgProc, null);
        manager.copyArtifact(ArtifactCategory.SUITE_INPUT, cbId, suiteId, guiProc, null);

        // Create random test combinations until suite has desired size
        HashSet<String> combinedIds = new HashSet<String>();
        while (combinedIds.size() < size) {

            int aindex = random.nextInt(passing.size());
            int bindex = random.nextInt(passing.size());
            String aid = passing.get(aindex).toString();
            String bid = passing.get(bindex).toString();
            String cid = aid + "_CONCAT_" + bid;

            if (!aid.equals(bid) && !combinedIds.contains(cid)) {
                combinedIds.add(cid);
                TestCase a = (TestCase) manager.getArtifactByCategoryAndOwnerId(ArtifactCategory.TEST_INPUT,
                        aid, tcProc);
                TestCase b = (TestCase) manager.getArtifactByCategoryAndOwnerId(ArtifactCategory.TEST_INPUT,
                        bid, tcProc);
                TestCase c = TestcaseProcessor.concat(a, b);
                manager.saveArtifact(ArtifactCategory.TEST_INPUT, tcProc, c, cid);
                manager.addTestCaseToSuite(cid, cbId);
            }
        }
        return true;
    }

    /**
     * Generate and save the features artifact for a given test case.
     *
     * @param manager           the TestDataManager to use
     * @param testId            the test id to generate and save features for
     * @param featuresProcessor the features processor to use to produce features for this test case
     * @param trim              whether or not to trim the features of the test case if infeasible
     * @return the artifact id
     */
    private static String addFeaturesToTest(
            final TestDataManager manager,
            final String testId,
            final FeaturesProcessor featuresProcessor,
            final boolean trim) {
        String trimString = "false";
        if (trim) {
            trimString = "true";
        }
        Map<String, String> options = new HashMap<String, String>();
        options.put(FeaturesProcessor.TEST_ID_OPTION, testId);
        options.put(FeaturesProcessor.TRIM_OPTION, trimString);
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
     * @param maxN      the max value of N to use when extracting N-grams from test cases
     * @return the artifact id
     */
    public static String addFeaturesToTest(
            final String mongoHost,
            final String mongoPort,
            final String dbId,
            final String testId,
            final int maxN) {
        TestDataManager manager = new TestDataManager(mongoHost, mongoPort, dbId);
        FeaturesProcessor featuresProcessor = new FeaturesProcessor(manager, maxN);

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
     * @param maxN      the max value of N to use when extracting N-grams from test cases
     * @return true if all features added successfully, otherwise false
     */
    public static boolean addFeaturesToSuite(
            final String mongoHost,
            final String mongoPort,
            final String dbId,
            final String suiteId,
            final int maxN) {

        TestDataManager manager = new TestDataManager(mongoHost, mongoPort, dbId);
        FeaturesProcessor fproc = new FeaturesProcessor(manager, maxN);
        int count = 0;
        for (String testId : manager.getTestIdsInSuite(suiteId)) {
            count++;
            if ((count % PROGRESS_INTERVAL) == 0) {
                System.out.println(".");
            }
            addFeaturesToTest(manager, testId, fproc, false);
        }

        return true;
    }

    /**
     * Add features to test cases in given suites which are aware of features
     * across all test cases.
     *
     * @param mongoHost        the mongodb host to use
     * @param mongoPort        the mongodb port to use
     * @param dbId             the db to use
     * @param inputSuiteId     input suite to use for feature extraction
     * @param predictedSuiteId suite of test cases to be predicted
     * @param maxN             max value of N to use when extracting N-grams from test cases
     * @return the unique id for the created group if successful, else null
     */
    public static String addGlobalFeaturesForSuites(final String mongoHost,
                                                    final String mongoPort,
                                                    final String dbId,
                                                    final String inputSuiteId,
                                                    final String predictedSuiteId,
                                                    final int maxN) {
        TestDataManager manager = new TestDataManager(mongoHost, mongoPort, dbId);
        FeaturesProcessor fproc = new FeaturesProcessor(manager, maxN);
        HashSet<String> allFeatures = new HashSet<String>();

        int count = 0;
        String groupId = manager.generateId();

        HashSet<String> suiteSet = new HashSet<String>();
        suiteSet.add(inputSuiteId);
        suiteSet.add(predictedSuiteId);

        // Add features to all test cases
        // Save features from input suite
        for (String suiteId : suiteSet) {
            for (String testId : manager.getTestIdsInSuite(suiteId)) {
                count++;
                if ((count % PROGRESS_INTERVAL) == 0) {
                    System.out.println(".");
                }

                FeaturesObject fob = (FeaturesObject) manager.getArtifactByCategoryAndOwnerId(
                        ArtifactCategory.TEST_INPUT,
                        testId,
                        fproc
                );

                if (fob == null) {
                    String artifactId = null;
                    if (suiteId.equals(inputSuiteId)) {
                        artifactId = addFeaturesToTest(manager, testId, fproc, true);
                    } else {
                        artifactId = addFeaturesToTest(manager, testId, fproc, false);
                    }
                    fob = (FeaturesObject) manager.getArtifactById(artifactId, fproc);
                }
                if (suiteId.equals(inputSuiteId)) {
                    allFeatures.addAll(fob.getFeatures());
                }
            }
        }

        // Build the DBObject
        BasicDBObject bdo = new BasicDBObject();

        // Add GroupId
        bdo.put(TestDataManagerKeys.GROUP_ID, groupId);

        // Add suite ids
        bdo.put(TestDataManagerKeys.SUITE_ID + "_input", inputSuiteId);
        bdo.put(TestDataManagerKeys.SUITE_ID + "_predicted", predictedSuiteId);

        // Build and add the global feature list
        BasicDBList dbl = new BasicDBList();
        System.out.println("Features List has size of " + allFeatures.size());
        dbl.addAll(allFeatures);
        bdo.put(TestDataManagerKeys.FEATURES_LIST, dbl);

        // Add the value of N
        bdo.put(TestDataManagerKeys.MAX_N, maxN);

        // Record the group entry
        MongoUtils.addItemToCollection(manager.getDb(),
                TestDataManagerCollections.GROUPS,
                bdo);

//        System.out.println("Adding global features");
//        for (String suiteId : suiteIds) {
//            for (String testId : manager.getTestIdsInSuite(suiteId)) {
//                BasicDBObject myDbo = new BasicDBObject();
//                // Build list of features from list of all possible
//                FeaturesObject fob = (FeaturesObject) manager.getArtifactByCategoryAndOwnerId(
//                        ArtifactCategory.TEST_INPUT,
//                        testId,
//                        fproc
//                );
//
//                BasicDBObject myGlobalFeatures = new BasicDBObject();
//                for (String feature : allFeatures) {
//                    if (fob.getFeatures().contains(feature)) {
//                        myGlobalFeatures.append(feature, "1.0");
//                    } else {
//                        myGlobalFeatures.append(feature, "0.0");
//                    }
//                }
//
//                // Add the overall group id for this entry
//                myDbo.append(TestDataManagerKeys.GROUP_ID, groupId);
//
//                // Add the test id for this test
//                myDbo.append(TestDataManagerKeys.TEST_ID, testId);
//
//                // Add the suite id for this test
//                myDbo.append(TestDataManagerKeys.SUITE_ID, suiteId);
//
//                // Add the global features object
//                myDbo.put(TestDataManagerKeys.FEATURES_OBJECT, myGlobalFeatures);
//
//                // Add an easy-to-parse feature list
//                // Add Dbo to GlobalizedFeatures collection
//                MongoUtils.addItemToCollection(manager.getDb(),
//                        TestDataManagerCollections.GLOBAL_FEATURES,
//                        myDbo);
//
//            }
//        }

        return groupId;
    }

}

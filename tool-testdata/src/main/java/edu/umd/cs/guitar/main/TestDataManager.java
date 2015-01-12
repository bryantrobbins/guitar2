package edu.umd.cs.guitar.main;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import edu.umd.cs.guitar.artifacts.ArtifactCategory;
import edu.umd.cs.guitar.artifacts.ArtifactProcessor;
import edu.umd.cs.guitar.util.MongoUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by bryan on 10/4/14.
 * This class is the entrypoint for storing test data into a MongoDB database
 */
public final class TestDataManager {
    /**
     * This instance provides logging with log4j.
     */
    private static Logger logger = LogManager.getLogger(TestDataManager.class);

    /**
     * A Mongo client for interacting with DB.
     */
    private MongoClient client;

    /**
     * The DB instance for this Manager.
     */
    private DB db;

    /**
     * Default constructor. Uses localhost:27017/dbname, where
     * dbname based on current system time from System.nanoTime()
     */
    public TestDataManager() {
        this("amalga_" + System.nanoTime());
    }

    /**
     * Constructor using localhost:27017/dbId as MongoDB connection.
     *
     * @param dbId dbId to use
     */

    public TestDataManager(final String dbId) {
        this("localhost", "27017", dbId);
    }

    /**
     * Constructor using given host and port, getting dbId based on
     * System.nanoTime().
     *
     * @param host hostname to use for the MongoDB connection
     * @param port port to use for the MongoDB connection
     */

    public TestDataManager(final String host, final String port) {
        this(host, port, "amalga_" + System.nanoTime());
    }

    /**
     * Constructor given all 3 of host, port, and dbId for MongoDB connection.
     *
     * @param host hostname to use for the MongoDB connection
     * @param port port to use for the MongoDB connection
     * @param dbId dbId to use
     */

    public TestDataManager(final String host, final String port,
                           final String dbId) {
        client = MongoUtils.createMongoClient(host, port);
        db = client.getDB(dbId);
    }

    /**
     * Provides a String suitable for use as a unique ID across this DB
     * instance.
     *
     * @return unique String
     */
    public String generateId() {
        return ObjectId.get().toString();
    }

    /**
     * Get the DB ID used for this instance. Needed because this can be
     * dynamically generated
     * depending on the choice of constructor
     *
     * @return the ID of the database in MongoDB
     */
    public String getDBId() {
        return db.getName();
    }

    /**
     * Get DB instance for this Manager.
     *
     * @return the DB instance
     */
    public DB getDb() {
        return db;
    }

    /**
     * Create a new test entry, generating and returning a unique test ID.
     *
     * @return the unique id of the test
     */
    public String createNewTest() {

        // Generate unique ID for test
        String testId = generateId();

        // Create the test
        createNewTest(testId);

        // Return testId
        return testId;
    }

    /**
     * Create a new test entry given a unique id for the test. id only needs
     * to be unique across current DB instance.
     *
     * @param testId the test id
     */
    public void createNewTest(final String testId) {
        // Create DBObject
        BasicDBObject basicDBObject = new BasicDBObject()
                .append(TestDataManagerKeys.TEST_ID, testId);

        // Insert object in tests table
        MongoUtils.addItemToCollection(db, TestDataManagerCollections.TESTS,
                basicDBObject);
    }

    /**
     * Create a new execution entry given a unique id for this test execution.
     * id only needs to be unique across current DB instance.
     *
     * @param executionId the execution id
     */
    public void createNewExecution(final String executionId) {
        // Create DBObject
        BasicDBObject basicDBObject = new BasicDBObject()
                .append(TestDataManagerKeys.EXECUTION_ID, executionId);

        // Insert object in tests table
        MongoUtils.addItemToCollection(db,
                TestDataManagerCollections.EXECUTIONS, basicDBObject);
    }


    /**
     * Create a new test suite entry, returning a unique ID for the suite.
     *
     * @return the unique id of the test suite
     */
    public String createNewSuite() {

        // Generate unique ID for suite
        String suiteId = generateId();

        createNewSuite(suiteId);

        // Send it back for posterity
        return suiteId;
    }


    /**
     * Create a new test suite entry given a suite id. Any existing test
     * cases associated with the same suite name will be cleared.
     *
     * @param suiteId the unique ID for this suite
     */
    public void createNewSuite(final String suiteId) {

        // Create DBObject
        BasicDBObject basicDBObject = new BasicDBObject()
                .append(TestDataManagerKeys.SUITE_ID, suiteId);

        // Insert object in suites table
        MongoUtils.addItemToCollection(db, TestDataManagerCollections.SUITES,
                basicDBObject);

        // Clear any existing entries in suite-specific table
        resetTestSuite(suiteId);
    }


    /**
     * Save an artifact to the DB.
     *
     * @param category  the category of the artifact from choices in edu.umd
     *                  .cs.guitar.artifacts.ArtifactCategory
     * @param processor an ArtifactProcessor instance for the artifact
     * @param options   Options used in constructing the artifact
     * @param owner     unique ID of the parent/owner of this artifact
     *                  (should be a test or suite)
     * @return unique id of the artifact
     */
    public String saveArtifact(final ArtifactCategory category,
                               final ArtifactProcessor<?> processor,
                               final Map<String,
                                       String> options, final String owner) {
        String artifactId = generateId();

        String key = processor.getKey();
        String index = options.get(ArtifactProcessor.INDEX_OPTION);

        // Build key with optional index suffix
        if (index != null) {
            key += index;
        }

        // Create object
        BasicDBObject basicDBObject = new BasicDBObject()
                .append(TestDataManagerKeys.ARITFACT_ID, artifactId)
                .append(TestDataManagerKeys.ARTIFACT_CATEGORY,
                        category.getKey())
                .append(TestDataManagerKeys.ARTIFACT_OWNER_ID, owner)
                .append(TestDataManagerKeys.ARTIFACT_TYPE, key)
                .append(TestDataManagerKeys.ARTIFACT_DATA,
                        processor.jsonFromOptions(options));

        MongoUtils.addItemToCollection(db, TestDataManagerCollections
                .ARTIFACTS, basicDBObject);

        return artifactId;
    }

    /**
     * Get an artifact as an object given its unique artifact ID.
     *
     * @param artifactId unique ID of artifact
     * @param processor  ArtifactProcessor for the artifact
     * @return artifact
     */
    public Object getArtifactById(final String artifactId,
                                  final ArtifactProcessor<?> processor) {

        BasicDBObject query = new BasicDBObject()
                .append(TestDataManagerKeys.ARITFACT_ID, artifactId);

        String dataJson = (String) db
                .getCollection(TestDataManagerCollections.ARTIFACTS)
                .findOne(query).get(TestDataManagerKeys.ARTIFACT_DATA);
        return processor.objectFromJson(dataJson);
    }

    /**
     * Get an artifact given its category and the ID of its owner (usually a
     * test, suite, or execution).
     *
     * @param category  the category of the artifact from choices in
     *                  edu.umd.cs.guitar.artifacts.ArtifactCategory
     * @param ownerId   the ID of the owner of this artifact
     * @param processor an ArtifactProcessor instance for the artifact
     * @return the artifact as a Java object
     */
    public Object getArtifactByCategoryAndOwnerId(final ArtifactCategory
                                                          category,
                                                  final String ownerId,
                                                  final ArtifactProcessor<?>
                                                          processor) {

        return getArtifactByCategoryAndOwnerId(category,
                ownerId,
                processor,
                null);

    }

    /**
     * Get an artifact given its category and the ID of its owner (usually a
     * test, suite, or execution).
     *
     * @param category  the category of the artifact from choices in
     *                  edu.umd.cs.guitar.artifacts.ArtifactCategory
     * @param ownerId   the ID of the owner of this artifact
     * @param processor an ArtifactProcessor instance for the artifact
     * @param index     the index of the artifact (needed if more than one
     *                  artifact per type + owner)
     * @return the artifact as a Java object
     */
    public Object getArtifactByCategoryAndOwnerId(final ArtifactCategory
                                                          category,
                                                  final String ownerId,
                                                  final ArtifactProcessor<?>
                                                          processor,
                                                  final String index) {

        String key = processor.getKey();
        if (index != null) {
            key += index;
        }

        BasicDBObject query = new BasicDBObject()
                .append(TestDataManagerKeys.ARTIFACT_CATEGORY,
                        category.getKey())
                .append(TestDataManagerKeys.ARTIFACT_OWNER_ID, ownerId)
                .append(TestDataManagerKeys.ARTIFACT_TYPE, key);

        int count = db.getCollection(TestDataManagerCollections.ARTIFACTS)
                .find(query).size();

        if (count != 1) {
            logger.warn("When trying to get one artifact, "
                    + "there are " + count
                    + " matching");
        }

        String id = (String) db.getCollection(TestDataManagerCollections
                .ARTIFACTS)
                .findOne(query).get(TestDataManagerKeys.ARITFACT_ID);

        return getArtifactById(id, processor);
    }

    /**
     * Add an existing test case to an existing test suite.
     *
     * @param testId  the test to be added to a suite
     * @param suiteId the suite to which test should be added
     */

    public void addTestCaseToSuite(final String testId, final String suiteId) {

        // Create DBObject
        BasicDBObject basicDBObject = new BasicDBObject()
                .append(TestDataManagerKeys.TEST_ID, testId);

        // Insert object in suite-specific table
        MongoUtils.addItemToCollection(db, TestDataManagerCollections
                        .idsInSuite(suiteId),
                basicDBObject);
    }

    /**
     * Remove any currently associated test cases from a suite. Note: this only
     * removes the association of the test case with the suite. It does not
     * actually remove the test cases from the DB.
     *
     * @param suiteId the suite to be cleared
     */

    public void resetTestSuite(final String suiteId) {
        MongoUtils.removeAllItemsFromCollection(getDb(),
                TestDataManagerCollections.idsInSuite(suiteId));

    }

    /**
     * Returns the test ids in a given test suite.
     *
     * @param suiteId the suite id
     * @return the list of test ids in the given suite
     */
    public List<String> getTestIdsInSuite(final String suiteId) {
        DBCollection tests = db.getCollection(
                TestDataManagerCollections.idsInSuite(suiteId));

        DBCursor curs = tests.find();

        ArrayList<String> ret = new ArrayList<String>();

        while (curs.hasNext()) {
            Map<String, String> nextMap = curs.next().toMap();
            ret.add(nextMap.get(TestDataManagerKeys.TEST_ID));
        }
        return ret;
    }

}

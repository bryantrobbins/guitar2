package edu.umd.cs.guitar.testdata;

import com.mongodb.*;
import edu.berkeley.nlp.lm.ArrayEncodedProbBackoffLm;
import edu.berkeley.nlp.lm.ConfigOptions;
import edu.berkeley.nlp.lm.StringWordIndexer;
import edu.berkeley.nlp.lm.collections.Iterators;
import edu.berkeley.nlp.lm.io.ArpaLmReader;
import edu.berkeley.nlp.lm.io.KneserNeyLmReaderCallback;
import edu.berkeley.nlp.lm.io.LmReaders;
import edu.berkeley.nlp.lm.io.TextReader;
import edu.umd.cs.guitar.testdata.berkeleylm.ArpaObject;
import edu.umd.cs.guitar.testdata.berkeleylm.ArpaObjectLmReader;
import edu.umd.cs.guitar.testdata.berkeleylm.KneserNeyObjectLmReaderCallback;
import edu.umd.cs.guitar.testdata.loader.ArtifactProcessor;
import edu.umd.cs.guitar.testdata.util.MongoUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

public class TestDataManager {

    private static Logger logger = LogManager.getLogger(TestDataManager.class);
    private String dbId;
    MongoClient client;

    public static String createDb(String prefix) {
        String id = generateId(prefix);
        return id;
    }

    public static String createDb() {
        return createDb(TestDataManagerDefaults.ID_PREFIX);
    }

    public static String generateId(String prefix) {
        Date date = new Date();
        return prefix + date.getTime();
    }

    public TestDataManager() {
        this(TestDataManagerDefaults.HOSTNAME, TestDataManagerDefaults.PORT, createDb());

        logger.warn("Using default values for hostname and port " + TestDataManagerDefaults.HOSTNAME
                + ":" + TestDataManagerDefaults.PORT);

    }

    public TestDataManager(String host, String port) {
        this(host, port, createDb());
    }

    public TestDataManager(String hostname, String port, String dbId) {
        this.client = MongoUtils.createMongoClient(hostname, port);
        this.dbId = dbId;
    }

    public boolean addArtifactToTest(String testId,
                                     Map<String, String> options, ArtifactProcessor<?> proc) throws Exception {

        String key = proc.getKey();
        String json = proc.jsonFromOptions(options);

        BasicDBObject artifactObject = new BasicDBObject();
        artifactObject.append("$set", new BasicDBObject().append(
                TestDataManagerDefaults.PREFIX_ARTIFACT + key, json));

        BasicDBObject testQuery = new BasicDBObject().append(
                TestDataManagerDefaults.KEY_TEST_ID, testId);

        DBCollection tests = client.getDB(dbId).getCollection(
                TestDataManagerDefaults.COLLECTION_TESTS);

        boolean res = true;
        // See if test exists
        if (tests.find(testQuery).size() < 1) {
            // We need to create the test entry first
            res = MongoUtils.addItemToCollection(client.getDB(dbId),
                    TestDataManagerDefaults.COLLECTION_TESTS, testQuery) && res;
        }

        // Append the artifact to the test entry
        tests.update(testQuery, artifactObject);

        return true;
    }

    public boolean addArtifactToExecution(String testId,
                                          String execId, Map<String, String> options, ArtifactProcessor<?> proc) throws Exception {


        String key = proc.getKey();
        String json = proc.jsonFromOptions(options);

        boolean res = true;

        BasicDBObject artifactObject = new BasicDBObject().append("$set", new BasicDBObject().append(
                TestDataManagerDefaults.PREFIX_ARTIFACT + key, json));

        BasicDBObject testQuery = new BasicDBObject().append(
                TestDataManagerDefaults.KEY_TEST_ID, testId);

        // Add test entry to this execution if not already present
        if (!MongoUtils.isItemInCollection(client.getDB(dbId), execId,
                testQuery)) {
            res = res
                    && MongoUtils.addItemToCollection(client.getDB(dbId),
                    execId, testQuery);
        }

        // Append the artifact to the execution entry
        DBCollection executions = client.getDB(dbId).getCollection(execId);
        executions.update(testQuery, artifactObject);

        return res;
    }


    public Object getTestArtifact(String testId,
                                  ArtifactProcessor<?> proc) throws InstantiationException,
            IllegalAccessException {
        DBCollection tests = client.getDB(dbId).getCollection(
                TestDataManagerDefaults.COLLECTION_TESTS);
        DBObject query = new BasicDBObject(TestDataManagerDefaults.KEY_TEST_ID,
                testId);

        String key = proc.getKey();
        String json = tests.find(query).next().toMap()
                .get(TestDataManagerDefaults.PREFIX_ARTIFACT + key).toString();

        return proc.objectFromJson(json);
    }

    public Object getExecutionArtifact(String testId, String execId,
                                       ArtifactProcessor<?> proc) throws InstantiationException,
            IllegalAccessException {
        DBCollection tests = client.getDB(dbId).getCollection(
                execId);
        DBObject query = new BasicDBObject(TestDataManagerDefaults.KEY_TEST_ID,
                testId);

        String key = proc.getKey();
        String json = tests.find(query).next().toMap()
                .get(TestDataManagerDefaults.PREFIX_ARTIFACT + key).toString();

        return proc.objectFromJson(json);
    }

    public Object waitForExecutionArtifact(String dbId, String testId, String executionId, ArtifactProcessor<?> proc) {
        String key = proc.getKey();

        DBCollection tests = client.getDB(dbId).getCollection(
                executionId);
        DBObject query = new BasicDBObject(TestDataManagerDefaults.KEY_TEST_ID,
                testId);

        while (tests.find(query).size() < 1) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String json = tests.find(query).next().toMap()
                .get(TestDataManagerDefaults.PREFIX_ARTIFACT + key).toString();

        return proc.objectFromJson(json);
    }

    private List<Object> getArtifacts(String dbId, String suiteId,
                                      ArtifactProcessor<?> proc) throws InstantiationException,
            IllegalAccessException {
        List<String> ids = getTestIdsInSuite(dbId, suiteId);
        List<Object> artifacts = new ArrayList<Object>();
        for (String id : ids) {
            artifacts.add(getTestArtifact(id, proc));
        }

        return artifacts;
    }

    public ArrayEncodedProbBackoffLm<String> computeArtifactModel(String dbId,
                                                                  String suiteId, int maxOrder,
                                                                  Class<? extends ArtifactProcessor<?>> processorClass)
            throws InstantiationException, IllegalAccessException {

        ArtifactProcessor<?> proc = processorClass.newInstance();
        String key = proc.getKey();

        // Set up WordIndexer
        final StringWordIndexer wordIndexer = new StringWordIndexer();
        wordIndexer.setStartSymbol(ArpaLmReader.START_SYMBOL);
        wordIndexer.setEndSymbol(ArpaLmReader.END_SYMBOL);
        wordIndexer.setUnkSymbol(ArpaLmReader.UNK_SYMBOL);

        // Set up config options
        ConfigOptions opts = new ConfigOptions();

        // Set up iterable over TestCases
        List<Object> artifacts = getArtifacts(dbId, suiteId, proc);
        final Iterable<String> events = Iterators.able(proc
                .getIterator(artifacts));

        // Set up reader
        final TextReader<String> reader = new TextReader<String>(events,
                wordIndexer);

        // Set up callback handler
        KneserNeyLmReaderCallback<String> kneserNeyReader = new KneserNeyLmReaderCallback<String>(
                wordIndexer, maxOrder, opts);

        // Construct model (actually counts N-grams, etc.)
        reader.parse(kneserNeyReader);

        // Convert model to ARPA format
        // Instead of storing to file, I'm storing to an object to make JSON
        // conversion easier
        // The object has toJson() and toArpa() methods :)
        ArpaObject arpa = new ArpaObject();
        arpa.setMaxOrder(maxOrder);
        kneserNeyReader.parse(new KneserNeyObjectLmReaderCallback<String>(arpa,
                wordIndexer));

        // Convert ARPA model to JSON
        String jsonArpa = arpa.toJson();

        // Save model to suite object in suite metadata collection
        BasicDBObject modelObject = new BasicDBObject();
        modelObject.append("$set",
                new BasicDBObject().append(TestDataManagerDefaults.PREFIX_MODEL + key, jsonArpa));

        BasicDBObject suiteQuery = new BasicDBObject().append(
                TestDataManagerDefaults.KEY_SUITE_ID, suiteId);

        client.getDB(dbId)
                .getCollection(
                        TestDataManagerDefaults.COLLECTION_SUITE_METADATA)
                .update(suiteQuery, modelObject);

        return getNgramModel(dbId, suiteId, TestDataManagerDefaults.PREFIX_MODEL + key);
    }

    public boolean addTestToSuite(String testId, String suiteId) {

        BasicDBObject suiteMetadata = new BasicDBObject(
                TestDataManagerDefaults.KEY_SUITE_ID, suiteId);
        boolean res = true;

        if (!MongoUtils.isItemInCollection(client.getDB(dbId),
                TestDataManagerDefaults.COLLECTION_SUITE_METADATA,
                suiteMetadata)) {
            res = MongoUtils.addItemToCollection(client.getDB(dbId),
                    TestDataManagerDefaults.COLLECTION_SUITE_METADATA,
                    suiteMetadata);
        }

        BasicDBObject testObject = new BasicDBObject(
                TestDataManagerDefaults.KEY_TEST_ID, testId);
        if (!MongoUtils.isItemInCollection(client.getDB(dbId), suiteId,
                testObject)) {
            res = res
                    && MongoUtils.addItemToCollection(client.getDB(dbId),
                    suiteId, testObject);
        }

        return res;

    }

    public boolean clearTestSuite(String dbId, String suiteId) {

        boolean res = MongoUtils.removeItemFromCollection(client.getDB(dbId),
                TestDataManagerDefaults.COLLECTION_SUITE_METADATA,
                TestDataManagerDefaults.KEY_SUITE_ID, suiteId);

        return res
                && MongoUtils.removeAllItemsFromCollection(client.getDB(dbId),
                suiteId);
    }

    public List<String> getTestIdsInSuite(String dbId, String suiteId) {
        DBCollection tests = client.getDB(dbId).getCollection(suiteId);

        DBCursor curs = tests.find();

        ArrayList<String> ret = new ArrayList<String>();

        while (curs.hasNext()) {
            Map<String, String> nextMap = curs.next().toMap();
            ret.add(nextMap.get(TestDataManagerDefaults.KEY_TEST_ID));
        }
        return ret;
    }

    public ArrayEncodedProbBackoffLm<String> getNgramModel(String dbId,
                                                           String suiteId, String key) {
        // Get JSON of ArpaObject from Mongo
        DBCollection suites = client.getDB(dbId).getCollection(
                TestDataManagerDefaults.COLLECTION_SUITE_METADATA);
        DBObject query = new BasicDBObject(
                TestDataManagerDefaults.KEY_SUITE_ID, suiteId);

        String jsonArpa = suites.find(query).next().toMap().get(key).toString();

        // Convert to ArpaObject with GSON
        ArpaObject arpa = ArpaObject.fromJson(jsonArpa);

        // Set up ArpaObjectLmReader
        // Set up WordIndexer
        final StringWordIndexer wordIndexer = new StringWordIndexer();
        wordIndexer.setStartSymbol(ArpaLmReader.START_SYMBOL);
        wordIndexer.setEndSymbol(ArpaLmReader.END_SYMBOL);
        wordIndexer.setUnkSymbol(ArpaLmReader.UNK_SYMBOL);

        ArpaObjectLmReader<String> arpaReader = new ArpaObjectLmReader<String>(
                arpa, wordIndexer);

        // Convert to NgramLanguageModel
        // return LmReaders.readContextEncodedLmFromArpa(arpaReader,
        // wordIndexer, new ConfigOptions());
        return LmReaders.readArrayEncodedLmFromArpa(arpaReader, false,
                wordIndexer, new ConfigOptions());
    }

    public String selectRandomTestFromSuite(String dbId, String suiteId,
                                            List<String> seen) {
        List<String> candidates = getTestIdsInSuite(dbId, suiteId);
        for (String test : seen) {
            candidates.remove(test);
        }

        int random = new Random().nextInt(candidates.size());
        return candidates.get(random);
    }


    public boolean addSuiteProperty(String dbId, String suiteId, String key,
                                    String value) {
        BasicDBObject kvObject = new BasicDBObject();
        kvObject.append("$set", new BasicDBObject().append(key, value));

        BasicDBObject suiteQuery = new BasicDBObject().append(
                TestDataManagerDefaults.KEY_SUITE_ID, suiteId);

        WriteResult wr = client
                .getDB(dbId)
                .getCollection(
                        TestDataManagerDefaults.COLLECTION_SUITE_METADATA)
                .update(suiteQuery, kvObject);

        if (!wr.getLastError().ok()) {
            logger.error("Error while adding key/value pair (" + key + ","
                    + value + ")" + " to metadata for suite " + suiteId, wr
                    .getLastError().getException());
            return false;
        }

        return true;
    }

    public String getSuiteProperty(String dbId, String suiteId, String key) {
        BasicDBObject query = new BasicDBObject().append(
                TestDataManagerDefaults.KEY_SUITE_ID, suiteId);

        return client
                .getDB(dbId)
                .getCollection(
                        TestDataManagerDefaults.COLLECTION_SUITE_METADATA)
                .findOne(query).toMap().get(key).toString();
    }

    public Map<String, String> getSuiteProperties(String dbId, String suiteId,
                                                  List<String> fields) {
        BasicDBObject query = new BasicDBObject().append(
                TestDataManagerDefaults.KEY_SUITE_ID, suiteId);

        Map<String, String> ret = new HashMap<String, String>();

        Map rawMap = client
                .getDB(dbId)
                .getCollection(
                        TestDataManagerDefaults.COLLECTION_SUITE_METADATA)
                .findOne(query).toMap();

        for (String s : fields) {
            ret.put(s, rawMap.get(s).toString());
        }

        return ret;
        // Comment added by Shiraz
    }
}

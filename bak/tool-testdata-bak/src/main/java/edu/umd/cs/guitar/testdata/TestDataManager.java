package edu.umd.cs.guitar.testdata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.umd.cs.guitar.testdata.loader.ArtifactProcessor;
import net.sourceforge.cobertura.coveragedata.ProjectData;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;

import edu.berkeley.nlp.lm.ArrayEncodedProbBackoffLm;
import edu.berkeley.nlp.lm.ConfigOptions;
import edu.berkeley.nlp.lm.StringWordIndexer;
import edu.berkeley.nlp.lm.collections.Iterators;
import edu.berkeley.nlp.lm.io.ArpaLmReader;
import edu.berkeley.nlp.lm.io.KneserNeyLmReaderCallback;
import edu.berkeley.nlp.lm.io.LmReaders;
import edu.berkeley.nlp.lm.io.TextReader;
import edu.umd.cs.guitar.model.data.GUIMap;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.testdata.berkeleylm.ArpaObject;
import edu.umd.cs.guitar.testdata.berkeleylm.ArpaObjectLmReader;
import edu.umd.cs.guitar.testdata.berkeleylm.KneserNeyObjectLmReaderCallback;
import edu.umd.cs.guitar.testdata.guitar.TestCaseIterator;
import edu.umd.cs.guitar.testdata.util.CoberturaUtils;
import edu.umd.cs.guitar.testdata.util.GUITARUtils;
import edu.umd.cs.guitar.testdata.util.MongoUtils;

public class TestDataManager {

	private static Logger logger = LogManager.getLogger(TestDataManager.class);

	MongoClient client;

	public TestDataManager() {
		// Create object with default DB config
		String hostname = TestDataManagerDefaults.HOSTNAME;
		String port = TestDataManagerDefaults.PORT;

		logger.warn("Using default values for hostname and port " + hostname
				+ ":" + port);

		client = MongoUtils.createMongoClient(hostname, port);
	}

	public TestDataManager(String hostname, int port) {
		client = MongoUtils.createMongoClient(hostname, "" + port);
	}

	public String createDb(String prefix) {
		String id = generateId(prefix);
		return id;
	}

	public String createDb() {
		return createDb(TestDataManagerDefaults.ID_PREFIX);
	}

	public boolean addArtifactToTest(String dbId, String testId,
			String artifactPath,
			Class<? extends ArtifactProcessor<?>> processorClass)
			throws Exception {
		return addArtifactToTest(dbId, testId, artifactPath,
				processorClass.newInstance());
	}

	public boolean addArtifactToTest(String dbId, String testId,
			String artifactPath, ArtifactProcessor<?> proc) throws Exception {

        proc.setDB(client.getDB(dbId));
		String key = proc.getKey();
		String json = proc.process(artifactPath);

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


    public boolean addArtifactToExecution(String dbId, String testId, String execId,
                                     String artifactPath,
                                     Class<? extends ArtifactProcessor<?>> processorClass)
            throws Exception {
        return addArtifactToExecution(dbId, testId, execId, artifactPath,
                processorClass.newInstance());
    }

    public boolean addArtifactToExecution(String dbId, String testId,
                                     String execId, String artifactPath, ArtifactProcessor<?> proc) throws Exception {

        proc.setDB(client.getDB(dbId));

        String key = proc.getKey();
        String json = proc.process(artifactPath);

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



    public Object getTestArtifact(String dbId, String testId,
			Class<? extends ArtifactProcessor<?>> processorClass)
			throws InstantiationException, IllegalAccessException {
		return getTestArtifact(dbId, testId, processorClass.newInstance());
	}

	public Object getTestArtifact(String dbId, String testId,
			ArtifactProcessor<?> proc) throws InstantiationException,
			IllegalAccessException {
		DBCollection tests = client.getDB(dbId).getCollection(
				TestDataManagerDefaults.COLLECTION_TESTS);
		DBObject query = new BasicDBObject(TestDataManagerDefaults.KEY_TEST_ID,
				testId);

		String key = proc.getKey();
		String json = tests.find(query).next().toMap()
				.get(TestDataManagerDefaults.PREFIX_ARTIFACT + key).toString();

		return proc.objectify(json);
	}


    public Object getExecutionArtifact(String dbId, String testId, String execId,
                                  Class<? extends ArtifactProcessor<?>> processorClass)
            throws InstantiationException, IllegalAccessException {
        return getExecutionArtifact(dbId, testId, execId, processorClass.newInstance());
    }

    public Object getExecutionArtifact(String dbId, String testId, String execId,
                                  ArtifactProcessor<?> proc) throws InstantiationException,
            IllegalAccessException {
        DBCollection tests = client.getDB(dbId).getCollection(
                execId);
        DBObject query = new BasicDBObject(TestDataManagerDefaults.KEY_TEST_ID,
                testId);

        proc.setDB(client.getDB(dbId));
        String key = proc.getKey();
        String json = tests.find(query).next().toMap()
                .get(TestDataManagerDefaults.PREFIX_ARTIFACT + key).toString();

        return proc.objectify(json);
    }

    public Object waitForExecutionArtifact(String dbId, String testId, String executionId, ArtifactProcessor<?> proc){
        String key = proc.getKey();
        proc.setDB(client.getDB(dbId));

        DBCollection tests = client.getDB(dbId).getCollection(
                executionId);
        DBObject query = new BasicDBObject(TestDataManagerDefaults.KEY_TEST_ID,
                testId);

        while(tests.find(query).size() < 1){
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String json = tests.find(query).next().toMap()
                .get(TestDataManagerDefaults.PREFIX_ARTIFACT + key).toString();

        return proc.objectify(json);
    }

    private List<Object> getArtifacts(String dbId, String suiteId,
			ArtifactProcessor<?> proc) throws InstantiationException,
			IllegalAccessException {
		List<String> ids = getTestIdsInSuite(dbId, suiteId);
		List<Object> artifacts = new ArrayList<Object>();
		for (String id : ids) {
			artifacts.add(getTestArtifact(dbId, id, proc));
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
				new BasicDBObject().append(TestDataManagerDefaults.PREFIX_MODEL+ key, jsonArpa));

		BasicDBObject suiteQuery = new BasicDBObject().append(
				TestDataManagerDefaults.KEY_SUITE_ID, suiteId);

		client.getDB(dbId)
				.getCollection(
						TestDataManagerDefaults.COLLECTION_SUITE_METADATA)
				.update(suiteQuery, modelObject);

		return getNgramModel(dbId, suiteId, TestDataManagerDefaults.PREFIX_MODEL+ key);
	}

	public boolean addTestToSuite(String dbId, String testId, String suiteId) {

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

	public ProjectData computeCoverage(String dbId, String suiteId) {
		List<String> testsInSuite = getTestIdsInSuite(dbId, suiteId);

		ProjectData merged = new ProjectData();

		for (String testId : testsInSuite) {
			String fileId = getSerIdForTest(dbId, testId);

			try {
				ProjectData currentTestCoverage = CoberturaUtils
						.getCoverageObjectFromGFS(client.getDB(dbId), fileId);
				merged.merge(currentTestCoverage);
			} catch (IOException e) {
				logger.error("Error while trying to merge test coverage", e);
				return null;
			}
		}

		// Generate new fileId
		String serFileId = generateId("serFile_");

		// Store object to GridFS
		GridFSInputFile gfsFile = null;

		// GridFS client
		GridFS gfsCoverage = new GridFS(client.getDB(dbId),
				TestDataManagerDefaults.COLLECTION_COVERAGE);

		// Get file as stream from Cobertura
		ByteArrayOutputStream serStreamOut = new ByteArrayOutputStream();
		CoberturaUtils.saveCoverageData(merged, serStreamOut);

		// Put file on GridFS
		gfsFile = gfsCoverage.createFile(new ByteArrayInputStream(serStreamOut
				.toByteArray()));

		if (gfsFile == null) {
			return null;
		}

		// Update file with generated file handle for future retrieval
		gfsFile.setFilename(serFileId);
		gfsFile.save();

		// Store file handle from GridFS with test suite metadata object in
		// Mongo
		BasicDBObject coverageObject = new BasicDBObject();
		coverageObject.append("$set", new BasicDBObject().append(
				TestDataManagerDefaults.KEY_SER_FILE, serFileId));

		BasicDBObject suiteQuery = new BasicDBObject().append(
				TestDataManagerDefaults.KEY_SUITE_ID, suiteId);

		client.getDB(dbId)
				.getCollection(
						TestDataManagerDefaults.COLLECTION_SUITE_METADATA)
				.update(suiteQuery, coverageObject);

		return merged;
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

	public ArrayEncodedProbBackoffLm<String> computeModel(String dbId,
			String suiteId, int maxOrder) {
		List<TestCase> tests = getTestCasesInSuite(dbId, suiteId);

		// Set up WordIndexer
		final StringWordIndexer wordIndexer = new StringWordIndexer();
		wordIndexer.setStartSymbol(ArpaLmReader.START_SYMBOL);
		wordIndexer.setEndSymbol(ArpaLmReader.END_SYMBOL);
		wordIndexer.setUnkSymbol(ArpaLmReader.UNK_SYMBOL);

		// Set up config options
		ConfigOptions opts = new ConfigOptions();

		// Set up iterable over TestCases
		final Iterable<String> events = Iterators.able(new TestCaseIterator(
				tests));

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
		modelObject.append("$set", new BasicDBObject().append(
				TestDataManagerDefaults.KEY_MODEL, jsonArpa));

		BasicDBObject suiteQuery = new BasicDBObject().append(
				TestDataManagerDefaults.KEY_SUITE_ID, suiteId);

		client.getDB(dbId)
				.getCollection(
						TestDataManagerDefaults.COLLECTION_SUITE_METADATA)
				.update(suiteQuery, modelObject);

		return getNgramModel(dbId, suiteId, TestDataManagerDefaults.KEY_MODEL);
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
		String test = candidates.get(random);
		return test;
	}



	public static String generateId(String prefix) {
		Date date = new Date();
		String id = prefix + date.getTime();
		return id;
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

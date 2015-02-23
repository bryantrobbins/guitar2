import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import edu.umd.cs.guitar.artifacts.ArtifactCategory;
import edu.umd.cs.guitar.artifacts.ArtifactProcessor;
import edu.umd.cs.guitar.main.TestDataManager;
import edu.umd.cs.guitar.main.TestDataManagerCollections;
import edu.umd.cs.guitar.main.TestDataManagerKeys;
import edu.umd.cs.guitar.util.MongoUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestDataManagerTest {

    private TestDataManager tdm;
    private MongoClient client;

    @Before
    public void prepareClients() throws UnknownHostException {
        tdm = new TestDataManager("localhost", "37017");
        client = new MongoClient("localhost", 37017);
    }

    @Test
    public void testGenerateId() throws Exception {
        Set<String> ids = new HashSet<String>();

        for (int i = 0; i < 10000; i++) {
            String id = tdm.generateId();
            ids.add(id);
        }

        Assert.assertEquals(10000, ids.size());
    }

    @Test
    public void testCreateNewTest() throws Exception {
        String dbId = tdm.getDBId();
        String testId = tdm.createNewTest();

        Assert.assertTrue(MongoUtils.isItemInCollection(client.getDB(dbId),
                TestDataManagerCollections.TESTS,
                new BasicDBObject(TestDataManagerKeys.TEST_ID, testId)));

        Assert.assertEquals(1, client.getDB(dbId).getCollection
                (TestDataManagerCollections.TESTS).getCount());
    }

    @Test
    public void testCreateNewSuite() throws Exception {
        String dbId = tdm.getDBId();
        String suiteId = tdm.createNewSuite();

        Assert.assertTrue(MongoUtils.isItemInCollection(client.getDB(dbId),
                TestDataManagerCollections.SUITES,
                new BasicDBObject(TestDataManagerKeys.SUITE_ID, suiteId)));

        Assert.assertEquals(1, client.getDB(dbId).getCollection
                (TestDataManagerCollections.SUITES).getCount());
    }

    @Test
    public void testSaveArtifact() throws Exception {
        String dbId = tdm.getDBId();
        String testId = tdm.createNewTest();

        TestProcessor testProcessor = new TestProcessor();
        Map<String, String> options = new HashMap<String, String>();

        String artifactId = tdm.saveArtifact(ArtifactCategory.TEST_INPUT,
                testProcessor, options, testId);

        Assert.assertTrue(MongoUtils.isItemInCollection(client.getDB(dbId),
                TestDataManagerCollections.ARTIFACTS,
                new BasicDBObject().append(TestDataManagerKeys.ARTIFACT_ID,
                        artifactId)
                        .append(TestDataManagerKeys.ARTIFACT_CATEGORY,
                                ArtifactCategory.TEST_INPUT.getKey())
                        .append(TestDataManagerKeys.ARTIFACT_TYPE,
                                testProcessor.getKey())
                        .append(TestDataManagerKeys.ARTIFACT_OWNER_ID, testId)
                        .append(TestDataManagerKeys.ARTIFACT_DATA,
                                testProcessor.getMagicString())));
    }

    @Test
    public void testSaveAndGetArtifact() throws Exception {
        String testId = tdm.createNewTest();

        TestProcessor testProcessor = new TestProcessor();
        Map<String, String> options = new HashMap<String, String>();

        String artifactId = tdm.saveArtifact(ArtifactCategory.TEST_INPUT,
                testProcessor, options, testId);

        String text = (String) tdm.getArtifactById(artifactId, testProcessor);
        Assert.assertEquals(testProcessor.getMagicString(), text);

        text = (String) tdm.getArtifactByCategoryAndOwnerId(ArtifactCategory
                        .TEST_INPUT,
                testId, testProcessor);

        Assert.assertEquals(testProcessor.getMagicString(), text);
    }

    /**
     * A stub for testing ArtifactProcessor methods.
     */
    public class TestProcessor implements ArtifactProcessor<String> {

        public String getMagicString() {
            return "testJson";
        }

        @Override
        public String jsonFromOptions(Map<String, String> options) {
            return getMagicString();
        }

        @Override
        public String jsonFromObject(Object o){
            return getMagicString();
        }

        @Override
        public String objectFromJson(String json) {
            return getMagicString();
        }

        @Override
        public String objectFromOptions(Map<String, String> options) {
            return null;
        }

        @Override
        public String getKey() {
            return "testKey";
        }

        @Override
        public Iterator<String> getIterator(List<String> objectList) {
            return null;
        }
    }

}
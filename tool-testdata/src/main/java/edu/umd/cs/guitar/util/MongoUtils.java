package edu.umd.cs.guitar.util;


import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.Map.Entry;


/**
 * This class provides utility methods for dealing with MongoDB.
 */
public final class MongoUtils {

    /**
     * This constructor hides the default public constructor for this utility
     * class.
     */
    private MongoUtils() {

    }

    /**
     * Provide a log4j logger.
     */
    private static Logger logger = LogManager.getLogger(MongoUtils.class);

    /**
     * Create a MongoDB client given a host and port.
     *
     * @param host hostname where MongoDB instance is running
     * @param port port on host where MongoDB instance is running
     * @return MongoClient for connecting to the provided MongoDB instance
     */
    public static MongoClient createMongoClient(final String host,
                                                final String port) {

        int iPort = Integer.parseInt(port);
        MongoClient mc = null;
        try {
            mc = new MongoClient(host, iPort);
        } catch (UnknownHostException e) {
            logger.error("Cannot conect to MongoDB at " + host + ":" + port, e);
        }

        return mc;
    }

    /**
     * Remove an item from the given DB and collection with the given key and
     * value (creating a BasicDBObject based on the key and value).
     *
     * @param db           the MongoDB DB to connect to
     * @param collectionId the collection to remove from
     * @param key          the key to search for
     * @param value        the value to search for
     * @return true if removal succeed; otherwise false
     */
    public static boolean removeItemFromCollection(final DB db,
                                                   final String collectionId,
                                                   final String key,
                                                   final String value) {
        WriteResult wr = db.getCollection(collectionId).remove(
                new BasicDBObject(key, value));

        if (!wr.getLastError().ok()) {
            logger.error("Error while removing item (" + key + "," + value
                    + ")" + " from collection " + collectionId, wr
                    .getLastError().getException());
            return false;
        }

        return true;
    }

    /**
     * Remove all items from a collection.
     *
     * @param db           the MongoDB DB to connect to
     * @param collectionId the collection to remove all items from
     * @return true if remove operation successful; otherwise false
     */
    public static boolean removeAllItemsFromCollection(final DB db,
                                                       final String
                                                               collectionId) {
        WriteResult wr = db.getCollection(collectionId).remove(
                new BasicDBObject());

        if (!wr.getLastError().ok()) {
            logger.error("Error while removing all items from collection "
                    + collectionId, wr.getLastError().getException());
            return false;
        }

        return true;
    }

    /**
     * Test whether a given object is present in a DB and collection.
     *
     * @param db           the MongoDB DB to connect to
     * @param collectionId the collection to search
     * @param item         the DBObject to search for
     * @return true if item is in collection; otherwise false
     */
    public static boolean isItemInCollection(final DB db,
                                             final String collectionId,
                                             final DBObject item) {

        DBCollection items = db.getCollection(collectionId);

        DBCursor curs = items.find();

        while (curs.hasNext()) {
            DBObject next = curs.next();
            if (compareItemsLeft(item, next)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Helper method to compare two items.
     *
     * @param a item to be compared
     * @param b item to be compared
     * @return true if the items match
     */
    private static boolean compareItemsLeft(final DBObject a,
                                            final DBObject b) {
        Map<String, String> aMap = a.toMap();
        Map<String, String> bMap = b.toMap();
        for (Entry<String, String> e : aMap.entrySet()) {
            String seeking = bMap.get(e.getKey());

            if (seeking == null) {
                return false;
            }

            if (!seeking.equals(e.getValue())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Add an item to a given DB instance and collection.
     *
     * @param db         the MongoDB DB to connect to
     * @param collection the collection to add an item to
     * @param item       the item to be added
     * @return true if the insert was successful; otherwise false.
     */
    public static boolean addItemToCollection(final DB db,
                                              final String collection,
                                              final BasicDBObject item) {
        DBCollection testsCollection = db.getCollection(collection);
        WriteResult wr = testsCollection.insert(item);

        if (!wr.getLastError().ok()) {
            logger.error("Error while adding item " + item + " to collection "
                    + collection, wr.getLastError().getException());
            return false;
        }

        return true;

    }

}

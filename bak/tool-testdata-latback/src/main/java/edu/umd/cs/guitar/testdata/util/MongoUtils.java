package edu.umd.cs.guitar.testdata.util;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

public class MongoUtils {

	private static Logger logger = LogManager.getLogger(MongoUtils.class);

	public static MongoClient createMongoClient(String host, String port) {

		int iPort = Integer.parseInt(port);
		MongoClient mc = null;
		try {
			mc = new MongoClient(host, iPort);
		} catch (UnknownHostException e) {
			logger.error("Cannot conect to MongoDB at " + host + ":" + port, e);
		}

		return mc;
	}

	public static boolean removeItemFromCollection(DB db, String collectionId,
			String key, String value) {
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

	public static boolean removeAllItemsFromCollection(DB db,
			String collectionId) {
		WriteResult wr = db.getCollection(collectionId).remove(
				new BasicDBObject());

		if (!wr.getLastError().ok()) {
			logger.error("Error while removing all items from collection "
					+ collectionId, wr.getLastError().getException());
			return false;
		}

		return true;
	}

	public static boolean isItemInCollection(DB db, String collectionId,
			DBObject item) {

		DBCollection items = db.getCollection(collectionId);

		DBCursor curs = items.find();

		while (curs.hasNext()) {
			DBObject next = curs.next();
			if(compareItemsLeft(item, next)){
				return true;
			}
		}

		return false;
	}

	private static boolean compareItemsLeft(DBObject a, DBObject b) {
		Map<String, String> aMap = a.toMap();
		Map<String, String> bMap = b.toMap();
		for (Entry<String, String> e : aMap.entrySet()) {
			String seeking = bMap.get(e.getKey());
			
			if (seeking == null){
				return false;
			}
			
			if (!seeking.equals(e.getValue())) {
				return false;
			}
		}
		
		return true;
	}

	public static boolean addItemToCollection(DB db, String collection,
			BasicDBObject item) {
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

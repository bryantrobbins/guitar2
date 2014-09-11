package edu.umd.cs.guitar.testdata.weblog

import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.guitar.testdata.Analyzer
import edu.umd.cs.guitar.testdata.TestDataManager
import edu.umd.cs.guitar.testdata.util.BerkeleyLMUtils

import com.google.gson.Gson

import edu.berkeley.nlp.lm.ArrayEncodedProbBackoffLm;
import edu.berkeley.nlp.lm.collections.Iterators;
import groovy.io.FileType

import com.mongodb.BasicDBObject
import com.mongodb.DBCollection
import com.mongodb.MongoClient

class InitData {
	private ConfigData config
	private TestDataManager loader
	private Analyzer analyzer
	private String dbId
	private Map sizeMap
	
	// Need an id for the global suite for all sessions
	private static String globalSuite = "global_suite"


	private class ConfigData {
		private List sessions
		private List groups
		private List slicedSessions
		private Map schemes

		public ConfigData(sessions, groups, schemes, slicedSessions){
			this.sessions = sessions
			this.groups = groups
			this.schemes = schemes
			this.slicedSessions = slicedSessions
		}

		public List getSessions(){
			return sessions
		}

		public List getGroups(){
			return groups
		}

		public List getSlicedSessions(){
			return slicedSessions
		}

		public Map getSchemes(){
			return schemes
		}
	}

	public Map getSchemes(){
		return config.getSchemes();
	}

	public InitData(String host, int port, String dbId){
		this.dbId = dbId
		this.config = new ConfigData([], [], [:], [])
		this.loader = new TestDataManager(host, port)
		this.analyzer = new Analyzer(dbId, host, port)
		this.sizeMap = [:]
	}

	public void loadDataFromDisk(File rootDir){
		// Need a unique key for each user session (test case)
		int nextId = 0
		
		config.getSchemes().put("DATE", [])
		config.getSchemes().put("USER", [])
		config.getSchemes().put("ROLE", [])
		config.getSchemes().put("BIG_USER", [])

		// Clear the global suite, in case this dbId is not unique
		loader.clearTestSuite(dbId, globalSuite)

		// Load Data
		rootDir.eachFileRecurse (FileType.FILES) { file ->
			def parts = file.toString().split(File.separator)

			def dateSuite = parts[1] + "_suite"
			def roleSuite = parts[2] + "_suite"
			def userSuite = parts[3].replace(".", "_") + "_suite"
			
			// Save any new suite IDs we may have encountered
			if(!config.getGroups().contains(dateSuite)){
				loader.clearTestSuite(dbId, dateSuite)
				loader.clearTestSuite(dbId, dateSuite +"_train")
				loader.clearTestSuite(dbId, dateSuite +"_test")
				config.getGroups().add(dateSuite)
				config.getSchemes().get("DATE").add(dateSuite)
			}

			if(!config.getGroups().contains(roleSuite)){
				loader.clearTestSuite(dbId, roleSuite)
				loader.clearTestSuite(dbId, roleSuite +"_train")
				loader.clearTestSuite(dbId, roleSuite +"_test")
				config.getGroups().add(roleSuite);
				config.getSchemes().get("ROLE").add(roleSuite)
			}

			if(!config.getGroups().contains(userSuite)){
				loader.clearTestSuite(dbId, userSuite)
				loader.clearTestSuite(dbId, userSuite +"_train")
				loader.clearTestSuite(dbId, userSuite +"_test")
				config.getGroups().add(userSuite);
				config.getSchemes().get("USER").add(userSuite)
			}

			String testId = "" + nextId
			
			// Save number of lines in file
			sizeMap.put(testId, file.readLines().size())

			// Save the session ID
			config.getSessions().add(testId)

			// Save the keyword file as a test case artifact to the DB
			loader.addArtifactToTest(dbId, testId, file.absolutePath, WeblogProcessor.class);

			// Associate the artifact with its role suite
			loader.addTestToSuite(dbId, testId, roleSuite);

			// Associate the artifact with its date suite
			loader.addTestToSuite(dbId, testId, dateSuite);

			// Associate the artifact with its user suite
			loader.addTestToSuite(dbId, testId, userSuite);

			// Associate the artifact with the global suite
			loader.addTestToSuite(dbId, testId, globalSuite);

			// Increment the unique ID
			nextId++
		}

		// Perform splits for training/test
		splitDataForScheme("ROLE")
		splitLargeDataForScheme("USER")

	}
	
	public void splitLargeDataForScheme(String scheme){
		List schemeList = this.config.getSchemes().get(scheme)
		List loopList = []

		println "There are ${schemeList.size()} groups in ${scheme}"
		// Have to do a deep copy of this list so that we can modify
		// schemeList as required in the body of the second loop
		// Not sure of a better way but this should work

		for(String category : schemeList){
			loopList.add(category)
		}

		for(String category : loopList ){
			List categorySessions = loader.getTestIdsInSuite(dbId, category)
			int nTotal = categorySessions.size()
			int nTrain = (int) Math.floor(0.9*nTotal)
			int nTest = nTotal - nTrain

                        
                       // println "nTotal ${nTotal} nTrain ${nTrain} nTest ${nTest}"

			// Determine total keyword count
			int sizeTotal = 0
			for(String session : categorySessions){
				sizeTotal += sizeMap.get(session)
			}
			
			//println "${category} has ${sizeTotal} keywords and ${nTotal} sessions"
			
			if(nTotal < 2){
				schemeList.remove(category)
			}
			else if(sizeTotal < 300){
				schemeList.remove(category)
			}
			else{
                         
				Collections.shuffle(categorySessions, new Random())

				categorySessions.eachWithIndex { sess, ix ->
					if(ix < nTrain){
						loader.addTestToSuite(dbId, sess, category + "_train")
					}
					else{
						loader.addTestToSuite(dbId, sess, category + "_test")
					}
				}
				
				println "${category} adds ${loader.getTestIdsInSuite(dbId, category + "_test").size()} test sessions"
		      	}
		}
		
		println "${scheme} has ${schemeList.size()} categories"
		for(String category : schemeList){
			println category
		
		}
	}

	public void splitDataForScheme(String scheme){
		List schemeList = this.config.getSchemes().get(scheme)
		List loopList = []

		println "There are ${schemeList.size()} groups in ${scheme}"
		// Have to do a deep copy of this list so that we can modify
		// schemeList as required in the body of the second loop
		// Not sure of a better way but this should work

		for(String category : schemeList){
			loopList.add(category)
		}

		for(String category : loopList ){
			List categorySessions = loader.getTestIdsInSuite(dbId, category)
			int nTotal = categorySessions.size()
			int nTrain = (int) Math.floor(0.9*nTotal)
			int nTest = nTotal - nTrain


                        println "nTotal ${nTotal} nTrain ${nTrain} nTest ${nTest}"
			// Determine total keyword count
			int sizeTotal = 0
			for(String session : categorySessions){
				sizeTotal += sizeMap.get(session)
			}
			
			//println "${category} has ${sizeTotal} keywords and ${nTotal} sessions"
			
			if(nTotal < 2){
				schemeList.remove(category)
			}
			else{
				Collections.shuffle(categorySessions, new Random())

				categorySessions.eachWithIndex { sess, ix ->
					if(ix < nTrain){
						loader.addTestToSuite(dbId, sess, category + "_train")
					}
					else{
						loader.addTestToSuite(dbId, sess, category + "_test")
					}
				}
				
				println "${category} adds ${loader.getTestIdsInSuite(dbId, category + "_test").size()} test sessions"
			}
		}
		
		println "${scheme} has ${schemeList.size()} categories"
		for(String category : schemeList){
			println category
			
		}
	}

	public void saveConfiguration(File saveLoc, host, port, db){
		Gson gson = new Gson()
		def data = gson.toJson(config)
		saveLoc.withWriter{ it << data }

		MongoClient client = new MongoClient(host, port);
		BasicDBObject doc = new BasicDBObject("id", db).append("data", data)

		DBCollection coll = client.getDB(db).getCollection("weblog_config_objects").insert(doc)
	}

	public static void main(args){
		/*
		 *  Arguments: 
		 *  args[0] : MongoDB host
		 *  args[1] : MongoDB port
		 *  args[2] : Database ID for Mongo
		 *  args[3] : Path to save config file
		 *  args[4] : root directory of Keyword files
		 */

		def host = args[0]
		int port = Integer.parseInt(args[1])
		def dbId = args[2]
		def config = new File(args[3])
		def rootDir = new File(args[4])

		InitData handler = new InitData(host, port, dbId);

		println "Loading data from location ${rootDir} into database ${dbId}"
		handler.loadDataFromDisk(rootDir)
		handler.saveConfiguration(config, host, port, dbId)
	}
}

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
			if(!groups.contains(dateSuite)){
				loader.clearTestSuite(dbId, dateSuite)
				loader.clearTestSuite(dbId, dateSuite +"_train")
				loader.clearTestSuite(dbId, dateSuite +"_test")
				config.getGroups().add(dateSuite)
				config.getSchemes().get("DATE").add(dateSuite)
			}

			if(!groups.contains(roleSuite)){
				loader.clearTestSuite(dbId, roleSuite)
				loader.clearTestSuite(dbId, roleSuite +"_train")
				loader.clearTestSuite(dbId, roleSuite +"_test")
				config.getGroups().add(roleSuite);
				config.getSchemes().get("ROLE").add(roleSuite)
			}

			if(!groups.contains(userSuite)){
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

	public void loadDataFromConfig(File configLoc){
		Gson gson = new Gson()

		configLoc.withReader {
			this.config = gson.fromJson(it.readLine(), ConfigData.class)
		}
	}

	public void eachSession(Closure clos){
		config.getSessions().each { clos(it) }
	}

	public void eachSessionInGroup(String group, Closure clos){
		def groupSessions = loader.getTestIdsInSuite(dbId, group)
		groupSessions.each { clos(it) }
	}

	public Analyzer getAnalyzer(){
		return analyzer
	}

	public List<String> getGroups(){
		return config.getGroups()
	}

	public List<String> getTrainingGroupsInScheme(String scheme){
		List ret = []

		for(String group : config.getSchemes().get(scheme)){
			ret.add(group + "_train")
		}

		return ret
	}

	public String getTestGroupFromTrainingGroup(String trainingGroup){
		List pieces = trainingGroup.split("_")[0..-2]
		String testGroup = ""
		for(String piece : pieces){
			testGroup += piece + "_"
		}

		return testGroup + "test"

	}

	public String getMatchingGroup(String sess, List groups){
		for(String group : groups){
			// We have test groups in the test suite, training groups in the training suite
			// Need to perform some conversion here to account for that
			String testGroup = getTestGroupFromTrainingGroup(group)
			if(loader.getTestIdsInSuite(dbId, testGroup).contains(sess)){
				return group
			}
		}

		return "NOGROUP"
	}

	public float categorizeStrings(String scheme, int modelOrder, List<String> stringsToCategorize, List<String> actualGroups, String method, float threshold, String modelChoice, boolean mine){
		int correct = 0
		int tp = 0
		int fp = 0
		float val_tp, val_fp = 0
		int incorrect = 0
		int total = 0
		List groups = getTrainingGroupsInScheme(scheme)
		//println scheme
		//println groups

		stringsToCategorize.eachWithIndex { str, ix ->

			String guess = "NOGROUP"
			
			String group = actualGroups.get(ix)
			
			if(method.equals("MULTI_STORED")){
				guess = getAnalyzer().categorizeStoredSequence(str, groups, modelOrder, WeblogProcessor.class, false)
			}
			else if(method.equals("MULTI_RAW")){
				guess = getAnalyzer().categorizeRawSequence(str, groups, modelOrder, WeblogProcessor.class, false)
			}
			else if(method.equals("BINARY_RAW")){
				guess = getAnalyzer().acceptRawSequenceGivenModel(str, modelChoice, modelOrder, WeblogProcessor.class, false, threshold)
				
			      	
		 		if(guess.equals("false") && group.equals("false")){
					println "True Positive guess= ${guess}, group= ${group}"
					tp++
				}

				if(guess.equals("true") && group.equals("false")){
					println "False Positive guess= ${guess}, group= ${group}"
					fp++

				}

				total++
			}

			
			
			if(method.equals("MULTI_RAW") || method.equals("MULTI_STORED")){
  
              			if(guess.equals(group)){
	 				correct++
					total++
					println "RIGHT: guess is ${guess} and group is ${group}"
				}
				else
				{		
					total++
			       	 	println "WRONG: guess is  ${guess} but is ${group}"
				}
		
			//	return(correct * 1.0) / total	
                    	}
		//	if(method.equals("BINARY_RAW")){
			
		}

               
		if(method.equals("BINARY_RAW")){
   			if(mine == false){
				val_tp = (tp * 1.0)/total
				val_fp = (fp * 1.0)/total
			  	println "True Positive ${val_tp} and False Positive ${val_fp}"
				//println "True positive rate ${val_tp}"
				return val_tp
			}
			else if (mine == true){
				val_fp = (fp * 1.0)/total
				//println "False positive  rate ${val_fp}"
				return val_fp
			}
		}
                else {
			println "Got ${correct} out of ${total}"
			return(correct * 1.0) / total
		}
	//	if(method.equals("BINARY_RAW")){
		//	println "True Positive - Got ${correct} out of ${total}"
		//	println "False Positive - Got ${incorrect} out of ${total}"
               // }
	//	return (correct * 1.0) / total
	}

	public float categorizeSessions(String scheme, int modelOrder){
		// Need to build list of actual groups
		def groups = getTrainingGroupsInScheme(scheme)
		List trials = []
		List actual = []

		for(String group : groups){
			String testGroup = getTestGroupFromTrainingGroup(group)
			eachSessionInGroup(testGroup){ sess ->
				actual.add(group)
				trials.add(sess)
			}
		}

		println "Testing ${trials.size()} sessions"

		return categorizeStrings(scheme, modelOrder, trials, actual, "MULTI_STORED", 0, null, false)
	}

	public float categorizeSessionNgramsAgainstOthers(String scheme, String myGroup, int modelOrder, int ngramOrder, float threshold, boolean mine){
		// Get ngrams of ngramOrder from global suite
		List ngramsToCategorize = []
		List ngramsActualGroups = []

		def groups = getTrainingGroupsInScheme(scheme)

		WeblogProcessor proc = WeblogProcessor.class.newInstance()

		if(!mine){
			for(String group : groups){
				if(!group.equals(myGroup)){
					String testGroup = getTestGroupFromTrainingGroup(group)

					// Add training data from other groups, as it was not used to build this model
					eachSessionInGroup(group){ sessId ->
						List<Object> aList = new ArrayList<Object>();
						aList.add(loader.getTestArtifact(dbId, sessId, proc));
						String line = proc.getIterator(aList).next();
						String[] words = line.split(" ")

						// Add the ngrams of the words array as something to be categorized
						for(int start = 0; start<words.length-ngramOrder; start++){
							int end = start + ngramOrder;
							String toAdd = ""
							for(int i = start; i<=end; i++){
								toAdd += words[i]
								if(i != end){
									toAdd += " "
								}
							}
							ngramsToCategorize.add(toAdd)
							ngramsActualGroups.add(false)
						}
					}
					// Add test data from other groups
					eachSessionInGroup(testGroup){ sessId ->
						List<Object> aList = new ArrayList<Object>();
						aList.add(loader.getTestArtifact(dbId, sessId, proc));
						String line = proc.getIterator(aList).next();
						String[] words = line.split(" ")

						// Add the ngrams of the words array as something to be categorized
						for(int start = 0; start<words.length-ngramOrder; start++){
							int end = start + ngramOrder;
							String toAdd = ""
							for(int i = start; i<=end; i++){
								toAdd += words[i]
								if(i != end){
									toAdd += " "
								}
							}
							ngramsToCategorize.add(toAdd)
							ngramsActualGroups.add(false)
						}
					}
				}
			}
		}

		else{
			// We can also add the test data from our own group
			eachSessionInGroup(myGroup){ sessId ->
				List<Object> aList = new ArrayList<Object>();
				aList.add(loader.getTestArtifact(dbId, sessId, proc));
				String line = proc.getIterator(aList).next();
				String[] words = line.split(" ")

				// Add the ngrams of the words array as something to be categorized
				for(int start = 0; start<words.length-ngramOrder; start++){
					int end = start + ngramOrder;
					String toAdd = ""
					for(int i = start; i<=end; i++){
						toAdd += words[i]
						if(i != end){
							toAdd += " "
						}
					}
					ngramsToCategorize.add(toAdd)
					ngramsActualGroups.add(true)
				}
			}
		}

		// Now we have the ngrams and their actual categories
		return categorizeStrings(scheme, modelOrder, ngramsToCategorize, ngramsActualGroups, "BINARY_RAW", threshold, myGroup, mine)
	}

	public void saveConfiguration(File saveLoc){
		Gson gson = new Gson()

		saveLoc.withWriter{ it << gson.toJson(config) }
	}

	public static void main(args){
		/*
		 *  Arguments: 
		 *  args[0] : MongoDB host
		 *  args[1] : MongoDB port
		 *  args[2] : Database ID for Mongo; if reset true, add timestamp tag and use as DB for saving data. If reset NOT true, use as DB directly
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
		handler.saveConfiguration(config)
	}
}

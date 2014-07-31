package edu.umd.cs.guitar.testdata;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.apache.http.client.ClientProtocolException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.umd.cs.guitar.testdata.jenkins.JenkinsClient;
import edu.umd.cs.guitar.testdata.jenkins.JenkinsJobResult;

public class JenkinsClientTests {

	private JenkinsClient client;

	@Before
	public void setup() throws FileNotFoundException, IOException {
		Properties prop = new Properties();
		prop.load(JenkinsClientTests.class
				.getResourceAsStream("/test.properties"));

		client = new JenkinsClient(prop.getProperty("host"),
				prop.getProperty("port"), prop.getProperty("path"),
				prop.getProperty("user"), prop.getProperty("pass"));

	}

	@Test
	public void testSubmitJob() throws ClientProtocolException, IOException {

		Map<String, String> params = new HashMap<String, String>();
		params.put("TEST_PARAM", "test12345");

		// The job must already exist on the Jenkins server
		client.submitJob("Test_Job", params);
	}

	@Test
	public void testSubmitJobNoParams() throws ClientProtocolException,
			IOException {

		// The job must already exist on the Jenkins server
		client.submitJob("Dummy_Job", null);
	}

	@Test
	public void testSubmitJobManyParams() throws ClientProtocolException,
			IOException {

		Map<String, String> params = new HashMap<String, String>();
		params.put("AUT_NAME", "JabRef");
		params.put("DB_ID", "bryan_1382203776252");
		params.put("SUITE_ID", "JabRef_suite_1382203776256");
		params.put("TEST_ID", "JabRef_suite_1382203776256");
		params.put("TEST_LENGTH", "20");
		
		// The job must already exist on the Jenkins server
		client.submitJob("Generate_Test", params);
	}
	
	
	
//	@Test
//	public void waitForJobs() throws IOException, InterruptedException, ExecutionException{
//
//		ExecutorService executor = Executors.newFixedThreadPool(10);
//		
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("AUT_NAME", "JabRef");
//		params.put("DB_ID", "bryan_1382203776252");
//		params.put("SUITE_ID", "JabRef_suite_1382203776256");
//		params.put("TEST_LENGTH", "20");
//		
//		ArrayList<FutureTask<JenkinsJobResult>> tasks = new ArrayList<FutureTask<JenkinsJobResult>>();
//		
//		for(int i=0; i<10; i++){
//			params.remove("TEST_ID");
//			params.put("TEST_ID", "JabRef_test_" + i);
//			FutureTask<JenkinsJobResult> task = client.submitJob("Generate_Test", params);
//			tasks.add(task);
//			executor.execute(task);
//			Thread.sleep(5000);
//		}
//
//		Boolean waiting = true;
//		while (waiting){
//			Boolean allDone = true;
//			for(FutureTask<JenkinsJobResult> task : tasks){
//				allDone = allDone && task.isDone();
//			}
//			
//			if(allDone){
//				waiting = false;
//			}
//			
//			Thread.sleep(20000);
//		}
//		
//		boolean success = true;
//		
//		for(FutureTask<JenkinsJobResult> task : tasks){
//			JenkinsJobResult res = task.get();
//			success = success && res.getResult();
//		}
//		
//		Assert.assertTrue(success);
//	}
	

}

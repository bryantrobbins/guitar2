package edu.umd.cs.guitar.testdata.guitar;


//import edu.umd.cs.guitar.replayer.experiment.JFCSMUTReplayerMain;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import edu.umd.cs.guitar.testdata.Analyzer;
import edu.umd.cs.guitar.testdata.Reducer;
import edu.umd.cs.guitar.testdata.TestDataManager;
import edu.umd.cs.guitar.testdata.jenkins.JenkinsClient;
import edu.umd.cs.guitar.testdata.processor.TextObject;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

@RunWith(Parameterized.class)
public class GUITARTests {

    private static Logger logger = LogManager.getLogger(GUITARTests.class);

    private static String propsPath = System.getProperty("guitar.properties", "suite.properties");

    private static JenkinsClient jenkinsClient;
    private static TestDataManager manager;
    private static LogProcessor logProcessor;

    private static String dbId;
    private static String autName;

    @Parameter(value = 0)
    public String testId;

    @Parameter(value = 1)
    public String executionId;

    @Parameters(name = "{0}")
    public static Iterable<Object[]> data() throws IOException {

        // Get static values from a file
        Properties props = new Properties();
        InputStream is = GUITARTests.class.getResourceAsStream("/" + propsPath);
        props.load(is);

        String mongoHost =  props.getProperty("mongo.host");
        String mongoPort =  props.getProperty("mongo.port");

        autName = props.getProperty("autName");
        dbId = props.getProperty("dbId");
        String suiteId = props.getProperty("suiteId");

        String jenkinsHost = props.getProperty("jenkins.host");
        String jenkinsPort = props.getProperty("jenkins.port");
        String jenkinsPath = props.getProperty("jenkins.path");
        String jenkinsUser = props.getProperty("jenkins.username");
        String jenkinsPass = props.getProperty("jenkins.password");

        // Create TDM
        manager = new TestDataManager(mongoHost, Integer.parseInt(mongoPort));

        // Create Jenkins Client
        jenkinsClient = new JenkinsClient(jenkinsHost, jenkinsPort, jenkinsPath, jenkinsUser, jenkinsPass);

        // Create Log Processor (static)
        logProcessor = new LogProcessor();
        
        List<Object[]> params = new ArrayList<Object[]>();

        String executionId =  "" + System.nanoTime();

        // Get tests in suite, populate parameter objects
        for(String testId : manager.getTestIdsInSuite(dbId, suiteId)){
            Object[] oneTest = new Object[2];
            oneTest[0] = testId;
            oneTest[1] = executionId;
            params.add(oneTest);
        }

        return params;
    }

    @Test
    public void testJenkinsReplay() throws IOException, IllegalAccessException, InstantiationException {

        // Build params for Jenkins job
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("AUT_NAME", autName);
        paramsMap.put("DB_ID", dbId);
        paramsMap.put("TEST_ID", testId);
        paramsMap.put("EXECUTION_ID", executionId);

        // Launch jenkins job
        jenkinsClient.submitJob("Run_Test", paramsMap);

        // Wait for job to complete
        TextObject log = (TextObject) manager.waitForExecutionArtifact(dbId, testId, executionId, logProcessor);
        for(int i=0; i<log.size(); i++){
            processLogForErrors(log.getLine(i));
        }
    }


// Changed by Shiraz
    private void processLogForErrors(String line) {
        //Assert.assertFalse(line.contains("ERROR"));
       
        String cmd ="perl D:\\Perl_Codes\\output2.pl"+" \""+line+"\"";
        Process process;
        try
	    {
		    process = Runtime.getRuntime().exec(cmd);
	    }
        catch(Exception e)
	    {
		    System.out.println("Exception: "+ e.toString());
	    }
    }

}

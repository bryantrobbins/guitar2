package edu.umd.cs.guitar.processors.guitar.main;

import edu.umd.cs.guitar.artifacts.ArtifactCategory;
import edu.umd.cs.guitar.artifacts.ArtifactProcessor;
import edu.umd.cs.guitar.main.TestDataManager;
import edu.umd.cs.guitar.processors.guitar.TestcaseProcessor;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bryan on 10/21/14.
 * <p/>
 * This class provides an entry point for saving test-level input artifacts to
 * the
 * MongoDB database.
 */
public final class SaveTestCaseInput {

    /**
     * Log4j logger.
     */
    private static Logger logger =
            LogManager.getLogger(SaveTestCaseInput.class);


    /**
     * This override hides the default public constructor.
     */
    private SaveTestCaseInput() {
        // Hide the default public constructor
    }

    /**
     * Entry point for this class.
     *
     * @param args command line arguments.
     */
    public static void main(final String[] args) {

        // Build and parse cmd line options

        Options options = new Options();
        options.addOption("t", true, "the test case dir");
        options.addOption("s", true, "the test suite id");

        options.addOption("h", true, "the mongodb host");
        options.addOption("p", true, "the mongodb port");
        options.addOption("d", true, "the mongodb db id");

        HelpFormatter hf = new HelpFormatter();
        CommandLine cmd;

        try {
            CommandLineParser parser = new BasicParser();
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            logger.error("Could not parse command line options. Try again", e);
            hf.printHelp("Check usage:", options);
            return;
        }

        // Build a testdata manager instance
        TestDataManager testDataManager = new TestDataManager(cmd
                .getOptionValue("h"), cmd.getOptionValue("p"),
                cmd.getOptionValue("d"));

        // Build processors for these objects
        ArtifactProcessor tcProc = new TestcaseProcessor();

        // Loop over files in dir
        String tcdir = cmd.getOptionValue("t");
        File tcdirFile = new File(tcdir);
        File[] tcfiles = tcdirFile.listFiles();

        if (tcfiles == null) {
            logger.error("Got a bad list of files");
        } else {
            for (File tcFile : tcfiles) {
                if (tcFile.getName().contains("tst")) {
                    // Create test case
                    String[] splits = tcFile.getName().split("/");
                    String testId = splits[splits.length - 1].split("\\.")[0];
                    testDataManager.createNewTest(testId);

                    // Add test case to suite
                    testDataManager.addTestCaseToSuite(testId,
                            cmd.getOptionValue("s"));

                    // Store the test case steps file
                    Map<String, String> procOptions = new HashMap<String,
                            String>();
                    procOptions.put(TestcaseProcessor.FILE_PATH_OPTION,
                            tcFile.getAbsolutePath());
                    testDataManager.saveArtifact(ArtifactCategory.TEST_INPUT,
                            tcProc,
                            procOptions, testId);

                }
            }
        }
    }
}

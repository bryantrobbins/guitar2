package edu.umd.cs.guitar.processors.guitar.main;

import edu.umd.cs.guitar.artifacts.ArtifactCategory;
import edu.umd.cs.guitar.artifacts.ArtifactProcessor;
import edu.umd.cs.guitar.main.TestDataManager;
import edu.umd.cs.guitar.processors.guitar.CoverageProcessor;
import edu.umd.cs.guitar.processors.guitar.LogProcessor;
import edu.umd.cs.guitar.processors.guitar.TestcaseProcessor;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bryan on 10/21/14.
 * <p/>
 * This class provides an entry point for saving test-level input artifacts to
 * the
 * MongoDB database.
 */
public final class SaveTestCaseOutput {

    /**
     * Log4j logger.
     */
    private static Logger logger =
            LogManager.getLogger(SaveTestCaseOutput.class);


    /**
     * This override hides the default public constructor.
     */
    private SaveTestCaseOutput() {
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
        options.addOption("e", true, "the test execution id");
        options.addOption("l", true, "the log file");
        options.addOption("c", true, "the coverage file");

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
        ArtifactProcessor coverageProcessor =
                new CoverageProcessor(testDataManager.getDb());
        ArtifactProcessor logProcessor =
                new LogProcessor();

        // Create test execution
        String executionId = cmd.getOptionValue("e");
        testDataManager.createNewExecution(executionId);

        // Store the coverage file
        Map<String, String> procOptions = new HashMap<String, String>();
        procOptions.put(TestcaseProcessor.FILE_PATH_OPTION,
                cmd.getOptionValue("c"));
        testDataManager.saveArtifact(ArtifactCategory.TEST_OUTPUT,
                coverageProcessor, procOptions, executionId);

        // Store the log file
        procOptions.put(TestcaseProcessor.FILE_PATH_OPTION,
                cmd.getOptionValue("l"));
        testDataManager.saveArtifact(ArtifactCategory.TEST_OUTPUT,
                logProcessor, procOptions, executionId);

    }
}

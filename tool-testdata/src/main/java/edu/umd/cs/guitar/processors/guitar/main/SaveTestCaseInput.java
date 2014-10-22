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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

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
     * A counter for test ids.
     */
    private static AtomicLong nextId;

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
        nextId.set(1);

        // Build and parse cmd line options

        Options options = new Options();
        options.addOption("t", true, "the test case file");
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

        // Create test case
        String testId = "" + nextId.getAndIncrement();
        testDataManager.createNewTest(testId);

        // Add test case to suite
        testDataManager.addTestCaseToSuite(testId, cmd.getOptionValue("s"));

        // Store the test case steps file
        Map<String, String> procOptions = new HashMap<String, String>();
        procOptions.put(TestcaseProcessor.FILE_PATH_OPTION,
                cmd.getOptionValue("t"));
        testDataManager.saveArtifact(ArtifactCategory.SUITE_INPUT, tcProc,
                procOptions, testId);
    }
}

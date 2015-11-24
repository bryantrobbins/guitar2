package edu.umd.cs.guitar.processors.guitar.main;

import edu.umd.cs.guitar.artifacts.ArtifactCategory;
import edu.umd.cs.guitar.artifacts.ArtifactProcessor;
import edu.umd.cs.guitar.main.TestDataManager;
import edu.umd.cs.guitar.processors.guitar.CoverageProcessor;
import edu.umd.cs.guitar.processors.guitar.EFGProcessor;
import edu.umd.cs.guitar.processors.guitar.GUIProcessor;
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
 * This class provides an entry point for saving suite-level artifacts to the
 * MongoDB database.
 */
public final class SaveSuiteInput {

    /**
     * Log4j logger.
     */
    private static Logger logger =
            LogManager.getLogger(SaveSuiteInput.class);

    /**
     * This override hides the default public constructor.
     */
    private SaveSuiteInput() {
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
        options.addOption("e", true, "the EFG file");
        options.addOption("g", true, "the GUI file");
        options.addOption("r", true, "the Ripper coverage file");
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
        ArtifactProcessor efgProc = new EFGProcessor(testDataManager.getDb());
        ArtifactProcessor guiProc = new GUIProcessor(testDataManager.getDb());
        ArtifactProcessor covProc =
                new CoverageProcessor(testDataManager.getDb());

        // Create test suite (reset contents if necessary)
        String suiteId = cmd.getOptionValue("s");
        testDataManager.createNewSuite(suiteId);

        // Store the EFG
        Map<String, String> procOptions = new HashMap<String, String>();
        procOptions.put(EFGProcessor.FILE_PATH_OPTION, cmd.getOptionValue("e"));
        testDataManager.saveArtifact(ArtifactCategory.SUITE_INPUT, efgProc,
                procOptions, suiteId);

        // Store the GUI
        procOptions = new HashMap<String, String>();
        procOptions.put(GUIProcessor.FILE_PATH_OPTION, cmd.getOptionValue("g"));
        testDataManager.saveArtifact(ArtifactCategory.SUITE_INPUT, guiProc,
                procOptions, suiteId);

        // Store the ripper coverage
        procOptions = new HashMap<String, String>();
        
        if(cmd.getOptionValue("r") != null) {
          procOptions.put(CoverageProcessor.FILE_PATH_OPTION,
            cmd.getOptionValue("r"));
          testDataManager.saveArtifact(ArtifactCategory.SUITE_INPUT, covProc,
            procOptions, suiteId);
        }
    }
}

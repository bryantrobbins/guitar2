package edu.umd.cs.guitar.processors.applog;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by bryan on 4/5/14.
 * <p/>
 * This class captures the lines of text file as a very simple Java Object.
 */
public class TextObject {

    /**
     * A log4j logger.
     */
    private static Logger logger = LogManager.getLogger(TextObject.class);

    /**
     * A little enum for a single test result.
     */
    public static enum TestResult {
        /**
         * Passing result.
         */
        PASS,

        /**
         * Failing because execution is missing.
         */
        EXECUTION_MISSING,

        /**
         * Failing because log is missing.
         */
        LOG_MISSING,

        /**
         * Failing because a component is disabled.
         */
        COMPONENT_DISABLED,

        /**
         * Failing because a component is not found.
         */
        COMPONENT_NOT_FOUND,

        /**
         * Failing because of a step timeout.
         */
        STEP_TIMEOUT,

        /**
         * Failing because of an error in the log.
         */
        ERROR_IN_LOG,

        /**
         * A top-level (Gradle) failure for any other reason.
         */
        FAIL,

        /**
         * Inconsistent result.
         */
        INCONSISTENT
    }

    /**
     * These are the lines of the represented text file.
     */
    private List<String> lines;


    /**
     * Default constructor constructs TextObject from List of lines from file.
     *
     * @param linesVal the lines
     */
    public TextObject(final List<String> linesVal) {
        this.lines = linesVal;
    }

    /**
     * Get the number of lines in this object.
     *
     * @return number of lines
     */
    public long size() {
        return lines.size();
    }

    /**
     * Gets a single line from a file based on index.
     *
     * @param lineNumber the line to be returned, 0-indexed
     * @return the line; or null if there was a problem
     */
    public String getLine(final int lineNumber) {
        return lines.get(lineNumber);
    }

    /**
     * Returns a new TextObject object given a path.
     *
     * @param path Path to the file
     * @return a new TextObject object or null if there was an error
     */
    public static TextObject getTextObjectFromFilePath(final String path) {
        TextObject ret = null;
        try {
            ret = new TextObject(FileUtils.readLines(new File(path)));
        } catch (IOException e) {
            logger.error("Error reading file when trying to create TextObject",
                    e);
        }
        return ret;
    }

    /**
     * Process the log text and return a codified result.
     *
     * @return the codified result
     */
    public TestResult computeResult() {
        Pattern pattern = Pattern.compile("(((?:[a-zA-Z]{3} \\d{1,2}, \\d{4,"
                + "4} \\d{1,2}:\\d{2}:\\d{2} (AM|PM) (\\(SEVERE\\)|\\"
                + "(ERROR\\))"
                + ").*\\r(?:(.*Exception.*(\\r.*)(\\tat.*\\r)+)))|("
                + "(?:[a-zA-Z]{3} \\d{1,2}, \\d{4,4} \\d{1,"
                + "2}:\\d{2}:\\d{2} (AM|PM) (\\(SEVERE\\)|\\(ERROR\\))).*))");

        for (int i = 0; i < this.size(); i++) {
            String line = this.getLine(i);
            if (line.toUpperCase().contains("COMPONENTDISABLED")) {
                return TestResult.COMPONENT_DISABLED;
            }
            if (line.toUpperCase().contains("COMPONENTNOTFOUND")) {
                return TestResult.COMPONENT_NOT_FOUND;
            }
            if (line.toUpperCase().contains("STEP TIMER: TIMEOUT!!!")) {
                return TestResult.STEP_TIMEOUT;
            }
            if (pattern.matcher(line).matches()) {
                return TestResult.ERROR_IN_LOG;
            }
            if (line.toUpperCase().contains(":REPLAY FAILED")) {
                return TestResult.FAIL;
            }
        }

        return TestResult.PASS;
    }

    /**
     * Computes the number of steps successfully completed.
     * @return the number of completed steps
     */
    public int computeStepCount() {
        int stepCount = 1;
        for (int i = 0; i < this.size(); i++) {
            String line = this.getLine(i);
            if (line.contains("END STEP")) {
                stepCount++;
            }
        }
        return stepCount;
    }

}

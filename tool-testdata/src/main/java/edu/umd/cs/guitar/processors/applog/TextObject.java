package edu.umd.cs.guitar.processors.applog;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

}

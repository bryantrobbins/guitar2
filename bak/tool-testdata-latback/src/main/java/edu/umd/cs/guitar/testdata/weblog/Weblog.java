package edu.umd.cs.guitar.testdata.weblog;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Weblog {

    private List<String> lines;
    private static Logger logger = LogManager.getLogger(Weblog.class);

    public Weblog(String path) {
        try {
            this.lines = FileUtils.readLines(new File(path), "UTF-8");
        } catch (IOException e) {
            logger.fatal("Cannot read file at " + path, e);
        }
    }

    public List<String> getLines() {
        return lines;
    }
}

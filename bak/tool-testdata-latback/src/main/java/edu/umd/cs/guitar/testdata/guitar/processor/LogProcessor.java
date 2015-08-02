package edu.umd.cs.guitar.testdata.guitar.processor;

import edu.umd.cs.guitar.testdata.loader.GsonFileProcessor;
import edu.umd.cs.guitar.testdata.loader.TextObject;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by bryan on 4/5/14.
 */
public class LogProcessor extends GsonFileProcessor<TextObject> {

    public static String FILE_PATH_OPTION = "path";
    private static Logger logger = LogManager.getLogger(LogProcessor.class);

    public LogProcessor() {
        super(TextObject.class);
    }

    @Override
    public TextObject objectFromOptions(Map<String, String> options) {
        return TextObject.getTextObjectFromFile(options.get(FILE_PATH_OPTION));
    }

    @Override
    public String getKey() {
        return "replayLog";
    }

    @Override
    public Iterator<String> getIterator(List<Object> objectList) {
        return null;
    }
}
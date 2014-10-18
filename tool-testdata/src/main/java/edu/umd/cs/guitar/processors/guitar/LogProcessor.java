package edu.umd.cs.guitar.processors.guitar;

import edu.umd.cs.guitar.artifacts.GsonFileProcessor;
import edu.umd.cs.guitar.processors.log.TextObject;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A processor for generic text files, including log files
 * <p/>
 * Created by bryan on 4/5/14.
 */
public class LogProcessor extends GsonFileProcessor<TextObject> {

    /**
     * Log4j logger.
     */
    private static Logger logger = LogManager.getLogger(LogProcessor.class);

    /**
     * Simple constructor passing Gson serializable TextObject to superclass.
     */
    public LogProcessor() {
        super(TextObject.class);
    }

    @Override
    public TextObject objectFromOptions(final Map<String, String> options) {
        return TextObject.getTextObjectFromFilePath(
                options.get(FILE_PATH_OPTION));
    }

    @Override
    public String getKey() {
        return "replayLog";
    }

    @Override
    public Iterator<String> getIterator(final List<TextObject> objectList) {
        return null;
    }

}

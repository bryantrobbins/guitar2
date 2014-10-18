package edu.umd.cs.guitar.processors.guitar;

import edu.umd.cs.guitar.artifacts.GsonFileProcessor;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.util.GUITARUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A processor for GUITAR GUI files based on the GsonFileProcessor superclass.
 * <p/>
 * Created by bryan on 4/5/14.
 */
public class GUIProcessor extends GsonFileProcessor<GUIStructure> {

    /**
     * Log4j logger.
     */
    private static Logger logger = LogManager.getLogger(GUIProcessor.class);

    /**
     * Simple constructor passing Gson serializable GUIStructure into
     * superclass.
     */
    public GUIProcessor() {
        super(GUIStructure.class);
    }

    @Override
    public GUIStructure objectFromOptions(final Map<String, String> options) {
        return GUITARUtils.getGuiFromFile(options.get(FILE_PATH_OPTION));
    }

    @Override
    public String getKey() {
        return "replayLog";
    }

    @Override
    public Iterator<String> getIterator(final List<GUIStructure> objectList) {
        return null;
    }

}

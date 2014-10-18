package edu.umd.cs.guitar.processors.guitar;

import edu.umd.cs.guitar.artifacts.GsonFileProcessor;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.util.GUITARUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A processor for GUITAR EFG files based on the GsonFileProcessor superclass.
 * Created by bryan on 4/5/14.
 */
public class EFGProcessor extends GsonFileProcessor<EFG> {

    /**
     * Log4j logger.
     */
    private static Logger logger = LogManager.getLogger(EFGProcessor.class);

    /**
     * Simple constructor passes Gson serializable EFG to superclass.
     */
    public EFGProcessor() {
        super(EFG.class);
    }

    @Override
    public EFG objectFromOptions(final Map<String, String> options) {
        return GUITARUtils.getEfgFromFile(options.get(FILE_PATH_OPTION));
    }

    @Override
    public String getKey() {
        return "efgModel";
    }

    @Override
    public Iterator<String> getIterator(final List<EFG> objectList) {
        return null;
    }

}

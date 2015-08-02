package edu.umd.cs.guitar.testdata.guitar;

import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.testdata.loader.GsonProcessor;
import edu.umd.cs.guitar.testdata.util.GUITARUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Created by bryan on 4/5/14.
 */
public class EFGProcessor extends GsonProcessor<EFG> {


    private static Logger logger = LogManager.getLogger(EFGProcessor.class);

    public EFGProcessor() {
        super(EFG.class);
    }

    @Override
    public String getKey() {
        return "replayLog";
    }

    @Override
    public EFG getObjectFromFile(String path) {
        return GUITARUtils.getEfgFromFile(path);
    }
}
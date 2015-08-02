package edu.umd.cs.guitar.testdata.guitar;

import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.testdata.loader.GsonProcessor;
import edu.umd.cs.guitar.testdata.loader.TextObject;
import edu.umd.cs.guitar.testdata.util.GUITARUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Created by bryan on 4/5/14.
 */
public class GUIProcessor extends GsonProcessor<GUIStructure> {


    private static Logger logger = LogManager.getLogger(GUIProcessor.class);

    public GUIProcessor() {
        super(GUIStructure.class);
    }

    @Override
    public String getKey() {
        return "replayLog";
    }

    @Override
    public GUIStructure getObjectFromFile(String path) {
        return GUITARUtils.getGuiFromFile(path);
    }
}
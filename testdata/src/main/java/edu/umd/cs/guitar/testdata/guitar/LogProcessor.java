package edu.umd.cs.guitar.testdata.guitar;

import com.mongodb.DB;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;
import edu.umd.cs.guitar.testdata.TestDataManager;
import edu.umd.cs.guitar.testdata.TestDataManagerDefaults;
import edu.umd.cs.guitar.testdata.processor.ArtifactProcessor;
import edu.umd.cs.guitar.testdata.processor.GsonProcessor;
import edu.umd.cs.guitar.testdata.processor.TextObject;
import edu.umd.cs.guitar.testdata.util.CoberturaUtils;
import net.sourceforge.cobertura.coveragedata.ProjectData;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by bryan on 4/5/14.
 */
public class LogProcessor extends GsonProcessor<TextObject> {


    private static Logger logger = LogManager.getLogger(LogProcessor.class);

    public LogProcessor() {
        super(TextObject.class);
    }

    @Override
    public String getKey() {
        return "replayLog";
    }

    @Override
    public TextObject getObjectFromFile(String path) {
        return TextObject.getTextObjectFromFile(path);
    }
}
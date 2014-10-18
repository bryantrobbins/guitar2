package edu.umd.cs.guitar.processors.guitar;

import edu.umd.cs.guitar.artifacts.GridFSFileProcessor;
import edu.umd.cs.guitar.util.CoberturaUtils;
import net.sourceforge.cobertura.coveragedata.ProjectData;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * This class implements a processor for Cobertura coverage files
 * <p/>
 * Created by bryan on 4/5/14.
 */
public class CoverageProcessor extends GridFSFileProcessor<ProjectData> {


    /**
     * Log4j logger.
     */
    private static Logger logger = LogManager.getLogger(CoverageProcessor
            .class);

    /**
     * Constructor to initialize superclass.
     */
    public CoverageProcessor() {
        super(ProjectData.class);
    }

    @Override
    public ProjectData objectFromByteArray(final byte[] data) {
        try {
            return CoberturaUtils
                    .loadCoverageData(new ByteArrayInputStream(data));
        } catch (IOException e) {
            logger.error("Error loading coverage object from byte array", e);
            return null;
        }
    }

    @Override
    public String getKey() {
        return "cobertura";
    }

    @Override
    public Iterator<String> getIterator(final List<ProjectData> objectList) {
        return null;
    }
}

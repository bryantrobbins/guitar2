package edu.umd.cs.guitar.testdata.guitar;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;
import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.testdata.TestDataManager;
import edu.umd.cs.guitar.testdata.TestDataManagerDefaults;
import edu.umd.cs.guitar.testdata.loader.ArtifactProcessor;
import edu.umd.cs.guitar.testdata.loader.GsonProcessor;
import edu.umd.cs.guitar.testdata.util.CoberturaUtils;
import net.sourceforge.cobertura.coveragedata.ProjectData;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by bryan on 4/5/14.
 */
public class CoverageProcessor implements ArtifactProcessor<ProjectData> {


    private static Logger logger = LogManager.getLogger(CoverageProcessor.class);

    private DB db = null;

    @Override
    public String process(String path) throws Exception {
        // Now we need to handle the ser binary file from Cobertura
        // We will use GridFS from Mongo

        File serFileHandle = new File(path);
        String serFileId = TestDataManager.generateId("serFile_");

        GridFS gfsCoverage = new GridFS(db,
                TestDataManagerDefaults.COLLECTION_COVERAGE);
        GridFSInputFile gfsFile = null;
        try {
            gfsFile = gfsCoverage.createFile(serFileHandle);
        } catch (IOException e) {
            logger.error("Could not create file from " + serFileHandle
                    + " in GridFS.", e);
        }

        if (gfsFile == null) {
            return null;
        }

        gfsFile.setFilename(serFileId);
        gfsFile.save();

        return serFileId;
    }

    @Override
    public ProjectData objectify(String json) {
            ProjectData covObject;
            try {
                covObject = CoberturaUtils.getCoverageObjectFromGFS(
                        db, json);
            } catch (IOException e) {
                logger.error("Error retrieving coverage data from GridFS", e);
                return null;
            }

            return covObject;
    }

    @Override
    public String getKey() {
        return "coverage";
    }

    @Override
    public void setDB(DB db) {
        this.db = db;
    }

    @Override
    public Iterator<String> getIterator(List<Object> objs) {
        throw new UnsupportedOperationException("Iterator not supported for CoverageProcessor");
    }
}

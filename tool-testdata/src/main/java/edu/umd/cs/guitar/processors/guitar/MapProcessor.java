package edu.umd.cs.guitar.processors.guitar;

import com.mongodb.DB;
import edu.umd.cs.guitar.artifacts.GridFSFileProcessor;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.data.GUIMap;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.List;

/**
 * A processor for GUITAR GUI Map files based on the GsonFileProcessor superclass.
 * <p/>
 * Created by bryan on 9/27/2015.
 */
public class MapProcessor extends GridFSFileProcessor<GUIMap> {

    /**
     * Log4j logger.
     */
    private static Logger logger = LogManager.getLogger(
            MapProcessor.class);

    /**
     * Default constructor for this processor. We require a DB instance
     * because we need to know which instance should hold the associated
     * binary data.
     *
     * @param dbVal the DB instance to use for storage
     */
    public MapProcessor(final DB dbVal) {
        super(dbVal);
    }

    @Override
    public GUIMap objectFromByteArray(final byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        return (GUIMap) IO.readObjFromFile(bais, GUIMap.class);
    }

    @Override
    public byte[] byteArrayFromObject(final GUIMap object) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IO.writeObjToFile(object, baos);
        return baos.toByteArray();
    }

    @Override
    public String getKey() {
        return "GUIMap";
    }

    @Override
    public Iterator<String> getIterator(final List<GUIMap> objectList) {
        return null;
    }
}

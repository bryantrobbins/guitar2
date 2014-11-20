package edu.umd.cs.guitar.processors.guitar;

import com.mongodb.DB;
import edu.umd.cs.guitar.artifacts.GridFSFileProcessor;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.data.GUIStructure;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.List;

/**
 * A processor for GUITAR GUI files based on the GsonFileProcessor superclass.
 * <p/>
 * Created by bryan on 4/5/14.
 */
public class GUIProcessor extends GridFSFileProcessor<GUIStructure> {

    /**
     * Log4j logger.
     */
    private static Logger logger = LogManager.getLogger(
            GUIProcessor.class);

    /**
     * Default constructor for this processor. We require a DB instance
     * because we need to know which instance should hold the associated
     * binary data.
     *
     * @param dbVal the DB instance to use for storage
     */
    public GUIProcessor(final DB dbVal) {
        super(dbVal);
    }

    @Override
    public GUIStructure objectFromByteArray(final byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        return (GUIStructure) IO.readObjFromFile(bais, GUIStructure.class);
    }

    @Override
    public byte[] byteArrayFromObject(final GUIStructure object) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IO.writeObjToFile(object, baos);
        return baos.toByteArray();
    }

    @Override
    public String getKey() {
        return "GUIStructure";
    }

    @Override
    public Iterator<String> getIterator(final List<GUIStructure> objectList) {
        return null;
    }
}

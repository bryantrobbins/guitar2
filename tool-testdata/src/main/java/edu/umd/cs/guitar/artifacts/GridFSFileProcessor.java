package edu.umd.cs.guitar.artifacts;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mongodb.DB;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by bryan on 4/5/14.
 * <p/>
 * This processor loads a binary file from the file system into the GridFS
 * filesystem of MongoDB (as binary data). This requires creating a unique ID
 * for the binary object and associating this ID with the corresponding entry
 * in the regular (JSON) document store.
 *
 * @param <T> The type being serialized by this instance
 */
public abstract class GridFSFileProcessor<T> implements ArtifactProcessor<T> {

    /**
     * Key for file path option in options map.
     */
    public static final String FILE_PATH_OPTION = "path";


    /**
     * Key for GridFS handle in json object.
     */
    public static final String GRID_FS_KEY = "gridKey";

    /**
     * Name of collection for binary objects.
     */
    public static final String GRID_BINARY_COLLECTION = "binary";

    /**
     * Logger instance for this class.
     */
    private static Logger logger =
            LogManager.getLogger(GridFSFileProcessor.class);


    /**
     * The DB connection to use for storing the object.
     */
    private DB db;

    /**
     * A parser for JSON objects.
     */
    private JsonParser parser = new JsonParser();

    /**
     * A static set of unique IDs.
     */
    private static final AtomicLong ID = new AtomicLong();

    /**
     * Default constructor for this processor. We require a DB instance
     * because we need to know which instance should hold the associated
     * binary data.
     *
     * @param dbVal the DB instance to use for storage
     */
    public GridFSFileProcessor(final DB dbVal) {
        db = dbVal;
    }

    @Override
    public final T objectFromJson(final String json) {
        // Parse the handle out of the json
        JsonElement je = parser.parse(json);
        String handle = je.getAsJsonObject().get(GRID_FS_KEY).getAsString();

        // GridFS object
        GridFS gfsCoverage = new GridFS(db, GRID_BINARY_COLLECTION);

        // Coverage object from GridFS
        GridFSDBFile binaryOutput = gfsCoverage.findOne(handle);

        // Prepare output stream
        ByteArrayOutputStream binaryStreamOut = new ByteArrayOutputStream();
        try {
            binaryOutput.writeTo(binaryStreamOut);
        } catch (IOException e) {
            logger.error("Error while trying to write to GridFS", e);
        }

        // Convert to desired object
        return objectFromByteArray(binaryStreamOut.toByteArray());
    }

    /**
     * Produce the object from a Map of options.
     *
     * @param options Set of key/value mapping used as input to the process
     *                of constructing an artifact
     * @return the object
     */
    public final T objectFromOptions(final Map<String, String> options) {
        return objectFromJson(jsonFromOptions(options));
    }

    /**
     * This method needs to be overridden by the subclass to produce the
     * desired object from the raw binary byte data.
     *
     * @param data bytes of data from GridFS
     * @return the desired object
     */
    public abstract T objectFromByteArray(final byte[] data);

    @Override
    public final String jsonFromOptions(final Map<String, String> options) {
        File binaryFile = new File(options.get(FILE_PATH_OPTION));
        String binaryFileId = "binary_object_" + ID.incrementAndGet();

        GridFS binaryCollection = new GridFS(db, GRID_BINARY_COLLECTION);
        GridFSInputFile gfsFile = null;
        try {
            gfsFile = binaryCollection.createFile(binaryFile);
        } catch (IOException e) {
            logger.error("Could not create file from " + binaryFile
                    + " in GridFS.", e);
        }

        if (gfsFile == null) {
            return null;
        }

        gfsFile.setFilename(binaryFileId);
        gfsFile.save();

        return binaryFileId;
    }

    /**
     * Store the given object and return its associated json String.
     *
     * @param object the object to be saved
     * @return the ID of the binary file in GridFS (used as json string in
     * primary MongoDB document store)
     */
    public final String jsonFromObject(final T object) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            oos = new ObjectOutputStream(baos);
        } catch (IOException e) {
            logger.error("Could not create output stream during json "
                    + "conversion", e);
        }
        try {
            if (oos == null) {
                return null;
            }
            oos.writeObject(object);
        } catch (IOException e) {
            logger.error("Could not write object to outputstream", e);
        }

        // Now we have an output stream to work with,
        // but GridFS needs an input stream!
        ByteArrayInputStream bais = new ByteArrayInputStream(baos
                .toByteArray());
        ObjectInputStream ois = null;

        try {
            ois = new ObjectInputStream(bais);
        } catch (IOException e) {
            logger.error("Error reading object from stream", e);
        }

        String binaryFileId = "binary_object_" + ID.incrementAndGet();
        GridFS binaryCollection = new GridFS(db, GRID_BINARY_COLLECTION);

        GridFSInputFile gfsFile = null;
        gfsFile = binaryCollection.createFile(ois);

        if (gfsFile == null) {
            return null;
        }

        gfsFile.setFilename(binaryFileId);
        gfsFile.save();

        return binaryFileId;
    }

}
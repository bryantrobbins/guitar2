package edu.umd.cs.guitar.artifacts;

import com.google.gson.Gson;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Created by bryan on 4/5/14.
 * <p/>
 * This processor loads a flat file from the file system and serializes
 * the file with GSON.
 *
 * @param <T> The type being serialized by this instance
 */
public abstract class GsonFileProcessor<T> implements ArtifactProcessor<T> {

    /**
     * Key for file path option in options map.
     */
    public static final String FILE_PATH_OPTION = "path";

    /**
     * Logger instance for this class.
     */
    private static Logger logger =
            LogManager.getLogger(GsonFileProcessor.class);
    /**
     * Serializer for GSON processing.
     */
    private Gson gson = new Gson();

    /**
     * The class to use when deserializing to an object.
     */
    private Class<T> objClass;

    /**
     * Constructor for this processor.
     *
     * @param objClassC the class being serialized/deserialized by this instance
     */
    public GsonFileProcessor(final Class<T> objClassC) {
        this.objClass = objClassC;
    }

    @Override
    public final T objectFromJson(final String json) {
        return gson.fromJson(json, objClass);
    }

    @Override
    public final String jsonFromOptions(final Map<String, String> options) {
        return gson.toJson(objectFromOptions(options));
    }

    /**
     * If needed, this provides the ability to convert an object to its Gson
     * string.
     *
     * @param object The object to be serialized
     * @return the json String
     */
    public final String jsonFromObject(final T object) {
        return gson.toJson(object);
    }

}

package edu.umd.cs.guitar.artifacts;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This interface defines the methods required to support the
 * serialization/deserialization of artifacts to and from a database. Because
 * TestDataManager uses JSON objects in a MongoDB database,
 * most of the methods revolve around serialization to and from JSON.
 *
 * @param <T> the class of objects being serialized by this instances
 */
public interface ArtifactProcessor<T> {

    /**
     * Key for index option in options map.
     */
    String INDEX_OPTION = "index";

    /**
     * This method provides conversion from options to a JSON string. The
     * options can specify, for example, a filename from which the object
     * can be loaded. Options available and their handling are left
     * completely to the implementer of this interface. No options are
     * strictly required.
     *
     * @param options Set of key/value mapping used as input to the process
     *                of constructing a JSON string for an artifact
     * @return the JSON string
     */
    String jsonFromOptions(final Map<String, String> options);

    /**
     * This method provides conversion from an Object to a JSON string.
     *
     * @param object The object to be converted
     * @return the JSON String
     */
    String jsonFromObject(final Object object);

    /**
     * This method provides conversion from a JSON string to an object.
     *
     * @param json the JSON string
     * @return the object
     */
    T objectFromJson(final String json);

    /**
     * This method converts a set of options to an object. The options could
     * specify, for example, a filename from which the object is loaded. No
     * options are strictly required, and need not match options for
     * serialization to JSON (though that may be convenient).
     *
     * @param options Set of key/value mapping used as input to the process
     *                of constructing an artifact
     * @return the object; or null if there was a problem
     */
    T objectFromOptions(final Map<String, String> options);

    /**
     * This method should return the key used to uniquely identify this
     * artifact from among all other artifact types.
     *
     * @return the unique key
     */
    String getKey();

    /**
     * This method returns an iterator over a ordered list of artifact
     * objects. This implementation is optional, and only needed for
     * artifacts which are used in N-gram models.
     *
     * @param objectList the list of objects
     * @return an iterator of String objects representing object list
     */
    Iterator<String> getIterator(final List<T> objectList);
}

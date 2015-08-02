package edu.umd.cs.guitar.testdata.loader;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface ArtifactProcessor<T> {
    public String jsonFromOptions(Map<String, String> options) throws Exception;

    public String jsonFromObject(T object);

    public T objectFromJson(String json);

    public T objectFromOptions(Map<String, String> options) throws IOException;

    public String getKey();

    public Iterator<String> getIterator(List<Object> objectList);
}
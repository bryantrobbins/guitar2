package edu.umd.cs.guitar.testdata.processor;

import com.mongodb.DB;

import java.util.Iterator;
import java.util.List;

public interface ArtifactProcessor<T> {
	public String process(String path) throws Exception;
	public T objectify(String json);
	public String getKey();
    public void setDB(DB db);
	public Iterator<String> getIterator(List<Object> objs);
}
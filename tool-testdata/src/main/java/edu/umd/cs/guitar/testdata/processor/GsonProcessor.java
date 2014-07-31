package edu.umd.cs.guitar.testdata.processor;

import com.google.gson.Gson;
import com.mongodb.DB;
import edu.umd.cs.guitar.testdata.weblog.Weblog;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by bryan on 4/5/14.
 */
public abstract class GsonProcessor<T> implements ArtifactProcessor<T> {


    private Gson gson = new Gson();
    private Class<T> objClass;

    public abstract T getObjectFromFile(String path);

    public GsonProcessor(Class<T> objClass){
        this.objClass = objClass;
    }

    @Override
    public String process(String path) throws IOException {
        T theObj = getObjectFromFile(path);
        return gson.toJson(theObj);
    }

    @Override
    public T objectify(String json) {
        return gson.fromJson(json, objClass);
    }

    @Override
    public void setDB(DB db) {
        return;
    }

    @Override
    public Iterator<String> getIterator(List<Object> objs) {
        throw new UnsupportedOperationException("Iterator not supported");
    }

}

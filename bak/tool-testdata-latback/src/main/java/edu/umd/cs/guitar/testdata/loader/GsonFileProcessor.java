package edu.umd.cs.guitar.testdata.loader;

import com.google.gson.Gson;

import java.util.Map;

/**
 * Created by bryan on 4/5/14.
 */
public abstract class GsonFileProcessor<T> implements ArtifactProcessor<T> {


    private Gson gson = new Gson();
    private Class<T> objClass;

    public GsonFileProcessor(Class<T> objClass) {
        this.objClass = objClass;
    }

    @Override
    public T objectFromJson(String json) {
        return gson.fromJson(json, objClass);
    }

    @Override
    public String jsonFromOptions(Map<String, String> options) throws Exception {
        return gson.toJson(objectFromOptions(options));
    }

    @Override
    public String jsonFromObject(T object) {
        return gson.toJson(object);
    }

}

package edu.umd.cs.guitar.artifacts;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GsonFileProcessorTest {

    @Test
    public void testObjectFromJson() throws Exception {
        String json = "{\"data\":[\"data0\",\"data1\",\"data2\"," +
                "\"data3\",\"data4\"]}";
        GsonTestProcessor proc = new GsonTestProcessor();
        Assert.assertEquals(new CustomObject(5), proc.objectFromJson(json));
    }

    @Test
    public void testJsonFromOptions() throws Exception {
        GsonTestProcessor proc = new GsonTestProcessor();

        Assert.assertEquals("{\"data\":[\"data0\",\"data1\",\"data2\"," +
                "\"data3\"]}", proc.jsonFromOptions(new HashMap<String,
                String>()));
    }

    @Test
    public void testJsonFromObject() throws Exception {
        CustomObject testObject = new CustomObject(5);
        GsonTestProcessor proc = new GsonTestProcessor();
        Assert.assertEquals("{\"data\":[\"data0\",\"data1\",\"data2\"," +
                "\"data3\",\"data4\"]}", proc.jsonFromObject(testObject));
    }

    @Test
    public void testJsonObjectBackAndForth() {
        CustomObject testObject = new CustomObject(100);
        GsonTestProcessor proc = new GsonTestProcessor();

        Assert.assertEquals(testObject, proc.objectFromJson(proc
                .jsonFromObject(testObject)));
    }

    private class CustomObject {
        public List<String> data = new ArrayList<String>();

        public CustomObject(int howMany) {
            for (int i = 0; i < howMany; i++) {
                data.add("data" + i);
            }
        }

        @Override
        public boolean equals(Object that) {
            if (!(that instanceof CustomObject)) {
                return false;
            }

            CustomObject thatObject = (CustomObject) that;

            if (!(thatObject.data.size() == this.data.size())) {
                return false;
            }

            for (int i = 0; i < this.data.size(); i++) {
                if (!this.data.get(i).equals(thatObject.data.get(i))) {
                    return false;
                }
            }

            return true;
        }
    }

    private class GsonTestProcessor extends
            GsonFileProcessor<CustomObject> {

        public GsonTestProcessor() {
            super(CustomObject.class);
        }

        @Override
        public CustomObject objectFromOptions(Map<String, String> options) {
            return new CustomObject(4);
        }

        @Override
        public String getKey() {
            return "testKey";
        }

        @Override
        public Iterator<String> getIterator(List<CustomObject> objectList) {
            return null;
        }
    }
}
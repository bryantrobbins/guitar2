package edu.umd.cs.guitar.artifacts;

import com.mongodb.DB;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GridFSFileProcessorTest {

    @Test
    public void testObjectFromJson() throws Exception {

    }

    @Test
    public void testObjectFromOptions() throws Exception {

    }

    @Test
    public void testObjectFromByteArray() throws Exception {


    }

    @Test
    public void testJsonFromOptions() throws Exception {

    }

    @Test
    public void testJsonFromObject() throws Exception {

    }

    private class CustomBinaryObject {
        public List<Byte> data = new ArrayList<Byte>();

        public CustomBinaryObject(int howMany) {
            for (int i = 0; i < howMany; i++) {
                String str = "data" + i;

                for (byte b : str.getBytes()) {
                    data.add(b);
                }
            }
        }

        @Override
        public boolean equals(Object that) {
            if (!(that instanceof CustomBinaryObject)) {
                return false;
            }

            CustomBinaryObject thatObject = (CustomBinaryObject) that;

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


    private class GridFSTestProcessor extends
            GridFSFileProcessor<CustomBinaryObject> {

        public GridFSTestProcessor(DB db) {
            super(db);
        }

        @Override
        public CustomBinaryObject objectFromByteArray(byte[] data) {
            return null;
        }

        @Override
        public String getKey() {
            return "testKey";
        }

        @Override
        public Iterator<String> getIterator(
                List<CustomBinaryObject> objectList) {

            return null;
        }
    }
}
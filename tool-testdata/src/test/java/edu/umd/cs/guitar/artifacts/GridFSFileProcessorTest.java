package edu.umd.cs.guitar.artifacts;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import edu.umd.cs.guitar.main.TestDataManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GridFSFileProcessorTest {

    private GridFSTestProcessor proc;
    private TestDataManager tdm;
    private MongoClient client;
    private GridFS gfsBinary;

    @Before
    public void prepareProcessor() throws UnknownHostException {
        tdm = new TestDataManager("localhost", "37017");
        client = new MongoClient("localhost", 37017);
        proc = new GridFSTestProcessor(tdm.getDb());

        gfsBinary = new GridFS(tdm.getDb(),
                GridFSTestProcessor.GRID_BINARY_COLLECTION);
    }

    @Test
    public void testObjectToAndFromJson() throws Exception {
        CustomBinaryObject object = new CustomBinaryObject(100);
        String json = proc.jsonFromObject(object);

        CustomBinaryObject savedObject = proc.objectFromJson(json);
        Assert.assertEquals("Objects do not match", object, savedObject);
    }

    @Test
    public void testObjectFromOptions() throws Exception {
        CustomBinaryObject object = new CustomBinaryObject(100);
        String path = writeOut(object);
        Map<String, String> options = new HashMap<String, String>();
        options.put(GridFSTestProcessor.FILE_PATH_OPTION, path);

        CustomBinaryObject savedObject = proc.objectFromOptions(options);

        Assert.assertEquals("Objects do not match", object, savedObject);
    }

    @Test
    public void testJsonFromOptions() throws Exception {
        CustomBinaryObject object = new CustomBinaryObject(100);
        String path = writeOut(object);
        Map<String, String> options = new HashMap<String, String>();
        options.put(GridFSTestProcessor.FILE_PATH_OPTION, path);
        String json = proc.jsonFromOptions(options);

        // Verify file created
        GridFSDBFile binaryOutput = gfsBinary.findOne(json);
        Assert.assertNotNull(binaryOutput);

        // Actually compare the data
        ByteArrayOutputStream binaryStreamOut = new ByteArrayOutputStream();
        binaryOutput.writeTo(binaryStreamOut);
        CustomBinaryObject savedObject = new CustomBinaryObject
                (binaryStreamOut.toByteArray());
        Assert.assertEquals("Objects do not match", object, savedObject);
    }

    @Test
    public void testJsonFromObject() throws Exception {
        CustomBinaryObject object = new CustomBinaryObject(100);
        String json = proc.jsonFromObject(object);

        // Verify file created
        GridFSDBFile binaryOutput = gfsBinary.findOne(json);
        Assert.assertNotNull(binaryOutput);

        // Actually compare the data
        ByteArrayOutputStream binaryStreamOut = new ByteArrayOutputStream();
        binaryOutput.writeTo(binaryStreamOut);
        CustomBinaryObject savedObject = new CustomBinaryObject
                (binaryStreamOut.toByteArray());
        Assert.assertEquals("Object data are not equal", object, savedObject);
    }

    private String writeOut(CustomBinaryObject object) throws IOException {
        File tempFile = File.createTempFile("temp_" + System.nanoTime(),
                ".dat");

        BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));

        for (byte b : object.data) {
            bw.write(b);
        }

        bw.close();

        return tempFile.getAbsolutePath();
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

        public CustomBinaryObject(byte[] bytes) {
            for (byte b : bytes) {
                data.add(b);
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
            return new CustomBinaryObject(data);
        }

        @Override
        public byte[] byteArrayFromObject(CustomBinaryObject object) {
            byte[] ret = new byte[object.data.size()];
            int index = 0;

            for (byte b : object.data) {
                ret[index] = b;
                index++;
            }

            return ret;
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
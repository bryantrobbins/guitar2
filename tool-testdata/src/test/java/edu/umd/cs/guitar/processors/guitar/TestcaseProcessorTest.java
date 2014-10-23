package edu.umd.cs.guitar.processors.guitar;

import edu.umd.cs.guitar.model.data.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TestcaseProcessorTest {

    @Test
    public void testObjectFromOptions() throws Exception {
        TestcaseProcessor proc = new TestcaseProcessor();

        Map<String, String> options = new HashMap<String, String>();
        options.put(TestcaseProcessor.FILE_PATH_OPTION,
                "tool-testdata/src/test/resources" +
                        "/t_e802842950_e103778092.tst");

        TestCase tc = proc.objectFromOptions(options);
        String json = proc.jsonFromObject(tc);

        Assert.assertNotNull(tc);
        Assert.assertNotNull(json);
        System.out.println(json);
    }
}
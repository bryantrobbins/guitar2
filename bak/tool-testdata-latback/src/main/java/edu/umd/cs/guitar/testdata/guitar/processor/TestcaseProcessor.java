package edu.umd.cs.guitar.testdata.guitar.processor;

import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.testdata.loader.GsonFileProcessor;
import edu.umd.cs.guitar.testdata.util.GUITARUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by bryan on 4/5/14.
 */
public class TestcaseProcessor extends GsonFileProcessor<TestCase> {


    public static String FILE_PATH_OPTION = "path";
    private static Logger logger = LogManager.getLogger(TestcaseProcessor.class);

    public TestcaseProcessor() {
        super(TestCase.class);
    }

    @Override
    public TestCase objectFromOptions(Map<String, String> options) {
        return GUITARUtils.getTestCaseFromFile(options.get(FILE_PATH_OPTION));
    }

    @Override
    public String getKey() {
        return "testSteps";
    }

    @Override
    public Iterator<String> getIterator(List<Object> objectList) {
        return null;
    }
}
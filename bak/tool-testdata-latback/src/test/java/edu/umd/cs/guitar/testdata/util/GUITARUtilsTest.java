package edu.umd.cs.guitar.testdata.util;

import edu.umd.cs.guitar.model.data.TestCase;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class GUITARUtilsTest {

    @Test
    public void testGetTestCaseFromFile() throws Exception {
        TestCase random1 = GUITARUtils.getTestCaseFromFile("tool-testdata/src/test/resources/random-1.tst");
        Assert.assertEquals(20, random1.getStep().size());
    }

    @Test
    public void testGetMapFromFile() throws Exception {

    }

    @Test
    public void testGetGuiFromFile() throws Exception {

    }

    @Test
    public void testGetEfgFromFile() throws Exception {

    }

    @Test
    public void testGetEventIdsFromTest() throws Exception {

    }
}
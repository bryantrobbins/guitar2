package edu.umd.cs.guitar.processors.guitar;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import edu.umd.cs.guitar.main.TestDataManager;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.XMLHandler;
import edu.umd.cs.guitar.model.data.GUIStructure;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.DifferenceListener;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class GUIProcessorTest {

    private void xmlCompare(URL expected, URL actual) throws IOException,
            SAXException {
        String expectedString = Resources.toString(expected, Charsets.UTF_8);
        String actualString = Resources.toString(actual, Charsets.UTF_8);

        DifferenceListener mine = new
                IgnoreTimestampAndVersionDifferenceListener();
        Diff diff = new Diff(expectedString, actualString);
        diff.overrideDifferenceListener(mine);
        Assert.assertTrue("XML is not similar " + diff.toString(),
                diff.similar());
    }

    @Test
    public void testCompare() throws XPathExpressionException,
            ParserConfigurationException, IOException, SAXException {
        TestDataManager tdm = new TestDataManager("localhost", "37017");
        GUIProcessor gp = new GUIProcessor(tdm.getDb());

        Map<String, String> opts = new HashMap<String, String>();
        URL url = Resources.getResource("JabRef.GUI");
        opts.put(GUIProcessor.FILE_PATH_OPTION, url.getPath());

        GUIStructure gReadRaw = (GUIStructure) IO.readObjFromFile(url.getPath(),
                GUIStructure.class);
        IO.writeObjToFile(gReadRaw, "rawFile.xml");

        GUIStructure gReadProcessor = gp.objectFromOptions(opts);
        IO.writeObjToFile(gReadProcessor, "processorFile.xml");

        GUIStructure fromGson = gp.objectFromJson(gp.jsonFromObject(gReadRaw));
        XMLHandler handler = new XMLHandler();
        handler.writeObjToFile(fromGson, "gsonFile.xml");

        // Compare original file to file from GUITAR's read
        xmlCompare(url, new File("rawFile.xml").toURI().toURL());

        // Compare original to processed from options
        xmlCompare(url, new File("processorFile.xml").toURI().toURL());

        // Compare original to json-and-back object
        xmlCompare(url, new File("gsonFile.xml").toURI().toURL());
    }

    @Test
    public void testDocParsing() throws IOException, SAXException,
            ParserConfigurationException, XPathExpressionException {
        TestDataManager tdm = new TestDataManager("localhost", "37017");
        GUIProcessor gp = new GUIProcessor(tdm.getDb());

        Map<String, String> opts = new HashMap<String, String>();
        URL url = Resources.getResource("JabRef.GUI");
        opts.put(GUIProcessor.FILE_PATH_OPTION, url.getPath());

        GUIStructure guiStructure = gp.objectFromOptions(opts);
        System.out.println(guiStructure.getGUI().size());

        // Need to parse GUI Structure
        // Write GUIStructure to temp file
        File tempFile = File.createTempFile("temp_gui", ".dat");
        System.out.println(tempFile.getAbsolutePath());

        IO.writeObjToFile(guiStructure, tempFile.getAbsolutePath());

        DocumentBuilderFactory domFactory = DocumentBuilderFactory
                .newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();

        // Parse and store
        Document docGUI2 = builder.parse(tempFile.getAbsolutePath());
        Assert.assertNotNull(docGUI2.getDocumentElement());

        String sWidgetID = "w802842950";

        String xpathExpression =
                "/GUIStructure/GUI[Container//Property[Name=\""
                        + GUITARConstants.ID_TAG_NAME
                        + "\" and Value=\""
                        + sWidgetID
                        + "\"]]/Window/Attributes/Property[Name=\""
                        + GUITARConstants.TITLE_TAG_NAME +
                        "\"]/Value/text()";

        System.out.println(xpathExpression);

        XPath xpath = XPathFactory.newInstance().newXPath();

        XPathExpression expr = xpath.compile(xpathExpression);
        Object result = expr.evaluate(docGUI2, XPathConstants.NODESET);
        NodeList nodes = (NodeList) result;
        Assert.assertEquals(1, nodes.getLength());
        Assert.assertEquals("JabRef", nodes.item(0).getNodeValue());
    }
}
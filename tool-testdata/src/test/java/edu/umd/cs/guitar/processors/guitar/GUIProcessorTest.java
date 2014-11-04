package edu.umd.cs.guitar.processors.guitar;


import com.google.common.io.Resources;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.data.GUIStructure;
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

    @Test
    public void testDocParsing() throws IOException, SAXException,
            ParserConfigurationException, XPathExpressionException {

        GUIProcessor gp = new GUIProcessor();
        Map<String, String> opts = new HashMap<String, String>();
        URL url = Resources.getResource("JabRef.GUI");


        opts.put(GUIProcessor.FILE_PATH_OPTION, url.getPath());
        GUIStructure guiStructure = gp.objectFromOptions(opts);

        // Need to parse GUI Structure
        // Write GUIStructure to temp file
        File tempFile = File.createTempFile("temp_gui", ".dat");
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
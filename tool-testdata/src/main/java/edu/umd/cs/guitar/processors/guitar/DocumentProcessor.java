package edu.umd.cs.guitar.processors.guitar;

import edu.umd.cs.guitar.artifacts.GsonFileProcessor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A processor for GUITAR GUI files based on the GsonFileProcessor superclass.
 * <p/>
 * Created by bryan on 4/5/14.
 */
public class DocumentProcessor extends GsonFileProcessor<Document> {

    /**
     * Log4j logger.
     */
    private static Logger logger = LogManager.getLogger(DocumentProcessor
            .class);

    /**
     * Simple constructor passing Gson serializable GUIStructure into
     * superclass.
     */
    public DocumentProcessor() {
        super(Document.class);
    }

    @Override
    public Document objectFromOptions(final Map<String, String> options) {
        // Initialize Document gui object
        DocumentBuilderFactory domFactory = DocumentBuilderFactory
                .newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder;
        try {
            builder = domFactory.newDocumentBuilder();
            return builder.parse(options.get(FILE_PATH_OPTION));
        } catch (ParserConfigurationException e) {
            logger.error("Cannot parse GUI file", e);
            return null;
        } catch (SAXException e) {
            logger.error("Cannot parse GUI file", e);
            return null;
        } catch (IOException e) {
            logger.error("Cannot open file", e);
            return null;
        }
    }

    @Override
    public String getKey() {
        return "guiDocument";
    }

    @Override
    public Iterator<String> getIterator(final List<Document> objectList) {
        return null;
    }

}

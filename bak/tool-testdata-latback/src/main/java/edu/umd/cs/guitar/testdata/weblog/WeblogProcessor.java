package edu.umd.cs.guitar.testdata.weblog;

import edu.umd.cs.guitar.testdata.AbstractStringIterator;
import edu.umd.cs.guitar.testdata.loader.GsonFileProcessor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WeblogProcessor extends GsonFileProcessor<Weblog> {

    private static Logger logger = LogManager.getLogger(WeblogProcessor.class);
    public static String KEY = "weblog";
    public static String FILE_PATH_OPTION = "path";

    public WeblogProcessor() {
        super(Weblog.class);
    }

    @Override
    public Weblog objectFromOptions(Map<String, String> options) {
        return new Weblog(options.get(FILE_PATH_OPTION));
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public Iterator<String> getIterator(List<Object> logs) {
        return new WeblogIterator(logs);
    }

    private class WeblogIterator extends AbstractStringIterator {

        public WeblogIterator(List<Object> objs) {
            super(objs);
        }

        @Override
        public String getStringForObj(Object obj) {
            Weblog log = (Weblog) obj;
            if (!(obj instanceof Weblog)) {
                throw new RuntimeException("Object is not of correct type in WeblogIterator");
            }

            String ret = "";
            for (int i = 0; i < log.getLines().size(); i++) {
                ret += log.getLines().get(i);

                if (i != log.getLines().size() - 1) {
                    ret += " ";
                }
            }

            return ret;
        }
    }
}

package edu.umd.cs.guitar.testdata.loader;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by bryan on 4/5/14.
 */
public class TextObject {

    private static Logger logger = LogManager.getLogger(TextObject.class);
    private static Gson gson = new Gson();

    private List<String> lines;


    public TextObject(List<String> lines) {
        this.lines = lines;
    }

    public long size(){
        return lines.size();
    }

    public String getLine(int lineNumber){
        return lines.get(lineNumber);
    }

    public static TextObject getTextObjectFromFile(String path) {

        List<String> lines = null;

        try {
            lines = FileUtils.readLines(new File(path), "UTF-8");
        } catch (IOException e) {
            logger.error("Could not read file at path " + path, e);
        }

        return new TextObject(lines);
    }
}

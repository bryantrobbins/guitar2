package edu.umd.cs.guitar.testdata.weblog;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class Weblog {

	private List<String> lines;

	public Weblog(String path) throws IOException{
		this.lines = FileUtils.readLines(new File(path), "UTF-8");
	}
	
	public List<String> getLines() {
		return lines;
	}
}

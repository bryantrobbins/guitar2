/*	
 *  Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland. Names of owners of this group may
 *  be obtained by sending an e-mail to atif@cs.umd.edu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 *  documentation files (the "Software"), to deal in the Software without restriction, including without 
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *	the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
 *	conditions:
 * 
 *	The above copyright notice and this permission notice shall be included in all copies or substantial 
 *	portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *	LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *	EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *	IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *	THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
package edu.umd.cs.guitar.cobertura;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 * 
 */
public class SourceCodeTraceCollector extends ConsoleTraceCollector {
	DateFormat df = new SimpleDateFormat("HH:mm:ss:SS");
	/**
	 * 
	 */
	private static final String SOURCE_DIRS_FLAG = "edu.umd.cs.guitar.source";
	long count = 0;

	String previouslyReadFile = null;

	/**
	 * @param sourceFileName
	 * @param statement
	 * @param lineNumber
	 * @return
	 */
	@Override
	public String getOutput(String sourceFileName, int lineNumber) {
		
		// Looking up source code file
		File file = searchSouceFile(sourceFileName);

		if (file == null) {
			System.err.println(sourceFileName + " " + lineNumber
					+ ": Not available");
			return "";
		}

		String statement = "";
		try {
			BufferedReader reader;
			// read source code line
			FileReader fileReader = new FileReader(file);
			reader = new BufferedReader(fileReader);

			for (int i = 0; i < lineNumber; i++)
				statement = reader.readLine();

		} catch (Exception e) {
			e.printStackTrace();
		}

		// print file name if there is a change
		long time = System.currentTimeMillis();
		if (!sourceFileName.equals(previouslyReadFile)) {
			String output = "\n";
			output += ((df.format(time)) + "\t");
			output += ("*** " + sourceFileName);
			System.out.println(output);
		}
		previouslyReadFile = sourceFileName;

		String output = "";
		output += ((df.format(time)) + "\t");
		output += lineNumber + ": ";
		output += statement;
		return output;
	}

	/**
	 * @return
	 */
	private File searchSouceFile(String sourceFileName) {
		String sourceDirString = System.getProperty(SOURCE_DIRS_FLAG);
		if (sourceDirString == null)
			return null;
		String[] sourceDirList = sourceDirString.split(File.pathSeparator);
		for (String sourceDir : sourceDirList) {
			String fullPath = sourceDir + File.separator + sourceFileName;
			File file = new File(fullPath);
			if (file.exists()) {
				return file;
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.cobertura.GTraceCollector#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return System.getProperty(SOURCE_DIRS_FLAG) != null;
	}
}

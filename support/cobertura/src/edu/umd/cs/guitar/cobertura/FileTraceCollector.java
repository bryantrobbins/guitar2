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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 * 
 */
public class FileTraceCollector extends GTraceCollector {
	List<String> data;
	/**
	 * 
	 */
	private static final String TRACE_FILE_FLAG = "edu.umd.cs.guitar.tracefile";
	String outputFile = System.getProperty(TRACE_FILE_FLAG);

	/**
	 * 
	 */
	public FileTraceCollector() {
		data = new ArrayList<String>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.cobertura.GTraceCollector#cleanUp()
	 */
	@Override
	public void cleanUp() {
		String outputFile = System.getProperty(TRACE_FILE_FLAG);
		if (outputFile != null) {
			try {
				// Create file
				FileWriter fstream = new FileWriter(outputFile);
				BufferedWriter out = new BufferedWriter(fstream);
				for (String line : data)
					out.write(line + "\n");

				// Close the output stream
				out.close();
				System.out.println("GUITAR: Saved trace to: " + outputFile);
			} catch (Exception e) {// Catch exception if any
				System.err.println("GUITAR: Error writting trace");
				System.err.println("Error: " + e.getMessage());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.umd.cs.guitar.cobertura.GTraceCollector#collectTrace(java.lang.String
	 * , int)
	 */
	@Override
	public void collectTrace(String sourceFileName, int lineNumber) {
		String output = getOutput(sourceFileName, lineNumber);
		data.add(output);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.cobertura.GTraceCollector#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return (System.getProperty(TRACE_FILE_FLAG) != null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.cobertura.GTraceCollector#setUp()
	 */
	@Override
	public void setUp() {
		// TODO Auto-generated method stub

	}

}

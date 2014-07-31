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
package edu.umd.cs.guitar.smut.testcase;

import java.util.List;

import org.kohsuke.args4j.Option;

import edu.umd.cs.guitar.util.Util;

;

/**
 * Class contains the runtime configurations of JFC GUITAR Ripper
 * 
 * <p>
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 */
public class TestCaseGeneratorConf {
	@Option(name = "-?", usage = "print this help message", aliases = "--help")
	static protected boolean HELP;

	// GUITAR runtime parameters
	@Option(name = "-gs", usage = "input GUI STATE file path", aliases = "--gui-state")
	static public String GUI_FILE = null;

	@Option(name = "-gd", usage = "input GUI STATE directory", aliases = "--gui-state-dir")
	static public String GUI_DIR = null;

	// @Option(name = "-im", usage = "output GUI Map file path", aliases =
	// "--input-gui-map", required = true)
	// static public String IN_GUI_MAP_FILE = null;
	//
	// @Option(name = "-om", usage = "output GUI Map file path", aliases =
	// "--output-gui-map", required = true)
	// static public String OUT_GUI_MAP_FILE = null;

	@Option(name = "-l", usage = "test case length", aliases = "--length", required = true)
	static public int LENGTH = 2;

	@Option(name = "-d", usage = "output directory", aliases = "--output-dir", required = true)
	static public String OUTPUT_DIRECTORY;

	@Option(name = "-id", usage = "ignored test cases directory", aliases = "--ignored-dir", required = false)
	static public  List<String>  IGNORED_DIRECTORY_LIST;
}

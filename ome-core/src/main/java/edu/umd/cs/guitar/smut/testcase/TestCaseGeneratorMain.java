/*  
 *  Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland. Names of owners of this group may
 *  be obtained by sending an e-mail to atif@cs.umd.edu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 *  documentation files (the "Software"), to deal in the Software without restriction, including without 
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *  the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
 *  conditions:
 * 
 *  The above copyright notice and this permission notice shall be included in all copies or substantial 
 *  portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *  LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *  EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
package edu.umd.cs.guitar.smut.testcase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.GUIMap;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.model.wrapper.TestCaseWrapper;
import edu.umd.cs.guitar.smut.converter.OracleAnalyzer;

/**
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 * 
 */
public class TestCaseGeneratorMain {

	private static final String TESTCASE_EXT = "tst";
	private static String lIgnoredDir;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println("TestCaseGeneratorMain is starting...");
		TestCaseGeneratorConf conf = new TestCaseGeneratorConf();
		CmdLineParser parser = new CmdLineParser(conf);
		try {
			parser.parseArgument(args);
			// -------------------
			// Reading data

			String testcaseDir = conf.OUTPUT_DIRECTORY;
			(new File(testcaseDir)).mkdir();

			// 2. Generating test cases
			System.out.println("Generating test cases...");

			int length = conf.LENGTH;

			String sGUIFile = conf.GUI_FILE;
			String sGUIDir = conf.GUI_DIR;
			List<String> lIgnoredDir = conf.IGNORED_DIRECTORY_LIST;

			TestCaseGenerator generator = new TestCaseGenerator();
			System.out.println("Getting ignored test cases...");
			List<String> ignoredTestCaseNames = getIgnoredTestCases(lIgnoredDir);

			System.out.println("Total: " + ignoredTestCaseNames.size());

			if (sGUIFile != null) {
				TestCase oracle = (TestCase) IO.readObjFromFile(sGUIFile,
						TestCase.class);
				List<TestCase> testsuite = generator.genTestSuite(oracle,
						length);
				writeTestSuiteToFile(testsuite, testcaseDir, length,
						ignoredTestCaseNames);

			} else if (sGUIDir != null) {
				File inputDir = new File(sGUIDir);
				File[] files = inputDir.listFiles();

				for (int i = 0; i < files.length; i++) {
					if (i % 50 == 0) {
						System.out
								.println("-----------------------------------------------");
						System.out.println("File #:" + (i + 1) + "/"
								+ files.length);
					}
					try {
						String sFileInputPath = files[i].getAbsolutePath();

						TestCase oracle = (TestCase) IO.readObjFromFile(
								sFileInputPath, TestCase.class);
						List<TestCase> testsuite = generator.genTestSuite(
								oracle, length);
						writeTestSuiteToFile(testsuite, testcaseDir, length,
								ignoredTestCaseNames);

					} catch (Exception e) {
						System.err.println(e);
					}
				}

			}

		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			System.err.println();
			System.err.println("Usage: java [JVM options] "
					+ TestCaseGeneratorMain.class.getName()
					+ " [Relayer options] \n");
			System.err.println("where [Replayer options] include:");
			System.err.println();
			parser.printUsage(System.err);
		}
		System.exit(0);
	}

	/**
	 * @param lIgnoredDirList
	 * @return
	 */
	private static List<String> getIgnoredTestCases(List<String> lIgnoredDirList) {
		List<String> result = new ArrayList<String>();
		if (lIgnoredDirList == null)
			return result;

		for (String sIgnoredDir : lIgnoredDirList) {
			File inputDir = new File(sIgnoredDir);
			File[] files = inputDir.listFiles();
			for (File file : files) {
				result.add(file.getName());
			}
		}
		return result;
	}

	private static void writeTestSuiteToFile(List<TestCase> testsuite,
			String testcaseDir, int length, List<String> ignoredTestCaseNames) {
		for (TestCase testcase : testsuite) {
			String sTestCaseName = getName(testcase, length) + "."
					+ TESTCASE_EXT;

			if (ignoredTestCaseNames.contains(sTestCaseName)) {
				// System.out.println(sTestCaseName + " is ignored");
				continue;
			}

			String sTestCaseFullName = testcaseDir + File.separator
					+ sTestCaseName;

			File fTestCase = new File(sTestCaseFullName);

			if (!fTestCase.exists()) {
				System.out.println("Writting: " + sTestCaseFullName);
				IO.writeObjToFile(testcase, sTestCaseFullName);
			}
		}

	}

	private static String getName(TestCase testcase, int length) {
		String name = "t_";
		TestCaseWrapper wTestCase = new TestCaseWrapper(testcase);
		int endIndex = wTestCase.size();
		int startIndex = endIndex - length;
		List<String> interaction = wTestCase.subSequence(startIndex, endIndex);
		int i = 0;
		while (i < interaction.size() - 1) {
			name += interaction.get(i);
			name += "_";
			i++;
		}
		name += interaction.get(i);

		return name;
	}
}

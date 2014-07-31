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
package edu.umd.cs.guitar.smut.converter;

import java.io.File;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.GUIMap;
import edu.umd.cs.guitar.model.data.ObjectFactory;
import edu.umd.cs.guitar.model.data.TestCase;

/**
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 * 
 */
public class EFGSynthesizerMain {

	static ObjectFactory factory = new ObjectFactory();

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println("EFGSynthesizerMain is starting...");
		EFGSynthesizerConf conf = new EFGSynthesizerConf();
		CmdLineParser parser = new CmdLineParser(conf);
		try {
			parser.parseArgument(args);

			EFGSynthesizer synthesizer = new EFGSynthesizer();

			EFG inEFG = (EFG) IO.readObjFromFile(conf.IN_EFG_FILE, EFG.class);
			GUIMap inMap = (GUIMap) IO.readObjFromFile(conf.GUI_MAP_FILE,
					GUIMap.class);

			//
			System.out.println("Initializing EFG....");
			EFG efg = synthesizer.initializeEFG(inEFG, inMap);

			System.out.println("Updating EFG....");
			// Update EFG
			File inputDir = new File(conf.TESTCASE_DIR);
			File[] files = inputDir.listFiles();

			for (int i = 0; i < files.length; i++) {

				try {

					if (i % 50 == 0) {
						System.out
								.println("-----------------------------------------------");
						System.out.println("File #:" + (i + 1) + "/"
								+ files.length);
					}
					String sFileInputPath = files[i].getAbsolutePath();
					TestCase testcase = (TestCase) IO.readObjFromFile(
							sFileInputPath, TestCase.class);

					efg = synthesizer.updateEFG(efg, testcase, inMap);
				} catch (Exception e) {
					System.err.println(e);
				}
			}

			IO.writeObjToFile(efg, conf.OUT_EFG_FILE);

		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			System.err.println();
			System.err.println("Usage: java [JVM options] "
					+ EFGSynthesizerMain.class.getName()
					+ " [Relayer options] \n");
			System.err.println("where [Replayer options] include:");
			System.err.println();
			parser.printUsage(System.err);
		}
		System.exit(0);
	}

}

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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.EventGraphType;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.data.ObjectFactory;

/**
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 * 
 */
public class EFG2TypeMain {

	static ObjectFactory factory = new ObjectFactory();

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length < 2) {
			System.err.println("Missing parameter(s)");
			System.exit(1);
		}

		String sEFG_FILE_NAME = args[0];
		String sOUT_FILE = args[1];
		EFG efg = (EFG) IO.readObjFromFile(sEFG_FILE_NAME, EFG.class);

		FileWriter fstream;
		try {
			fstream = new FileWriter(sOUT_FILE);
			BufferedWriter out = new BufferedWriter(fstream);

			List<EventType> eventList = efg.getEvents().getEvent();

			for (EventType event : eventList) {
				String eID = event.getEventId();
				String eType = getType(event, efg);

				out.write(eID + "\t" + eType +"\n");
			}
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.exit(0);
	}

	interface EventTypeConst {
		public String System = "S";
		public String Reaching = "R";
	}

	private static String getType(EventType event, EFG efg) {
		EventGraphType eventGraph = efg.getEventGraph();
		List<EventType> eventList = efg.getEvents().getEvent();

		int eventGraphSize = eventList.size();
		Integer row = eventList.indexOf(event);

		for (int col = 0; col < eventGraphSize; col++) {
			int relation = eventGraph.getRow().get(row).getE().get(col);

			if (relation == GUITARConstants.REACHING_EDGE) {
				return EventTypeConst.Reaching;
			}
		}

		return EventTypeConst.System;
	}

}

/**
 * 
 */
package edu.umd.cs.guitar.cobertura;

import java.util.LinkedList;
import java.util.List;

/**
 * @author baonn
 * 
 */
public class TraceData {

	public static boolean isPrintToConsole() {
		return (System.getProperty("edu.cs.umd.guitar.console") != null);
	}

	public static String getOutputFile() {
		return System.getProperty("edu.cs.umd.guitar.tracefile");
	}

	public static boolean isWriteToFile() {
		return (System.getProperty("edu.cs.umd.guitar.tracefile") != null);
	}

	public static List<String> trace = new LinkedList<String>();

}

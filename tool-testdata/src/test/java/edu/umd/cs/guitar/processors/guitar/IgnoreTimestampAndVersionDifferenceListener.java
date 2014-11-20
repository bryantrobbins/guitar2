package edu.umd.cs.guitar.processors.guitar;

import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Node;

import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;

/*
 * This is based on the following SO discussion:
 * http://stackoverflow.com/questions/1241593/java-how-do-i-ignore-certain-elements-when-comparing-xml
 */

public class IgnoreTimestampAndVersionDifferenceListener implements
		DifferenceListener {
	private Set<String> blackList = new HashSet<String>();

	@Override
	public int differenceFound(Difference difference) {
		if (difference.getId() == DifferenceConstants.ATTR_VALUE_ID) {
			if (difference.getControlNodeDetail().getNode().getNodeName()
					.equals("timestamp")
					|| difference.getControlNodeDetail().getNode()
							.getNodeName().equals("version")) {
				return DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
			}
		}

		return DifferenceListener.RETURN_ACCEPT_DIFFERENCE;
	}

	@Override
	public void skippedComparison(Node node, Node node1) {

	}

}

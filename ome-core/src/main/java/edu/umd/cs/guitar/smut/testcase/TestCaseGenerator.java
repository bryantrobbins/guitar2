package edu.umd.cs.guitar.smut.testcase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.umd.cs.guitar.model.data.EventEffectSetType;
import edu.umd.cs.guitar.model.data.EventEffectType;
import edu.umd.cs.guitar.model.data.EventSetType;
import edu.umd.cs.guitar.model.data.EventTrace;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.data.GUIMap;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.ObjectFactory;
import edu.umd.cs.guitar.model.data.StepType;
import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.model.data.WidgetMapElementType;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;
import edu.umd.cs.guitar.smut.converter.OracleAnalyzer;

public class TestCaseGenerator {

	ObjectFactory factory = new ObjectFactory();
	OracleAnalyzer analyzer = new OracleAnalyzer();
	List<String> ignoredTestCases = new ArrayList<String>();
	
	/**
	 * @param ignoredTestCases the ignoredTestCases to set
	 */
	public void setIgnoredTestCases(List<String> ignoredTestCases) {
		this.ignoredTestCases = ignoredTestCases;
	}


	public List<TestCase> genTestSuite(TestCase guiState, int length) {
		EventTrace eventTrace = analyzer.getEventTrace(guiState);
		List<TestCase> testsuite = genTestSuite(eventTrace, length);
		return testsuite;
	}

	
	public List<TestCase> genTestSuite(EventTrace eventTrace, int length) {
		List<TestCase> testsuite = new ArrayList<TestCase>();

		int prefixLength = length - 1;

		EventEffectSetType eventEffectSet = eventTrace.getEventEffectSet();

		if (eventEffectSet == null)
			return testsuite;

		List<EventEffectType> eventEffectList = eventEffectSet.getEventEffect();

		Set<List<String>> interactionSet = new HashSet<List<String>>();

		while (prefixLength <= eventEffectList.size()) {

			List<String> prefix = getPrefixSequence(0, prefixLength, eventTrace);


			EventSetType lastEventSet = eventEffectList.get(prefixLength - 1)
					.getEventSet();
			if (lastEventSet != null) {
				for (String lastEvent : lastEventSet.getEventId()) {
					List<String> stringTestCase = new ArrayList<String>(prefix);
					stringTestCase.add(lastEvent);
					if (!interactionSet.contains(stringTestCase)) {
						interactionSet.add(stringTestCase);
						TestCase testcase = getTestCaseFromString(stringTestCase);
						
						// Setup reaching step
						int i=0;
						while (i<stringTestCase.size()-length){
							testcase.getStep().get(i).setReachingStep(true);
							i++;
						}
						while (i<stringTestCase.size()){
							testcase.getStep().get(i).setReachingStep(false);
							i++;
						}
						
						testsuite.add(testcase);
					}
				}
			}
			prefixLength++;

		}

		return testsuite;

	}

	private TestCase getTestCaseFromString(List<String> eventSequence) {
		TestCase testcase = factory.createTestCase();
		for (String eventID : eventSequence) {
			StepType step = factory.createStepType();
			step.setEventId(eventID);
			testcase.getStep().add(step);
		}

		return testcase;
	}

	private List<String> getPrefixSequence(int startIndex, int endIndex,
			EventTrace eventTrace) {
		List<String> sequence = new ArrayList<String>();
		EventEffectSetType eventEffectSet = eventTrace.getEventEffectSet();

		for (int i = startIndex; i < endIndex; i++) {
			EventEffectType eventEffect = eventEffectSet.getEventEffect()
					.get(i);
			if (eventEffect.getEventSet() != null)
				sequence.add(eventEffect.getEventId());
		}

		return sequence;
	}
}

package edu.umd.cs.guitar.smut.converter;

import java.util.List;

import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.EventGraphType;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.data.EventsType;
import edu.umd.cs.guitar.model.data.GUIMap;
import edu.umd.cs.guitar.model.data.ObjectFactory;
import edu.umd.cs.guitar.model.data.RowType;
import edu.umd.cs.guitar.model.data.StepType;
import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.model.wrapper.EFGWrapper;
import edu.umd.cs.guitar.model.wrapper.GUIMapWrapper;

public class EFGSynthesizer {

	ObjectFactory factory = new ObjectFactory();

	/**
	 * Initialize an EFG from a GUI Map and an existing EFG
	 * 
	 * <p>
	 * 
	 * @param inEFG
	 * @param map
	 * @return
	 */
	public EFG initializeEFG(EFG inEFG, GUIMap map) {

		EFG retEFG = factory.createEFG();
		// Copy event graph to the EFG
		EventsType retEvents = factory.createEventsType();
		retEFG.setEvents(retEvents);

		for (EventType event : map.getEventMap().getEventMapElement()) {
			event.setInitial(false);
			retEvents.getEvent().add(event);
			
		}

		EventGraphType retEventGraph = factory.createEventGraphType();
		retEFG.setEventGraph(retEventGraph);

		for (int i = 0; i < retEvents.getEvent().size(); i++) {
			RowType row = factory.createRowType();
			retEventGraph.getRow().add(row);
			for (int j = 0; j < retEvents.getEvent().size(); j++) {
				row.getE().add(0);
			}
		}

		EFGWrapper wEFG = new EFGWrapper(retEFG);

		// Copy event graph from old EFG to new EFG ignoring reaching edges
		if (inEFG != null) {
			EventsType inEvents = inEFG.getEvents();
			EventGraphType inEventGraph = inEFG.getEventGraph();

			if (inEventGraph != null && inEvents != null) {
				
				// Update initial status of the new EFG
				
				for (EventType inEvent: inEFG.getEvents().getEvent()){
					String inID = inEvent.getEventId();
					EventType newEvent= wEFG.getEventByID(inID);
					if(newEvent!=null){
						newEvent.setInitial(inEvent.isInitial());
					}
					
				}
				
				
				
				// Update edge info
				for (int rowIndex = 0; rowIndex < inEvents.getEvent().size(); rowIndex++) {
					RowType row = inEventGraph.getRow().get(rowIndex);

					for (int colIndex = 0; colIndex < inEvents.getEvent()
							.size(); colIndex++) {
						int relation = row.getE().get(colIndex);

						String sourceID = inEvents.getEvent().get(rowIndex)
								.getEventId();
						String targetID = inEvents.getEvent().get(colIndex)
								.getEventId();
						if (sourceID == null || targetID == null)
							continue;
						wEFG.addEdge(sourceID, targetID, relation);

					}
				}
			}

		}

		return retEFG;

	}

	public EFG updateEFG(EFG efg, TestCase testcase) {
		return updateEFG(efg, testcase, null);
	}

	public EFG updateEFG(EFG efg, TestCase testcase, GUIMap map) {

		EventGraphType eventGraph = factory.createEventGraphType();
		EFGWrapper wEFG = new EFGWrapper(efg);

		// Initialize event Graph
		if (eventGraph == null) {
			return efg;
		}

		GUIMapWrapper wMap = new GUIMapWrapper(map);

		List<StepType> stepList = testcase.getStep();
		try {

			for (int i = 0; i < stepList.size() - 1; i++) {
				String sourceID = stepList.get(i).getEventId();
				String targetID = stepList.get(i + 1).getEventId();

				if (sourceID == null || targetID == null)
					continue;

				String sourceEventType = wMap.getEventType(sourceID);

				int edgeType;

//				if (GUITARConstants.EXPAND.equals(sourceEventType)				
//						&& !sourceID.equals(targetID))
//					edgeType = GUITARConstants.REACHING_EDGE;
//				else
//					edgeType = GUITARConstants.FOLLOW_EDGE;
				int edge= wEFG.getEdge(sourceID, targetID);
				if(edge==GUITARConstants.NO_EDGE)
					wEFG.addEdge(sourceID, targetID, GUITARConstants.FOLLOW_EDGE);
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		return wEFG.getEfg();

	}

}

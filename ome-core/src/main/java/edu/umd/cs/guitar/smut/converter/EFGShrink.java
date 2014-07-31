package edu.umd.cs.guitar.smut.converter;

import java.util.HashSet;
import java.util.Set;

import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.EventGraphType;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.data.EventsType;
import edu.umd.cs.guitar.model.data.GUIMap;
import edu.umd.cs.guitar.model.data.ObjectFactory;
import edu.umd.cs.guitar.model.data.RowType;
import edu.umd.cs.guitar.model.wrapper.EFGWrapper;

public class EFGShrink {

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
				for (int rowIndex = 0; rowIndex < inEvents.getEvent().size(); rowIndex++) {
					RowType row = inEventGraph.getRow().get(rowIndex);

					for (int colIndex = 0; colIndex < inEvents.getEvent()
							.size(); colIndex++) {
						int relation = row.getE().get(colIndex);
						if (relation != GUITARConstants.NO_EDGE) {
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

		}

		return retEFG;

	}

	public EFG shrink(EFG inEFG) {

		// Copy event graph from old EFG to new EFG ignoring reaching edges

		EFG outEFG = factory.createEFG();
		if (inEFG == null) {
			return outEFG;
		}

		EventsType outEvents = factory.createEventsType();
		outEFG.setEvents(outEvents);

		EventGraphType outGraph = factory.createEventGraphType();
		outEFG.setEventGraph(outGraph);

		Set<String> lId = new HashSet<String>();

		for (EventType event : inEFG.getEvents().getEvent()) {
			String id = event.getEventId();
			if (!lId.contains(id)) {
				outEvents.getEvent().add(event);
				lId.add(id);
			}
		}

		// Initialize

		for (int i = 0; i < outEvents.getEvent().size(); i++) {
			RowType row = factory.createRowType();
			outGraph.getRow().add(row);
			for (int j = 0; j < outEvents.getEvent().size(); j++) {
				row.getE().add(GUITARConstants.NO_EDGE);
			}
		}

		EventsType inEvents = inEFG.getEvents();
		EventGraphType inEventGraph = inEFG.getEventGraph();

		EFGWrapper wOutEFG = new EFGWrapper(outEFG);
		if (inEventGraph != null && inEvents != null) {
			for (int rowIndex = 0; rowIndex < inEvents.getEvent().size(); rowIndex++) {
				RowType row = inEventGraph.getRow().get(rowIndex);

				for (int colIndex = 0; colIndex < inEvents.getEvent().size(); colIndex++) {
					int relation = row.getE().get(colIndex);
					if (relation != GUITARConstants.NO_EDGE) {
						String sourceID = inEvents.getEvent().get(rowIndex)
								.getEventId();
						String targetID = inEvents.getEvent().get(colIndex)
								.getEventId();
						if (sourceID == null || targetID == null)
							continue;
						wOutEFG.addEdge(sourceID, targetID, relation);
					}
				}
			}
		}

		return outEFG;
	}

}

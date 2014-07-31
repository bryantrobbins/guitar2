package edu.umd.cs.guitar.smut.converter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.EventMapType;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.data.GUIMap;
import edu.umd.cs.guitar.model.data.WidgetMapElementType;
import edu.umd.cs.guitar.model.data.WidgetMapType;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;
import edu.umd.cs.guitar.model.wrapper.GUIMapWrapper;

public class GUIMap2EventMain {

	interface EventTypeText {
		static final String INTERACTION = "SYSTEM INTERACTION";
		static final String EXPAND = "EXPAND";
		static final String RESTRICED = "RESTRICED FOCUS";
		static final String TERMINAL = "TERMINAL";
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("GUIMap2EventMain is starting...");
		GUIMap2EventConf conf = new GUIMap2EventConf();
		CmdLineParser parser = new CmdLineParser(conf);

		try {
			parser.parseArgument(args);
			GUIMap map = (GUIMap) IO.readObjFromFile(conf.IN_MAP_FILE,
					GUIMap.class);

			EventMapType eventMap = map.getEventMap();
			FileWriter fstream = new FileWriter(conf.OUT_TXT_FILE);
			BufferedWriter out = new BufferedWriter(fstream);

			for (EventType event : eventMap.getEventMapElement()) {

				String eid = event.getEventId();
				String wid = event.getWidgetId();
				Boolean init = event.isInitial();
				String type = getEventType(event, map);
				String action = event.getAction();

				String outString = "" + eid + "\t" + wid + "\t" + type + "\t"
						+ init + "\t" + action + "\t" + "\n";

				out.write(outString);
			}

			out.close();

		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			System.err.println();
			System.err.println("Usage: java [JVM options] "
					+ GUIMap2EventMain.class.getName()
					+ " [Relayer options] \n");
			System.err.println("where [Replayer options] include:");
			System.err.println();
			parser.printUsage(System.err);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}

	private static String getEventType(EventType event, GUIMap map) {
		// TODO Auto-generated method stub
		String type = event.getType();
		String wid = event.getWidgetId();
		if (!EventTypeText.INTERACTION.equals(type)) {
			return type;
		}

		// Now we modify the system interaction events to include window open
		// events

		GUIMapWrapper wGUIMap = new GUIMapWrapper(map);
		WidgetMapElementType widgetElement = wGUIMap.getWidgetByID(wid);

		if (widgetElement == null)
			return type;

		ComponentType component = widgetElement.getComponent();
		ComponentTypeWrapper wComponent = new ComponentTypeWrapper(component);
		String invokelist = wComponent
				.getFirstValueByName(GUITARConstants.INVOKELIST_TAG_NAME);

		if (invokelist == null)
			return type;
		else
			return EventTypeText.EXPAND;

	}

}

package edu.umd.cs.guitar.ripper.plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Date;

import edu.umd.cs.guitar.event.GEvent;
import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GIDGenerator;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.JFCConstants;
import edu.umd.cs.guitar.model.JFCDefaultIDGenerator;
import edu.umd.cs.guitar.model.JFCDefaultIDGeneratorSimple;
import edu.umd.cs.guitar.model.data.AttributesType;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.ContainerType;
import edu.umd.cs.guitar.model.data.EventMapType;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.data.GUIMap;
import edu.umd.cs.guitar.model.data.GUIType;
import edu.umd.cs.guitar.model.data.ParameterListType;
import edu.umd.cs.guitar.model.data.PropertyType;
import edu.umd.cs.guitar.model.data.StepType;
import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.model.data.WidgetMapElementType;
import edu.umd.cs.guitar.model.data.WidgetMapType;
import edu.umd.cs.guitar.model.wrapper.AttributesTypeWrapper;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;
import edu.umd.cs.guitar.model.wrapper.GUITypeWrapper;
import edu.umd.cs.guitar.util.AppUtil;
import edu.umd.cs.guitar.util.GUITARLog;
import edu.umd.cs.guitar.model.XMLHandler;

import com.mongodb.MongoClient;
import com.mongodb.DB;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

import com.google.gson.Gson;

/**
 * Experimental Ripper plugin to collect test case and component Map as
 * random-walk ripper is executing.
 * <p/>
 * <p/>
 * This plugin is ONLY USED for random-walk ripping experiments.
 *
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>.
 */
public class WalkingTestCaseGenerator implements
        GRipperBeforeExpandingComponnent, GRipperAfter {
    private static final String WALKING_RIPPER_MAP = "guitar.ripper.walking.map";
    private static final String WALKING_RIPPER_TEST = "guitar.ripper.walking.testcase";
    private static final String WALKING_RIPPER_DB = "guitar.ripper.walking.useDb";

    GUIMap theMap;
    TestCase theTest;

    public WalkingTestCaseGenerator() {
        // Initialize new Map and TestCase objects
        theMap = new GUIMap();
        theTest = new TestCase();
    }

    @Override
    public void beforeExpandingComponent(GComponent component, GWindow window) {

        ComponentType componentData = component.extractProperties();
        GUIType windowData = window.extractWindow();

        long windowHashcode = getWindowHashCode(windowData);
        String componentID = getComponentID(componentData, windowHashcode);
        PropertyType cid = new PropertyType();
        cid.setName(GUITARConstants.ID_TAG_NAME);
        cid.setValue(new ArrayList<String>());
        cid.getValue().add(componentID);
        componentData.getAttributes().getProperty().add(cid);


        List<GEvent> events = component.getEventList();
        GEvent theGEvent = null;

        for (GEvent e : events) {
            if (e.getClass().getName().equalsIgnoreCase("JFCActionHandler")) {
                theGEvent = e;
            }
        }

        // Save the above info to Map and TestCase objects

        // Add widget to Map
        WidgetMapElementType theWidget = new WidgetMapElementType();
        theWidget.setWidgetId(componentID);
        theWidget.setComponent(componentData);
        theWidget.setWindow(windowData.getWindow());

        if (theMap.getWidgetMap() == null) {
            theMap.setWidgetMap(new WidgetMapType());
            theMap.setEventMap(new EventMapType());
        }

        if (!theMap.getWidgetMap().getWidgetMapElement().contains(theWidget)) {
            theMap.getWidgetMap().getWidgetMapElement().add(theWidget);
        }

        // Build event
        EventType theEvent = new EventType();
        theEvent.setType(component.getTypeVal());
        theEvent.setEventId(componentID.replaceAll("w", "e"));
        theEvent.setWidgetId(componentID);
        theEvent.setInitial(false);
        theEvent.setParameterList(new ArrayList<ParameterListType>());
        theEvent.setType(component.getTypeVal());
        theEvent.setAction("edu.umd.cs.guitar.event.JFCActionHandler");

        // Add event to Map
        if (!theMap.getEventMap().getEventMapElement().contains(theEvent)) {
            theMap.getWidgetMap().getWidgetMapElement().add(theWidget);
            theMap.getEventMap().getEventMapElement().add(theEvent);
        }

        // Add event to test case
        StepType thisStep = new StepType();
        thisStep.setEventId(componentID);
        thisStep.setReachingStep(true);
        theTest.getStep().add(thisStep);
    }

    @Override
    public void afterRipping() {
        // Write Map and TC to file
        XMLHandler xmlHandler = new XMLHandler();

        String mapFileName = System.getProperty(WALKING_RIPPER_MAP);
        if (mapFileName == null) {
            mapFileName = "WalkingRipper.MAP";
        }

        String testFileName = System.getProperty(WALKING_RIPPER_TEST);

        if (testFileName == null) {
            testFileName = "WalkingRipper.TST";
        }

        IO.writeObjToFile(theMap, mapFileName);
        IO.writeObjToFile(theTest, testFileName);

        GUITARLog.log
                .info("Walking Ripper MAP file written to :" + mapFileName);
        GUITARLog.log.info("Walking Ripper TST file written to :"
                + testFileName);
    }


    /**
     * This method is copied from {@link JFCDefaultIDGenerator}
     * <p/>
     *
     * @param gui GUIType shows hashcode is to be generated
     * @return Hashcode for the input GUIType
     */
    private long getWindowHashCode(GUIType gui) {
        GUITypeWrapper wGUI = new GUITypeWrapper(gui);
        String title = wGUI.getTitle();
        AppUtil appUtil = new AppUtil();

        String fuzzyTitle = appUtil.findRegexForString(title);

        long hashcode = fuzzyTitle.hashCode();

        hashcode = (hashcode * 2) & 0xffffffffL;

        return hashcode;
    }

    private String getComponentID(ComponentType component, long windoHashCode) {
        AttributesType attributes = component.getAttributes();
        final int prime = 31;
        long hashcode = 1;

        if (attributes != null) {

            long localHashCode = getLocalHashcode(component);
            hashcode = windoHashCode * prime + localHashCode;
            hashcode = (hashcode * 2) & 0xffffffffL;

        } else {
            hashcode = windoHashCode;
        }

        String sID = GUITARConstants.COMPONENT_ID_PREFIX + hashcode;
        return sID;
    }

    static List<String> ID_PROPERTIES = new ArrayList<String>(
            JFCConstants.ID_PROPERTIES);

    /**
     * Those classes are invisible widgets but cause false-positive when
     * calculating ID
     */
    static List<String> IGNORED_CLASSES = Arrays.asList("javax.swing.JPanel",
            "javax.swing.JTabbedPane", "javax.swing.JScrollPane",
            "javax.swing.JSplitPane", "javax.swing.Box",
            "javax.swing.JViewport", "javax.swing.JScrollBar",
            "javax.swing.JLayeredPane",
            "javax.swing.JList$AccessibleJList$AccessibleJListChild",
            "javax.swing.JList$AccessibleJList", "javax.swing.JList",
            "javax.swing.JScrollPane$ScrollBar",
            "javax.swing.plaf.metal.MetalScrollButton");

    /**
     * Those classes are invisible widgets but cause false-positive when
     * calculating ID
     */
    static List<String> IS_ADD_INDEX_CLASSES = Arrays
            .asList("javax.swing.JTabbedPane$Page");

    /**
     * @param component
     * @return
     */
    private long getLocalHashcode(ComponentType component) {
        final int prime = 31;

        long hashcode = 1;

        AttributesType attributes = component.getAttributes();
        if (attributes == null) {
            return hashcode;
        }

        // Specially handle titles
        AttributesTypeWrapper wAttribute = new AttributesTypeWrapper(attributes);
        String sClass = wAttribute
                .getFirstValByName(GUITARConstants.CLASS_TAG_NAME);

        if (IGNORED_CLASSES.contains(sClass)) {
            hashcode = (prime * hashcode + (sClass == null ? 0 : (sClass
                    .hashCode())));
            return hashcode;
        }

        // Normal cases
        // Using ID_Properties for hash code

        List<PropertyType> lProperty = attributes.getProperty();

        if (lProperty == null) {
            return hashcode;
        }

        for (PropertyType property : lProperty) {

            String name = property.getName();
            if (ID_PROPERTIES.contains(name)) {

                hashcode = (prime * hashcode + (name == null ? 0 : name
                        .hashCode()));

                List<String> valueList = property.getValue();
                if (valueList != null) {
                    for (String value : valueList) {
                        hashcode = (prime * hashcode + (value == null ? 0
                                : (value.hashCode())));

                    }
                }
            }
        }

        hashcode = (hashcode * 2) & 0xffffffffL;

        return hashcode;

    }
}

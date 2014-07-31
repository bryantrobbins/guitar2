package edu.umd.cs.guitar.smut.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.ContainerType;
import edu.umd.cs.guitar.model.data.EventEffectSetType;
import edu.umd.cs.guitar.model.data.EventEffectType;
import edu.umd.cs.guitar.model.data.EventMapElementType;
import edu.umd.cs.guitar.model.data.EventSetType;
import edu.umd.cs.guitar.model.data.EventStateType;
import edu.umd.cs.guitar.model.data.EventTrace;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.data.EventsType;
import edu.umd.cs.guitar.model.data.GUIMap;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.GUIType;
import edu.umd.cs.guitar.model.data.ObjectFactory;
import edu.umd.cs.guitar.model.data.StepType;
import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.model.data.WidgetMapElementType;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;
import edu.umd.cs.guitar.model.wrapper.GUITypeWrapper;

public class OracleAnalyzer {

	// TODO: Move this one to an external configuration file
	// Keeping it here because we don't want to interfere the current GUITAR
	// structure
	private static List<String> IGNORED_CLASS_EVENTS = Arrays.asList(
			"com.jgoodies.looks.plastic.PlasticArrowButton",
			"com.jgoodies.looks.plastic.PlasticComboBoxButton",
			"com.jgoodies.looks.plastic.PlasticSpinnerUI$SpinnerArrowButton",
			"javax.swing.plaf.synth.SynthScrollBarUI$1",
			"javax.swing.plaf.synth.SynthScrollBarUI$2",
			"javax.swing.plaf.synth.SynthArrowButton",
			"javax.swing.plaf.basic.BasicComboPopup$1",
			"javax.swing.JScrollPane$ScrollBar",
			"javax.swing.plaf.metal.MetalScrollButton",
			"javax.swing.plaf.metal.MetalComboBoxButton",
			"sun.awt.X11.XFileDialogPeer$2",
			"javax.swing.JScrollPane$ScrollBar", "sun.swing.FilePane$1",
			"sun.swing.FilePane$2", "sun.swing.FilePane$3",
			"sun.swing.FilePane$4", "sun.swing.FilePane$5"

	);

	List<ComponentType> eventComponent;
	ObjectFactory factory = new ObjectFactory();

	public EventTrace getEventTrace(TestCase testcase) {
		EventTrace trace = factory.createEventTrace();

		GUIStructure guiState;
		guiState = testcase.getGUIStructure();
		EventSetType initialEventSet = getEventState(guiState);
		trace.setInitialEventSet(initialEventSet);

		EventEffectSetType eventEffectSet = factory.createEventEffectSetType();
		trace.setEventEffectSet(eventEffectSet);

		for (StepType step : testcase.getStep()) {
			guiState = step.getGUIStructure();

			EventEffectType eventEffect = factory.createEventEffectType();
			EventSetType eventSet = getEventState(guiState);

			eventEffect.setEventId(step.getEventId());
			eventEffect.setEventSet(eventSet);

			eventEffectSet.getEventEffect().add(eventEffect);

		}
		return trace;
	}

	/**
	 * Convert from a GUI structure to an event state
	 * 
	 * <p>
	 * 
	 * @param guiState
	 * @return
	 */
	EventSetType getEventState(GUIStructure guiState) {
		EventSetType eventState = factory.createEventSetType();

		List<WidgetMapElementType> subList;
		subList = getComponentWithEvent(guiState);

		for (WidgetMapElementType comp : subList) {
			ComponentTypeWrapper wComp = new ComponentTypeWrapper(comp
					.getComponent());
			String ID = wComp.getFirstValueByName(GUITARConstants.ID_TAG_NAME);
			// List<String> eventIDList = wComp.getEventIDList();
			ID = ID.replaceAll(GUITARConstants.COMPONENT_ID_PREFIX,
					GUITARConstants.EVENT_ID_PREFIX);

			// String ID = wComp
			// .getFirstValueByName(GUITARConstants.ID_TAG_NAME) +"_" +
			// wComp.getFirstValueByName(GUITARConstants.CLASS_TAG_NAME);
			eventState.getEventId().add(ID);
		}
		return eventState;
	}

	@Deprecated
	public List<ComponentType> getWidgetWithEvent(TestCase testcase) {
		List<ComponentType> result = new ArrayList<ComponentType>();

		GUIStructure guiState;
		guiState = testcase.getGUIStructure();
		List<WidgetMapElementType> subList;
		subList = getComponentWithEvent(guiState);
		for (WidgetMapElementType comp : subList) {
			if (!inList(comp.getComponent(), result)) {
				result.add(comp.getComponent());
			}
		}

		for (StepType step : testcase.getStep()) {

			guiState = step.getGUIStructure();
			if (guiState != null) {
				subList = getComponentWithEvent(guiState);
				for (WidgetMapElementType comp : subList) {
					if (!inList(comp.getComponent(), result)) {
						result.add(comp.getComponent());
					}
				}
			}
		}
		return result;
	}

	/**
	 * @param component
	 * @param list
	 * @return
	 */
	private boolean inList(ComponentType component, List<ComponentType> list) {
		if (component == null)
			return false;
		ComponentTypeWrapper wComponent = new ComponentTypeWrapper(component);
		String ID = wComponent.getFirstValueByName(GUITARConstants.ID_TAG_NAME);

		if (ID == null)
			return false;

		for (ComponentType oComp : list) {
			ComponentTypeWrapper wOComp = new ComponentTypeWrapper(oComp);
			String oID = wOComp
					.getFirstValueByName(GUITARConstants.ID_TAG_NAME);
			if (ID.equals(oID))
				return true;
		}

		return false;
	}

	/**
	 * Get all widgets with available events in the GUI
	 * 
	 * <p>
	 * 
	 * @param gs
	 * @return
	 */
	public List<WidgetMapElementType> getComponentWithEvent(GUIStructure gs) {
		List<WidgetMapElementType> list = new ArrayList<WidgetMapElementType>();

		Set<GUIType> allAvailableWindows = getAvailableWindows(gs);
		for (GUIType gui : allAvailableWindows) {

			List<ComponentType> subList = getWidgetWithEvent(gui);
			for (ComponentType component : subList) {
				WidgetMapElementType widgetMap = factory
						.createWidgetMapElementType();
				ComponentTypeWrapper wComponent = new ComponentTypeWrapper(
						component);
				widgetMap.setWidgetId(wComponent
						.getFirstValueByName(GUITARConstants.ID_TAG_NAME));

				widgetMap.setWindow(gui.getWindow());

				ComponentType compactWidget = factory.createComponentType();
				compactWidget.setAttributes(component.getAttributes());
				compactWidget.setOptional(component.getOptional());

				widgetMap.setComponent(compactWidget);

				list.add(widgetMap);
			}

		}

		return list;
	}

	private List<ComponentType> getWidgetWithEvent(GUIType gui) {

		ContainerType container = gui.getContainer();
		ComponentType component = (ComponentType) container;
		List<ComponentType> result = getAvailableEventsFromComponent(component);
		return result;
	}

	private List<ComponentType> getAvailableEventsFromComponent(
			ComponentType component) {
		List<ComponentType> result = new ArrayList<ComponentType>();

		ComponentTypeWrapper wComponent = new ComponentTypeWrapper(component);
		String sEvent = wComponent
				.getFirstValueByName(GUITARConstants.EVENT_TAG_NAME);

		String sEnabled = wComponent.getFirstValueByName("enabled");
		String sVisible = wComponent.getFirstValueByName("visible");
		String sHeight = wComponent.getFirstValueByName("height");
		String sWidth = wComponent.getFirstValueByName("width");
		String sEditable = wComponent.getFirstValueByName("editable");

		String sTitle = wComponent
				.getFirstValueByName(GUITARConstants.TITLE_TAG_NAME);
		String sClass = wComponent
				.getFirstValueByName(GUITARConstants.CLASS_TAG_NAME);

		if (sEvent != null && "true".equals(sEnabled)
				&& "true".equals(sVisible) && !"0".equals(sHeight)
				&& !"0".equals(sWidth) && !"false".equals(sEditable)
				&& !IGNORED_CLASS_EVENTS.contains(sClass)) {

			String sID = wComponent
					.getFirstValueByName(GUITARConstants.ID_TAG_NAME);

			if (sID != null)
				result.add(component);
		}

		if (component instanceof ContainerType) {
			ContainerType container = (ContainerType) component;
			for (ComponentType comp : container.getContents()
					.getWidgetOrContainer()) {
				result.addAll(getAvailableEventsFromComponent(comp));
			}
		}

		return result;
	}

	// /**
	// * @param gui
	// * @return
	// */
	// private static Set<String> getAvailableEvents(GUIType gui) {
	// ContainerType container = gui.getContainer();
	// Set<String> result = new HashSet<String>();
	// getAvailableEventsFromComponent(container, result);
	// return result;
	// }
	//
	// /**
	// * @param container
	// * @param result
	// */
	// private static void getAvailableEventsFromComponent(
	// ComponentType component, Set<String> result) {
	//
	// ComponentTypeWrapper wComponent = new ComponentTypeWrapper(component);
	// String sEvent = wComponent
	// .getFirstValueByName(GUITARConstants.EVENT_TAG_NAME);
	//
	// String sEnabled = wComponent.getFirstValueByName("enabled");
	// String sVisible = wComponent.getFirstValueByName("visible");
	// String sHeight = wComponent.getFirstValueByName("height");
	// String sWidth = wComponent.getFirstValueByName("width");
	//
	// String sTitle = wComponent
	// .getFirstValueByName(GUITARConstants.TITLE_TAG_NAME);
	// String sClass = wComponent
	// .getFirstValueByName(GUITARConstants.CLASS_TAG_NAME);
	//
	// if (sEvent != null && "true".equals(sEnabled)
	// && "true".equals(sVisible) && !"0".equals(sHeight)
	// && !"0".equals(sWidth)
	// // && "true".equals(sFocusable)
	// ) {
	// String sID = wComponent
	// .getFirstValueByName(GUITARConstants.ID_TAG_NAME);
	//
	// if (sID != null)
	// result.add(sID);
	// }
	//
	// if (component instanceof ContainerType) {
	// ContainerType container = (ContainerType) component;
	// for (ComponentType comp : container.getContents()
	// .getWidgetOrContainer()) {
	// getAvailableEventsFromComponent(comp, result);
	// }
	// }
	//
	// }

	/**
	 * Get all available windows from a gui state
	 * 
	 * <p>
	 * 
	 * @param gs
	 * @return
	 */
	private static Set<GUIType> getAvailableWindows(GUIStructure gs) {

		Set<GUIType> availableWindows = new HashSet<GUIType>();

		for (GUIType window : gs.getGUI()) {
			GUITypeWrapper wGUI = new GUITypeWrapper(window);
			if (wGUI.isModal()) {

				availableWindows = getAvailableWindowFromModal(window, gs);

				boolean isShortestModalChain = true;
				for (GUIType child : availableWindows) {
					GUITypeWrapper wChild = new GUITypeWrapper(child);
					if (wChild.isModal()) {
						isShortestModalChain = false;
						break;
					}
				}
				if (isShortestModalChain) {
					availableWindows.add(window);
					return (availableWindows);
				}
			}
		}

		return new HashSet<GUIType>(gs.getGUI());

	}

	private static Set<GUIType> getAvailableWindowFromModal(GUIType root,
			GUIStructure gs) {
		GUIType firstModalWindow = getFirstModalChild(root, gs);

		// No successor modal window
		if (firstModalWindow == null) {
			Set<GUIType> allSuccessor = getSuccessor(root, gs);
			return allSuccessor;
		} else {
			return getAvailableWindowFromModal(firstModalWindow, gs);
		}
	}

	/**
	 * Get first modal window from children list, not including the current
	 * window
	 * 
	 * <p>
	 * 
	 * @param wRoot
	 * @return
	 */
	private static GUIType getFirstModalChild(GUIType root, GUIStructure gs) {

		Set<GUIType> invokeeWindows = getInvokeeWindow(root, gs);

		for (GUIType child : invokeeWindows) {
			GUITypeWrapper wChild = new GUITypeWrapper(child);
			if (wChild.isModal())
				return child;

			GUIType childFirstModal = getFirstModalChild(child, gs);
			if (childFirstModal != null)
				return childFirstModal;
		}
		return null;
	}

	/**
	 * @param root
	 * @return
	 */
	private static Set<GUIType> getInvokeeWindow(GUIType root, GUIStructure gs) {
		ContainerType containerType = root.getContainer();

		Set<GUIType> lInvokeeWindow = new HashSet<GUIType>();
		getInvokeeWindowComponent(containerType, lInvokeeWindow, gs);

		// Remove loop

		GUITypeWrapper wRoot = new GUITypeWrapper(root);
		String title = wRoot.getTitle();

		for (GUIType child : lInvokeeWindow) {
			GUITypeWrapper wChild = new GUITypeWrapper(child);
			if (title.equals(wChild.getTitle()))
				lInvokeeWindow.remove(child);
		}

		return lInvokeeWindow;
	}

	/**
	 * @param containerType
	 * @param lInvokeeWindow
	 */
	private static void getInvokeeWindowComponent(ComponentType component,
			Set<GUIType> lInvokeeWindow, GUIStructure gs) {
		ComponentTypeWrapper wComponent = new ComponentTypeWrapper(component);

		List<String> localInvokeeWindow = wComponent
				.getValueListByName(GUITARConstants.INVOKELIST_TAG_NAME);

		if (localInvokeeWindow != null) {
			for (GUIType gui : gs.getGUI()) {
				GUITypeWrapper wGUI = new GUITypeWrapper(gui);
				String title = wGUI.getTitle();
				if (localInvokeeWindow.contains(title))
					lInvokeeWindow.add(gui);
			}
		}

		if (component instanceof ContainerType) {
			ContainerType container = (ContainerType) component;
			for (ComponentType child : container.getContents()
					.getWidgetOrContainer()) {
				getInvokeeWindowComponent(child, lInvokeeWindow, gs);
			}

		}
	}

	/**
	 * @param root
	 * @param gs
	 * @return
	 */
	private static Set<GUIType> getSuccessor(GUIType root, GUIStructure gs) {
		Set<GUIType> lSuccessor = new HashSet<GUIType>();
		getSuccessorHelper(root, gs, lSuccessor);
		// lSuccessor.add(root);
		return lSuccessor;
	}

	/**
	 * @param root
	 * @param gs
	 * @param lSuccessor
	 */
	private static void getSuccessorHelper(GUIType root, GUIStructure gs,
			Set<GUIType> lSuccessor) {

		Set<GUIType> children = getInvokeeWindow(root, gs);
		for (GUIType child : children) {
			getSuccessorHelper(child, gs, lSuccessor);
		}
	}

	public GUIMap updateMap(GUIMap inputMap, TestCase testcase) {
		GUIMap outputMap = inputMap;
		for (StepType step : testcase.getStep()) {
			GUIStructure gs = step.getGUIStructure();
			if (gs != null)
				outputMap = updateMap(outputMap, gs);
		}
		return outputMap;
	}

	public GUIMap updateMap(GUIMap inputMap, GUIStructure gs) {
		GUIMap outputMap = inputMap;

		List<WidgetMapElementType> widgetList = getComponentWithEvent(gs);

		for (WidgetMapElementType widget : widgetList) {

			if (!isInMap(widget, outputMap))
				if (widget.getWidgetId() != null) {
					System.out.println("New widget found: "
							+ widget.getWidgetId());

					ComponentTypeWrapper wComponent = new ComponentTypeWrapper(
							widget.getComponent());

					List<EventType> eventList = wComponent.getEventList();

					// Update widget map
					outputMap.getWidgetMap().getWidgetMapElement().add(widget);

					// Update event map

					for (EventType event : eventList) {
						outputMap.getEventMap().getEventMapElement().add(event);
					}
				}
		}

		return outputMap;
	}

	boolean isInMap(WidgetMapElementType component, GUIMap inMap) {
		String wID = component.getWidgetId();
		for (WidgetMapElementType oWidget : inMap.getWidgetMap()
				.getWidgetMapElement()) {
			String oWID = oWidget.getWidgetId();

			if (wID != null)
				if (wID.equals(oWID))
					return true;
		}
		return false;
	}

	GUIMap compactMap(GUIMap inMap) {
		GUIMap result = null;
		for (WidgetMapElementType widget : inMap.getWidgetMap()
				.getWidgetMapElement()) {

		}
		return result;
	}
}

package edu.umd.cs.guitar.processors.features;

import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.processors.applog.TextObject;
import edu.umd.cs.guitar.util.GUITARUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by bryan on 4/5/14.
 * <p/>
 * This class captures the lines of text file as a very simple Java Object.
 */
public class FeaturesObject {

    /**
     * A log4j logger.
     */
    private static Logger logger = LogManager.getLogger(FeaturesObject.class);

    /**
     * Default value of maxN.
     */

    private static final int DEFAULT_MAX_N = 0;

    /**
     * Max number of N-grams to use in feature extraction.
     */
    private int maxN = DEFAULT_MAX_N;

    /**
     * This is the list of features.
     */
    private Set<String> features;


    /**
     * Default constructor constructs FeaturesObject from existing feature list.
     *
     * @param featuresVal the features
     */
    public FeaturesObject(final Set<String> featuresVal) {
        this.features = featuresVal;
    }

    /**
     * Get the number of features in this object.
     *
     * @return number of lines
     */
    public long size() {
        return features.size();
    }

    /**
     * Get the features associated with this object.
     *
     * @return the List of features
     */
    public Set<String> getFeatures() {
        return features;
    }

    /**
     * Check if a feature exists in this feature list.
     *
     * @param feature the feature to search for
     * @return true if the feature is in the list, else false
     */
    public boolean hasFeature(final String feature) {
        return features.contains(feature);
    }


    /**
     * Return the feature object for a given test case object.
     *
     * @param testCase the test case
     * @param maxN     the max value of N to use when extracting N-grams from the test case
     * @return the corresponding features, or null if the test case is null
     */
    public static FeaturesObject getFeaturesFromTestCase(final TestCase testCase,
                                                         final int maxN) {

        Set<String> features = new HashSet<String>();
        List<String> eventsInOrder = GUITARUtils.getEventIdsFromTest(testCase);

        for (int i = 1; i <= maxN; i++) {
            features.addAll(getNgrams(i, eventsInOrder));
        }

        features.addAll(getBefores(eventsInOrder));

        return new FeaturesObject(features);
    }

    /**
     * Return the feature object for a given test case object and given log.
     *
     * @param testCase the test case
     * @param testLog  the test case log
     * @param gui      the GUI Structure for this application
     * @param efg      the EFG for this application
     * @param maxN     the max value of N to use when extracting N-grams from the test case
     * @param trim     true if the features of the test case should be trimmed according to the log
     * @return the corresponding features, or null if the test case is null
     */
    public static FeaturesObject getFeaturesFromTestCase(final TestCase testCase,
                                                         final TextObject testLog,
                                                         final GUIStructure gui,
                                                         final EFG efg,
                                                         final int maxN,
                                                         final boolean trim) {

        if (efg == null) {
            logger.info("The EFG is null in getFeaturesFromTestCase");
        }

        if (gui == null) {
            logger.info("The GUIStructure is null in getFeaturesFromTestCase");
        }

        Set<String> features = new HashSet<String>();
        List<String> eventsInOrder = GUITARUtils.getEventIdsFromTest(testCase);

        // Prune events if test execution was not completed (assuming due to test case being infeasible)
        if (trim && !testLog.computeResult().equals(TextObject.TestResult.PASS)) {
            eventsInOrder = eventsInOrder.subList(0, testLog.computeStepCount());
        }

        // Get event types
        List<String> typesInOrder = new ArrayList<String>();
        for (String eventId : eventsInOrder) {
            EventType et = getEventById(efg, eventId);
            typesInOrder.add(et.getType());
        }

        for (int i = 1; i <= maxN; i++) {
            features.addAll(getNgrams(i, eventsInOrder));
            features.addAll(getNgrams(i, typesInOrder));
        }

        features.addAll(getBefores(eventsInOrder));
        features.addAll(getBefores(typesInOrder));

        return new FeaturesObject(features);
    }

    /**
     * Get event from an EFG by ID.
     *
     * @param efg     the EFG for this suite
     * @param eventId the event ID to look up
     * @return the EventType object (has all event data)
     */
    private static EventType getEventById(final EFG efg, final String eventId) {
        if (efg == null || efg.getEvents() == null) {
            logger.info("The EFG or its events are null in getEventById");
        }

        for (EventType et : efg.getEvents().getEvent()) {
            if (et.getEventId().equals(eventId)) {
                return et;
            }
        }

        throw new EventTypeLookupException(eventId);
    }

    /**
     * Get the collection of N-gram strings.
     *
     * @param n      the value of 'n' for the n-grams
     * @param events an event sequence from which to construct n-grams
     * @return a Set of N-grams
     */
    private static Set<String> getNgrams(final int n, final List<String> events) {
        List<String> deepCopy = new ArrayList<String>(events);

        // pad
        if (n > 1) {
            for (int i = 0; i < (n + 1); i++) {
                deepCopy.add(0, "START");
                deepCopy.add("END");
            }
        }

        // Compute ngrams as arrays
        Set<String> ngrams = new HashSet<String>();

        for (int i = 0; i < deepCopy.size() - n + 1; i++) {
            ngrams.add(getNgramFromList(deepCopy.subList(i, i + n)));
        }

        return ngrams;
    }

    /**
     * Get a String representation of an ngram from an array representation.
     *
     * @param ngram an ngram as an array of symbols
     * @return a string representation for the ngram
     */
    private static String getNgramFromList(final List<String> ngram) {
        StringBuilder sb = new StringBuilder();
        sb.append("ngram");

        for (String sym : ngram) {
            sb.append("_");
            sb.append(sym);
        }

        return sb.toString();
    }

    /**
     * Build and return the "before" relationships for a given evemt sequence.
     *
     * @param events an event sequence from which to construct before relationships
     * @return a Set of before relationships as Strings
     */
    private static Set<String> getBefores(final List<String> events) {
        Set<String> befores = new HashSet<String>();
        for (int i = 0; i < events.size(); i++) {
            for (int j = i + 1; j < events.size(); j++) {
                StringBuilder sb = new StringBuilder();
                sb.append(events.get(i));
                sb.append("_before_");
                sb.append(events.get(j));
                befores.add(sb.toString());
            }
        }

        return befores;
    }
}


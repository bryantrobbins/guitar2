package edu.umd.cs.guitar.processors.features;

import edu.umd.cs.guitar.model.data.TestCase;
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
     * Max number of N-grams to use in feature extraction.
     */
    private static final int MAX_N = 4;
    /**
     * This is the list of features.
     */
    private List<String> features;


    /**
     * Default constructor constructs FeaturesObject from existing feature list.
     *
     * @param featuresVal the features
     */
    public FeaturesObject(final List<String> featuresVal) {
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
    public List<String> getFeatures() {
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
     * @return the corresponding features, or null if the test case is null
     */
    public static FeaturesObject getFeaturesFromTestCase(final TestCase
                                                                 testCase) {

        List<String> features = new ArrayList<String>();
        List<String> eventsInOrder = GUITARUtils.getEventIdsFromTest(testCase);

        for (int i = 1; i <= MAX_N; i++) {
            features.addAll(getNgrams(i, eventsInOrder));
        }

        features.addAll(getBefores(eventsInOrder));

        return new FeaturesObject(features);
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


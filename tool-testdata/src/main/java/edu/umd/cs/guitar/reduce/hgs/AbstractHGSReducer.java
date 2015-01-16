package edu.umd.cs.guitar.reduce.hgs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * A Base class for HGS-based reductions.
 */
public abstract class AbstractHGSReducer implements HGSReducer {

    /**
     * A map for looking up info by cardinality.
     */
    private Map<Integer, Set<String>> cardMap;

    /**
     * A map for tracking growth (I think?).
     */
    private Map<Integer, Integer> lastSize;

    /**
     * Reset sticky data.
     */
    public void clearAbstractData() {
        if (cardMap != null) {
            cardMap = null;
            lastSize = null;
        }
    }

    /**
     * A helper method for lookup.
     *
     * @param currentCard current cardinality to look up
     * @param marked      map of marked data
     * @return Set of requirements which have set sizes matching desired
     * cardinality
     */
    public Set<String> getRequirementsWithSetsMatchingCardinality(
            final int currentCard, final Map<String, Boolean> marked) {
        if (this.cardMap == null) {
            this.cardMap = new HashMap<Integer, Set<String>>();
            this.lastSize = new HashMap<Integer, Integer>();
        }

        int markedCountNow = 0;

        if (marked != null) {
            markedCountNow = computeMarkedCount(marked);
        } else {
            markedCountNow = 0;
        }

        Set<String> ret = this.cardMap.get(currentCard);
        if ((ret == null) || (markedCountNow > lastSize.get(currentCard))) {
            ret = new HashSet<String>();
            for (String rid : this.getAllRequirements()) {
                boolean cardMatch = this.getTestSetForRequirement(rid).size()
                        == currentCard;
                if ((marked == null) && cardMatch) {
                    ret.add(rid);
                } else if ((marked != null) && (!marked.get(rid))
                        && cardMatch) {
                    ret.add(rid);
                }
            }

            this.cardMap.put(currentCard, ret);
            this.lastSize.put(currentCard, markedCountNow);
        }

        return this.cardMap.get(currentCard);
    }

    /**
     * Helper method for getting number of marked ???
     *
     * @param mark Marked data
     * @return count of marked entries
     */
    public int computeMarkedCount(final Map<String, Boolean> mark) {
        int count = 0;
        for (Entry<String, Boolean> ent : mark.entrySet()) {
            if (ent.getValue()) {
                count++;
            }
        }

        return count;

    }

    /**
     * Higher-level method for getting List of tests which cover requirements
     * which have sets matching desired cardinality.
     *
     * @param currentCard current cardinality to search for
     * @param marked      map of marked data
     * @return List of tests
     */
    public List<String> getTestsFromSetsWithCardinality(final int currentCard,
                                                        final Map<String,
                                                                Boolean>
                                                                marked) {
        Set<String> ret = new HashSet<String>();
        for (String rid : this.getRequirementsWithSetsMatchingCardinality(
                currentCard, marked)) {
            for (String tid : this.getTestSetForRequirement(rid)) {
                ret.add(tid);
            }
        }

        return getListFromSet(ret);
    }

    /**
     * Helper method for converting sets to lists.
     *
     * @param in A set
     * @return A list
     */
    private List<String> getListFromSet(final Set<String> in) {
        List<String> arrayList = new ArrayList<String>();
        arrayList.addAll(in);
        return arrayList;
    }

}

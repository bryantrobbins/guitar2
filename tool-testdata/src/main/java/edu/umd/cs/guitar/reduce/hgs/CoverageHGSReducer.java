package edu.umd.cs.guitar.reduce.hgs;

import edu.umd.cs.guitar.main.TestDataManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * A class which implements HGS for reduction based on code coverage.
 */
public class CoverageHGSReducer extends AbstractHGSReducer {

    /**
     * A TestDataManager instance.
     */
    private TestDataManager tdm;

    /**
     * Map for lookups.
     */
    private Map<String, Set<String>> map;

    /**
     * Map for inverse lookups.
     */
    private Map<String, Set<String>> inverseMap;

    /**
     * Basic constructor given TestDataManager and a suite ID.
     *
     * @param theTdm       the manager instance
     * @param inputSuiteId the id of the suite reduced
     */
    public CoverageHGSReducer(final TestDataManager theTdm,
                              final String inputSuiteId) {
        this.tdm = theTdm;
        this.map = new HashMap<String, Set<String>>();
        this.inverseMap = new HashMap<String, Set<String>>();
    }

    /**
     * Load forward map with a single test case's information.
     *
     * @param tid the ID of the test to be processed
     */

    @Override
    public void processTestCase(final String tid) {
        tdm.getDBId();
        //ProjectData coverage = tdm.getCoverageObjectForTest(dbId, tid);
        //Set<String> lineIds = CoberturaUtils.getIdsForLinesCovered(coverage);

//        for (String lid : lineIds) {
//            if (map.containsKey(lid)) {
//                map.get(lid).add(tid);
//            } else {
//                map.put(lid, new HashSet<String>());
//                map.get(lid).add(tid);
//            }
//        }
//
//        this.clearAbstractData();
    }

    @Override
    public Set<String> getMetRequirementsForTestCase(final String tid) {
        Set<String> ret = inverseMap.get(tid);
        if (ret == null) {
            ret = new HashSet<String>();
            for (Entry<String, Set<String>> ent : map.entrySet()) {
                if (ent.getValue().contains(tid)) {
                    ret.add(ent.getKey());
                }
            }

            inverseMap.put(tid, ret);
        }

        return inverseMap.get(tid);
    }

    @Override
    public Set<String> getAllRequirements() {
        return map.keySet();
    }

    @Override
    public Set<String> getTestSetForRequirement(final String rid) {
        return map.get(rid);
    }

}

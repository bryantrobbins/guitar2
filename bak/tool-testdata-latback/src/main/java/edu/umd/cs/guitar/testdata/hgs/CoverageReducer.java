package edu.umd.cs.guitar.testdata.hgs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sourceforge.cobertura.coveragedata.ProjectData;
import edu.umd.cs.guitar.testdata.TestDataManager;
import edu.umd.cs.guitar.testdata.util.CoberturaUtils;

public class CoverageReducer extends AbstractReducer {

	private TestDataManager tdm;
	private String dbId;
	private Map<String, Set<String>> map;
	private Map<String, Set<String>> inverseMap;
	
	
	public CoverageReducer(TestDataManager tdm, String dbId, String inputSuiteId){
		this.tdm = tdm;
		this.dbId = dbId;
		this.map = new HashMap<String, Set<String>>();
		this.inverseMap = new HashMap<String, Set<String>>();
	}
	
	@Override
	public void processTestCase(String tid) {
		ProjectData coverage = tdm.getCoverageObjectForTest(dbId, tid);
		Set<String> lineIds = CoberturaUtils.getIdsForLinesCovered(coverage);
		
		for(String lid : lineIds){
			if(map.containsKey(lid)){
				map.get(lid).add(tid);
			}
			else{
				map.put(lid, new HashSet<String>());
				map.get(lid).add(tid);
			}
		}
		
		this.clearAbstractData();
	}

	@Override
	public Set<String> getMetRequirementsForTestCase(String tid) {
		Set<String> ret = inverseMap.get(tid);
		if(ret == null){
			ret = new HashSet<String>();
			for(Entry<String, Set<String>> ent : map.entrySet()){
				if(ent.getValue().contains(tid)){
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
	public Set<String> getTestSetForRequirement(String rid) {
		return map.get(rid);
	}

}

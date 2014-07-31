package edu.umd.cs.guitar.testdata.hgs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public abstract class AbstractReducer implements HGSReducer {

	Map<Integer, Set<String>> cardMap;
	Map<Integer, Integer> lastSize;

	public void clearAbstractData() {
		if (cardMap != null) {
			cardMap = null;
			lastSize = null;
		}
	}

	public Set<String> getRequirementsWithSetsMatchingCardinality(
			int currentCard, Map<String, Boolean> marked) {
		if (this.cardMap == null) {
			this.cardMap = new HashMap<Integer, Set<String>>();
			this.lastSize = new HashMap<Integer, Integer>();
		}
		
		int markedCountNow = 0;
		
		if(marked != null){
			markedCountNow = computeMarkedCount(marked);
		}
		else{
			markedCountNow = 0;
		}
		
		Set<String> ret = this.cardMap.get(currentCard);
		if ((ret == null) || (markedCountNow > lastSize.get(currentCard))) {
			ret = new HashSet<String>();
			for(String rid : this.getAllRequirements()){
				boolean cardMatch = this.getTestSetForRequirement(rid).size() == currentCard; 
				if((marked == null) && cardMatch){
					ret.add(rid);
				}
				
				else if((marked != null) && (!marked.get(rid)) && cardMatch){
					ret.add(rid);
				}
			}

			this.cardMap.put(currentCard, ret);
			this.lastSize.put(currentCard, markedCountNow);
		}

		return this.cardMap.get(currentCard);
	}
	
	public int computeMarkedCount(Map<String, Boolean> mark){
		int count = 0;
		for(Entry<String, Boolean> ent : mark.entrySet()){
			if(ent.getValue()){
				count++;
			}
		}
		
		return count;
		
	}
	
	

	public List<String> getTestsFromSetsWithCardinality(int currentCard,
			Map<String, Boolean> marked) {
		Set<String> ret = new HashSet<String>();
		for (String rid : this.getRequirementsWithSetsMatchingCardinality(
				currentCard, marked)) {
			for (String tid : this.getTestSetForRequirement(rid)) {
				ret.add(tid);
			}
		}

		return getListFromSet(ret);
	}

	private List<String> getListFromSet(Set<String> in) {
		List<String> arrayList = new ArrayList<String>();
		arrayList.addAll(in);
		return arrayList;
	}

}

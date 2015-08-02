package edu.umd.cs.guitar.testdata.hgs;

import java.util.Set;

public interface HGSReducer {
	public void processTestCase(String id);
	public Set<String> getMetRequirementsForTestCase(String id);
	public Set<String> getAllRequirements();
	public Set<String> getTestSetForRequirement(String id);
}

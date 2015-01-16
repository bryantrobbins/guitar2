package edu.umd.cs.guitar.reduce.hgs;

import java.util.Set;

/**
 * An interface for building HGSReducers. Specifies a number of helper
 * methods needed to implement HGS on a generic domain.
 */
public interface HGSReducer {
    /**
     * Load in a test case given its id.
     *
     * @param id the test id
     */
    void processTestCase(String id);

    /**
     * Get the requirements covered by a given test case.
     *
     * @param id the test id
     * @return a set of covered requirements
     */
    Set<String> getMetRequirementsForTestCase(String id);

    /**
     * Get all requirements for the current problem.
     *
     * @return a set of requirements as Strings
     */
    Set<String> getAllRequirements();

    /**
     * Get the tests which cover a given requirement.
     *
     * @param id the requirement id
     * @return a set of covered test ids
     */
    Set<String> getTestSetForRequirement(String id);
}

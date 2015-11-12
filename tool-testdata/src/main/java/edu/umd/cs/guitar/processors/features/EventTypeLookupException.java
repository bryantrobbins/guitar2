package edu.umd.cs.guitar.processors.features;

/**
 * Created by bryan on 11/12/15.
 * <p/>
 * Just a wrapper for EventType lookup failures.
 */
public class EventTypeLookupException extends RuntimeException {

    /**
     * Create one of these wrapper exceptions given an eventId.
     *
     * @param eventId the ID of the event that could not be found
     */
    public EventTypeLookupException(final String eventId) {
        super("Tried to find EFG event with id " + eventId + " but found none");
    }

}

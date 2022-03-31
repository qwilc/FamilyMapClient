package service;

import dao.*;
import result.EventResult;

import java.sql.Connection;

/**
 * The EventService class performs the functionality for event requests with a specified ID
 */
public class EventService extends GetDataService {
    /**
     * Finds and returns data for the event with the given event ID if that event exists and is associated with the user indicated by the authentication token
     *
     * @param authtoken the authentication token
     * @param eventID the event identification
     * @return an EventResult with the outcome of the request
     */
    public EventResult event(String authtoken, String eventID) {
       return (EventResult) super.getData(authtoken, eventID);
    }

    @Override
    protected void initializeResult() {
        result = new EventResult(null, false, null, null, null, 0, 0, null, null, null, 0);
    }

    @Override
    protected DAO initializeDAO(Connection conn) {
        return new EventDAO(conn);
    }
}

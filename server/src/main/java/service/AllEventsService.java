package service;

import dao.*;
import model.Event;
import model.Model;
import result.AllEventsResult;
import result.Result;

import java.sql.Connection;

/**
 * The AllEventsService class performs the functionality for event requests without a specified ID
 */
public class AllEventsService extends GetDataService {
    /**
     * Finds and returns data for all the events associated with the user indicated by the authentication token
     *
     * @param authtoken the authentication token
     * @return an AllEventsResult object with the outcome of the request
     */
    public AllEventsResult events(String authtoken) {
        return (AllEventsResult) super.getData(authtoken, null);
    }

    @Override
    protected void initializeResult() {
        result = new AllEventsResult(null, false, null);
    }

    @Override
    protected DAO initializeDAO(Connection conn) {
        return new EventDAO(conn);
    }

    @Override
    protected void setResultData(Result result, Model[] array) {
        Event[] events = new Event[array.length];
        for(int i = 0; i < array.length; i++) {
            events[i] = (Event) array[i];
        }

        ((AllEventsResult) result).setData(events);
    }
}

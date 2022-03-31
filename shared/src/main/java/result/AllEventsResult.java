package result;

import model.Event;

/**
 * The AllEventsResult class stores the data for the result body for event requests without an eventID
 */
public class AllEventsResult extends Result {
    /**
     * an array of all the events associated with a given user
     */
    private Event[] data;

    /**
     * Creates an AllEventsResult object
     *
     * @param message The message to return in case of an error
     * @param success indicates whether the request was successful
     * @param data the events
     */
    public AllEventsResult(String message, boolean success, Event[] data) {
        super(message, success);
        this.data = data;
    }

    public Event[] getData() {
        return data;
    }

    public void setData(Event[] data) {
        this.data = data;
    }
}

package request;

import model.Event;
import model.Person;
import model.User;

/**
 * The LoadRequest class stores the data for the request body of a load request
 */
public class LoadRequest {
    /**
     * The users to load
     */
    private User[] users;
    /**
     * The people to load
     */
    private Person[] persons;
    /**
     * The events to load
     */
    private Event[] events;

    public LoadRequest() {}

    public LoadRequest(User[] users, Person[] persons, Event[] events) {
        this.users = users;
        this.persons = persons;
        this.events = events;
    }

    public User[] getUsers() {
        return users;
    }

    public void setUsers(User[] users) {
        this.users = users;
    }

    public Person[] getPersons() {
        return persons;
    }

    public void setPersons(Person[] persons) {
        this.persons = persons;
    }

    public Event[] getEvents() {
        return events;
    }

    public void setEvents(Event[] events) {
        this.events = events;
    }
}

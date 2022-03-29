package com.example.familymap;

import java.util.HashMap;
import java.util.Map;

import model.Event;
import model.Person;
import result.AllEventsResult;
import result.AllPeopleResult;

public class DataCache {
    private static DataCache instance;
    private static String serverHost;
    private static String serverPort;
    private static String authtoken;
    private static String userId;
    private static Map<String, Person> people;
    private static Map<String, Event> events;
    //Map<PersonID, List<Events>> personEvents;
    //Set<PersonID> paternalAncestors
    //Set<PersonID> maternalAncestors
    //Settings settings

    public static DataCache getInstance() {
        if(instance == null) {
            instance = new DataCache();
        }
        return instance;
    }

    private DataCache() {}

    public String getServerHost() {
        return serverHost;
    }
    public void setServerHost(String serverHost) {
        DataCache.serverHost = serverHost;
    }

    public String getServerPort() {
        return serverPort;
    }
    public void setServerPort(String serverPort) {
        DataCache.serverPort = serverPort;
    }

    public static String getAuthtoken() {
        return authtoken;
    }
    public static void setAuthtoken(String authtoken) {
        DataCache.authtoken = authtoken;
    }

    public static String getUserID() {
        return userId;
    }
    public static void setUserID(String userID) {
        DataCache.userId = userID;
    }

    public static Map<String, Person> getPeople() {
        return people;
    }

    public static Map<String, Event> getEvents() {
        return events;
    }

    public static void fillData(ServerProxy serverProxy) {
        fillPeopleData(serverProxy);
        fillEventData(serverProxy);
    }

    private static void fillPeopleData(ServerProxy serverProxy) {
        AllPeopleResult peopleResult = serverProxy.getPeople();
        people = new HashMap<>();
        for(Person person : peopleResult.getData()) {
            people.put(person.getPersonID(), person);
        }
    }

    private static void fillEventData(ServerProxy serverProxy) {
        AllEventsResult eventResult = serverProxy.getEvents();
        events = new HashMap<>();
        for(Event event : eventResult.getData()) {
            events.put(event.getEventID(), event);
        }
    }

    public static Person getPersonByID(String personID) {
        return people.get(personID);
    }

    public static String getUserFullName() {
        Person user = getPersonByID(userId);
        return user.getFirstName() + " " + user.getLastName();
    }

    //getEventByID(eventID)
    //List<Event> getPersonEvents
}

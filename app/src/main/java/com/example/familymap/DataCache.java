package com.example.familymap;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.HashMap;
import java.util.Locale;
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

    private static final Float[] colors = { //TODO: Does this work with the whole float vs Float thing?
            BitmapDescriptorFactory.HUE_AZURE,
            BitmapDescriptorFactory.HUE_BLUE,
            BitmapDescriptorFactory.HUE_CYAN,
            BitmapDescriptorFactory.HUE_GREEN,
            BitmapDescriptorFactory.HUE_MAGENTA,
            BitmapDescriptorFactory.HUE_ORANGE,
            BitmapDescriptorFactory.HUE_RED,
            BitmapDescriptorFactory.HUE_ROSE,
            BitmapDescriptorFactory.HUE_VIOLET,
            BitmapDescriptorFactory.HUE_YELLOW };
    private static Map<String, Float> eventColors;

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

    @RequiresApi(api = Build.VERSION_CODES.N)
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

    @RequiresApi(api = Build.VERSION_CODES.N) //TODO: I have no idea what this actually does but the IDE said to use it
    private static void fillEventData(ServerProxy serverProxy) {
        AllEventsResult eventResult = serverProxy.getEvents();
        events = new HashMap<>();
        for(Event event : eventResult.getData()) {
            events.put(event.getEventID(), event);
        }

        setEventColors();
    }

    public static Person getPersonByID(String personID) {
        return people.get(personID);
    }

    public static String getUserFullName() {
        Person user = getPersonByID(userId);
        return user.getFirstName() + " " + user.getLastName();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void setEventColors() {
        int i = 0;
        for(String eventID : events.keySet()) { //TODO: Better way to loop? Should I just use an iterator?
            eventColors.putIfAbsent(events.get(eventID).getEventType().toLowerCase(), colors[i]);
            if(i >= colors.length - 1) {
                i = 0;
            }
            else {
                i++;
            }
        }
    }

    public static Map<String, Float> getEventColors() {
        return eventColors;
    }

    //getEventByID(eventID)
    //List<Event> getPersonEvents
}

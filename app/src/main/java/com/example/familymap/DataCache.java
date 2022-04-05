package com.example.familymap;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
    private static Map<String, HashSet<Event>> personEvents;
    private static HashSet<String> paternalAncestors;
    private static HashSet<String> maternalAncestors;
    //Settings settings

//    private static final float[] colors = {
//            BitmapDescriptorFactory.HUE_AZURE,
//            BitmapDescriptorFactory.HUE_BLUE,
//            BitmapDescriptorFactory.HUE_CYAN,
//            BitmapDescriptorFactory.HUE_GREEN,
//            BitmapDescriptorFactory.HUE_MAGENTA,
//            BitmapDescriptorFactory.HUE_ORANGE,
//            BitmapDescriptorFactory.HUE_RED,
//            BitmapDescriptorFactory.HUE_ROSE,
//            BitmapDescriptorFactory.HUE_VIOLET,
//            BitmapDescriptorFactory.HUE_YELLOW };

        private static final float[] colors = { //TODO: Does this work with the whole float vs Float thing?
                BitmapDescriptorFactory.HUE_YELLOW,
                BitmapDescriptorFactory.HUE_AZURE,
                BitmapDescriptorFactory.HUE_ROSE,
                BitmapDescriptorFactory.HUE_GREEN,
                BitmapDescriptorFactory.HUE_RED,
                BitmapDescriptorFactory.HUE_BLUE,
                BitmapDescriptorFactory.HUE_ORANGE,
                BitmapDescriptorFactory.HUE_VIOLET,
                BitmapDescriptorFactory.HUE_CYAN,
                BitmapDescriptorFactory.HUE_MAGENTA,
             };
    private static HashMap<String, Float> eventColors;

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

    private static void fillAncestorData() {
        Person user = getPersonByID(userId);

        String fatherID = user.getFatherID();
        if(fatherID != null) {
            paternalAncestors = getPersonAncestors(fatherID);
        }

        String motherID = user.getMotherID();
        if(motherID != null) {
            maternalAncestors = getPersonAncestors(user.getMotherID());
        }
    }

    private static HashSet<String> getPersonAncestors(String personID) {
        HashSet<String> ancestors = new HashSet<>();
        getPersonAncestorsHelper(ancestors, personID);
        return ancestors;
    }

    private static void getPersonAncestorsHelper(HashSet<String> ancestors, String personID) {
        ancestors.add(personID);

        Person person = getPersonByID(personID);

        if(person.getFatherID() != null) {
            getPersonAncestorsHelper(ancestors, person.getFatherID());
        }

        if(person.getMotherID() != null) {
            getPersonAncestorsHelper(ancestors, person.getMotherID());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N) //TODO: Get rid of any of these that remain
    private static void fillEventData(ServerProxy serverProxy) {
        AllEventsResult eventResult = serverProxy.getEvents();
        events = new HashMap<>();
        personEvents = new HashMap<>();
        for(Event event : eventResult.getData()) {
            events.put(event.getEventID(), event);

            personEvents.putIfAbsent(event.getPersonID(), new HashSet<>());
            personEvents.get(event.getPersonID()).add(event);
        }

        setEventColors();
    }

    public static Person getPersonByID(String personID) {
        return people.get(personID);
    }

    public static String getUserFullName() {
        return getPersonFullName(userId);
    }

    public static String getPersonFullName(String personId) {
        Person person = getPersonByID(personId);
        return person.getFirstName() + " " + person.getLastName();
    }

    public static void setEventColors() {
        int i = 0;
        eventColors = new HashMap<>();
        for(String eventID : events.keySet()) { //TODO: Better way to loop? Should I just use an iterator?
            String eventType = events.get(eventID).getEventType().toLowerCase();
            Float putReturnValue = eventColors.putIfAbsent(eventType, colors[i]);
            if(putReturnValue == null && i >= colors.length - 1) {
                i = 0;
            }
            else if(putReturnValue == null){
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

package com.example.familymap;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import model.Event;
import model.FamilyMember;
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
    private static Map<String, List<Event>> personEvents;
    private static HashSet<String> paternalAncestors;
    private static HashSet<String> maternalAncestors;

    private static boolean isEventActivity = false;
    private static Person selectedPerson;
    private static List<FamilyMember> family;
    private static Event selectedEvent;

    //private static boolean areUnappliedSettings = false; TODO: Could do this right if time
    private static boolean showSpouseLines; //TODO: Will DataCache get wiped when the app closes? If so, need to update on reopening
    private static boolean showAncestorLines;
    private static boolean showLifeStoryLines;
    private static boolean showFatherSide;
    private static boolean showMotherSide;
    private static boolean showMaleEvents;
    private static boolean showFemaleEvents;

        private static final float[] colors = {
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

    public static HashSet<String> getPaternalAncestors() {
        return paternalAncestors;
    }

    public static HashSet<String> getMaternalAncestors() {
        return maternalAncestors;
    }

    public static Person getSelectedPerson() {
        return selectedPerson;
    }

    public static void setSelectedPerson(Person selectedPerson) {
        DataCache.selectedPerson = selectedPerson;
    }

    public static void setSelectedPerson(String personID) {
        selectedPerson = people.get(personID);
    }

    public static Event getSelectedEvent() {
        return selectedEvent;
    }

    public static void setSelectedEvent(Event selectedEvent) {
        DataCache.selectedEvent = selectedEvent;
    }

    public static void initializeSettings(SharedPreferences preferences) {
        DataCache.setShowLifeStoryLines(preferences.getBoolean("life_story", true));
        DataCache.setShowAncestorLines(preferences.getBoolean("family_tree", true));
        DataCache.setShowSpouseLines(preferences.getBoolean("spouse", true));
        DataCache.setShowFatherSide(preferences.getBoolean("father_side", true));
        DataCache.setShowMotherSide(preferences.getBoolean("mother_side", true));
        DataCache.setShowMaleEvents(preferences.getBoolean("male_events", true));
        DataCache.setShowFemaleEvents(preferences.getBoolean("female_events", true));
    }

    public static boolean showSpouseLines() {
        return showSpouseLines;
    }

    public static void setShowSpouseLines(boolean showSpouseLines) {
        DataCache.showSpouseLines = showSpouseLines;
    }

    public static boolean showAncestorLines() {
        return showAncestorLines;
    }

    public static void setShowAncestorLines(boolean showAncestorLines) {
        DataCache.showAncestorLines = showAncestorLines;
    }

    public static boolean showLifeStoryLines() {
        return showLifeStoryLines;
    }

    public static void setShowLifeStoryLines(boolean showLifeStoryLines) {
        DataCache.showLifeStoryLines = showLifeStoryLines;
    }

    public static boolean showFatherSide() {
        return showFatherSide;
    }

    public static void setShowFatherSide(boolean showFatherSide) {
        DataCache.showFatherSide = showFatherSide;
    }

    public static boolean showMotherSide() {
        return showMotherSide;
    }

    public static void setShowMotherSide(boolean showMotherSide) {
        DataCache.showMotherSide = showMotherSide;
    }

    public static boolean showMaleEvents() {
        return showMaleEvents;
    }

    public static void setShowMaleEvents(boolean showMaleEvents) {
        DataCache.showMaleEvents = showMaleEvents;
    }

    public static boolean showFemaleEvents() {
        return showFemaleEvents;
    }

    public static void setShowFemaleEvents(boolean showFemaleEvents) {
        DataCache.showFemaleEvents = showFemaleEvents;
    }

    public static void fillData(ServerProxy serverProxy) {
        fillPeopleData(serverProxy);
        fillEventData(serverProxy);
        fillAncestorData();
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

    private static void fillEventData(ServerProxy serverProxy) {
        AllEventsResult eventResult = serverProxy.getEvents();
        events = new HashMap<>();
        personEvents = new HashMap<String, List<Event>>();
        for(Event event : eventResult.getData()) {
            events.put(event.getEventID(), event);

            personEvents.putIfAbsent(event.getPersonID(), new ArrayList<>());
            personEvents.get(event.getPersonID()).add(event);
        }

        setEventColors();
    }

    public static Person getPersonByID(String personID) {
        return people.get(personID);
    }

    public static String getUserFullName() {
        return getFullName(userId);
    }

    public static String getFullName(String personId) {
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

    public static boolean isEventActivity() {
        return isEventActivity;
    }

    public static void setIsEventActivity(boolean isEventActivity) {
        DataCache.isEventActivity = isEventActivity;
    }

    public static String eventInfoString(Event event) {
        return event.getEventType().toUpperCase() + ": "
                + event.getCity() + ", " + event.getCountry()
                + " (" + event.getYear() + ")";
    }

    public static List<Event> getPersonEvents(Person person) {
        String personID = person.getPersonID();
        Collections.sort(personEvents.get(personID)); //TODO: Best place to sort?
        return personEvents.get(personID);
    }

    public static List<Event> getPersonEvents(String personID) {
        Collections.sort(personEvents.get(personID));
        return personEvents.get(personID);
    }

    public static List<FamilyMember> getSelectedPersonFamily() { //TODO: Does this need to be filtered?
        assert selectedPerson != null;

        if(family == null) {
            family = new ArrayList<>();
        }
        else {
            family.clear();
        }

        String fatherID = selectedPerson.getFatherID();
        String motherID = selectedPerson.getMotherID();
        String spouseID = selectedPerson.getSpouseID();

        addToFamily(fatherID, "Father");
        addToFamily(motherID, "Mother");
        addToFamily(spouseID, "Spouse");
        addChildrenToFamily();

        return family;
    }

    private static void addChildrenToFamily () {
        for(Map.Entry<String, Person> entry : people.entrySet()) { //TODO: Better iteration method?
            Person person = entry.getValue();
            assert person != null;

            String parentID;
            if(selectedPerson.getGender().equals("m")) {
                parentID = person.getFatherID();
            }
            else {
                parentID = person.getMotherID();
            }

            if(parentID != null && parentID.equals(selectedPerson.getPersonID() ) ) {
                String personID = entry.getKey();
                addToFamily(personID, "Child");
            }
        }
    }

    private static void addToFamily(String personID, String relation) {
        if(personID != null) {
            FamilyMember familyMember = new FamilyMember(getFullName(personID), relation, personID);
            family.add(familyMember);
        }
    }

    public static Event getPersonFirstEvent(String personID) {
        return getPersonEvents(personID).get(0);
    }

    public static boolean isEventShown(Event event) {
        String gender = getGenderFromEvent(event);
        String personID = event.getPersonID();

        //If the event falls into a category that is currently disabled, return false; else, return true
        return !(paternalAncestors.contains(personID) && !DataCache.showFatherSide()
        || maternalAncestors.contains(personID) && !DataCache.showMotherSide
        || gender.equals("m") && !DataCache.showMaleEvents()
        || gender.equals("f") && !DataCache.showFemaleEvents());

    }

    private static String getGenderFromEvent(Event event) {
        String personID = event.getPersonID();
        return getPersonByID(personID).getGender(); //TODO: How much is reasonable for separating out this line into separate variables?
    }

    public static List<Person> filterPeopleByQuery(String query) {
        List<Person> matches = new ArrayList<>();

        for(Person person : people.values()) {
            String name = getFullName(person.getPersonID()).toLowerCase();
            if(name.contains(query)) {
                matches.add(person);
            }
        }

        return matches;
    }

    //TODO: Could we theoretically have a class/method that takes in a list of items and
    // determines what function to call to get the string based on the item's class and
    // then performs the query filtering?
    public static List<Event> filterEventsByQuery(String query) {
        List<Event> matches = new ArrayList<>();

        for(Event event : events.values()) {
            String details = eventInfoString(event).toLowerCase();
            if(details.contains(query) && isEventShown(event)) {
                matches.add(event);
            }
        }

        return matches;
    }

    public static IconDrawable getGenderIcon(Person person, Context context) {
        IconDrawable iconDrawable;
        if(person.getGender().equals("m")) {
            iconDrawable = new IconDrawable(context, FontAwesomeIcons.fa_male)
                    .color(R.color.purple_500)
                    .sizeRes(R.dimen.icon_size);
        }
        else {
            iconDrawable = new IconDrawable(context, FontAwesomeIcons.fa_female)
                    .colorRes(R.color.purple_200)
                    .sizeRes(R.dimen.icon_size);
        }

        return iconDrawable;
    }

    public static IconDrawable getEventIcon(Context context) {
        return new IconDrawable(context, FontAwesomeIcons.fa_map_marker)
                .colorRes(R.color.white)
                .sizeRes(R.dimen.icon_size);
    }


    //getEventByID(eventID)
}

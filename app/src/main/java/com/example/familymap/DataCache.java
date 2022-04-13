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
//    private static String serverHost; TODO: Can prolly delete
//    private static String serverPort;
    private static String authtoken;
    private static String userId;

    private static Map<String, Person> people;
    private static Map<String, Event> events;
    private static Map<String, List<Event>> personEvents;
    private static HashSet<String> paternalAncestors;
    private static HashSet<String> maternalAncestors;

    private static boolean isEventActivity = false;
    private static Person selectedPerson;
    private static Event selectedEvent;
    private static Event savedSelectedEvent;
    private static List<FamilyMember> family;

    private static boolean isSpouseLineEnabled;
    private static boolean isFamilyTreeEnabled;
    private static boolean isLifeStoryEnabled;
    private static boolean isFatherSideEnabled;
    private static boolean isMotherSideEnabled;
    private static boolean isMaleEventsEnabled;
    private static boolean isFemaleEventsEnabled;

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

//    public String getServerHost() { TODO: Can prolly delete
//        return serverHost;
//    }
//    public void setServerHost(String serverHost) {
//        DataCache.serverHost = serverHost;
//    }
//
//    public String getServerPort() {
//        return serverPort;
//    }
//    public void setServerPort(String serverPort) {
//        DataCache.serverPort = serverPort;
//    }

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

    public static void saveSelectedEvent() {
        savedSelectedEvent = selectedEvent;
    }

    public static void loadSavedEvent() {
        if(savedSelectedEvent != null) {
            selectedEvent = savedSelectedEvent;
            setSelectedPerson(selectedEvent.getPersonID());
        }
    }

    public static void resetData() {
        isEventActivity = false;
        selectedEvent = null;
        selectedPerson = null;
        savedSelectedEvent = null;
    }

    public static void initializeSettings(SharedPreferences preferences) {
        DataCache.setIsLifeStoryEnabled(preferences.getBoolean("life_story", true));
        DataCache.setIsFamilyTreeEnabled(preferences.getBoolean("family_tree", true));
        DataCache.setIsSpouseLineEnabled(preferences.getBoolean("spouse", true));
        DataCache.setIsFatherSideEnabled(preferences.getBoolean("father_side", true));
        DataCache.setIsMotherSideEnabled(preferences.getBoolean("mother_side", true));
        DataCache.setIsMaleEventsEnabled(preferences.getBoolean("male_events", true));
        DataCache.setIsFemaleEventsEnabled(preferences.getBoolean("female_events", true));
    }

    public static boolean isSpouseLineEnabled() {
        return isSpouseLineEnabled;
    }

    public static void setIsSpouseLineEnabled(boolean isSpouseLineEnabled) {
        DataCache.isSpouseLineEnabled = isSpouseLineEnabled;
    }

    public static boolean isFamilyTreeEnabled() {
        return isFamilyTreeEnabled;
    }

    public static void setIsFamilyTreeEnabled(boolean isFamilyTreeEnabled) {
        DataCache.isFamilyTreeEnabled = isFamilyTreeEnabled;
    }

    public static boolean isLifeStoryEnabled() {
        return isLifeStoryEnabled;
    }

    public static void setIsLifeStoryEnabled(boolean isLifeStoryEnabled) {
        DataCache.isLifeStoryEnabled = isLifeStoryEnabled;
    }

    public static boolean isFatherSideEnabled() {
        return isFatherSideEnabled;
    }

    public static void setIsFatherSideEnabled(boolean isFatherSideEnabled) {
        DataCache.isFatherSideEnabled = isFatherSideEnabled;
    }

    public static boolean isMotherSideEnabled() {
        return isMotherSideEnabled;
    }

    public static void setIsMotherSideEnabled(boolean isMotherSideEnabled) {
        DataCache.isMotherSideEnabled = isMotherSideEnabled;
    }

    public static boolean isMaleEventsEnabled() {
        return isMaleEventsEnabled;
    }

    public static void setIsMaleEventsEnabled(boolean isMaleEventsEnabled) {
        DataCache.isMaleEventsEnabled = isMaleEventsEnabled;
    }

    public static boolean isFemaleEventsEnabled() {
        return isFemaleEventsEnabled;
    }

    public static void setIsFemaleEventsEnabled(boolean isFemaleEventsEnabled) {
        DataCache.isFemaleEventsEnabled = isFemaleEventsEnabled;
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
        personEvents = new HashMap<>();
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
        for(Event event : events.values()) {
            String eventType = event.getEventType().toLowerCase();
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
        return getPersonEvents(personID);
    }

    public static List<Event> getPersonEvents(String personID) {
        List<Event> events = personEvents.get(personID);
        assert events != null;
        Collections.sort(events);
        return events;
    }

    public static List<FamilyMember> getSelectedPersonFamily() {
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
        for(Map.Entry<String, Person> entry : people.entrySet()) {
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
        return !(paternalAncestors.contains(personID) && !DataCache.isFatherSideEnabled()
        || maternalAncestors.contains(personID) && !DataCache.isMotherSideEnabled
        || gender.equals("m") && !DataCache.isMaleEventsEnabled()
        || gender.equals("f") && !DataCache.isFemaleEventsEnabled());

    }

    private static String getGenderFromEvent(Event event) {
        String personID = event.getPersonID();
        return getPersonByID(personID).getGender();
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
}

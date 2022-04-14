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
    private String authtoken;
    private String userId;

    private Map<String, Person> people;
    private Map<String, Event> events;
    private Map<String, List<Event>> personEvents;
    private HashSet<String> paternalAncestors;
    private HashSet<String> maternalAncestors;

    private boolean isEventActivity = false;
    private Person selectedPerson;
    private Event selectedEvent;
    private Event savedSelectedEvent;
    private List<FamilyMember> family;

    private boolean isSpouseLineEnabled;
    private boolean isFamilyTreeEnabled;
    private boolean isLifeStoryEnabled;
    private boolean isFatherSideEnabled;
    private boolean isMotherSideEnabled;
    private boolean isMaleEventsEnabled;
    private boolean isFemaleEventsEnabled;

    private final float[] colors = {
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
    private HashMap<String, Float> eventColors;

    public static DataCache getInstance() {
        if(instance == null) {
            instance = new DataCache();
        }
        return instance;
    }

    private DataCache() {}

    public String getAuthtoken() {
        return authtoken;
    }
    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }

    public String getUserID() {
        return userId;
    }
    public void setUserID(String userID) {
        this.userId = userID;
    }

    public Map<String, Person> getPeople() {
        return people;
    }

    public Map<String, Event> getEvents() {
        return events;
    }

    public HashSet<String> getPaternalAncestors() {
        return paternalAncestors;
    }

    public HashSet<String> getMaternalAncestors() {
        return maternalAncestors;
    }

    public Person getSelectedPerson() {
        return selectedPerson;
    }

    public void setSelectedPerson(Person selectedPerson) {
        this.selectedPerson = selectedPerson;
    }
    public void setSelectedPerson(String personID) {
        selectedPerson = people.get(personID);
    }

    public Event getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(Event selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    public void saveSelectedEvent() {
        savedSelectedEvent = selectedEvent;
    }

    public void loadSavedEvent() {
        if(savedSelectedEvent != null) {
            selectedEvent = savedSelectedEvent;
            setSelectedPerson(selectedEvent.getPersonID());
        }
    }

    public void resetData() {
        isEventActivity = false;
        selectedEvent = null;
        selectedPerson = null;
        savedSelectedEvent = null;
    }

    public void initializeSettings(SharedPreferences preferences) {
        this.setIsLifeStoryEnabled(preferences.getBoolean("life_story", true));
        this.setIsFamilyTreeEnabled(preferences.getBoolean("family_tree", true));
        this.setIsSpouseLineEnabled(preferences.getBoolean("spouse", true));
        this.setIsFatherSideEnabled(preferences.getBoolean("father_side", true));
        this.setIsMotherSideEnabled(preferences.getBoolean("mother_side", true));
        this.setIsMaleEventsEnabled(preferences.getBoolean("male_events", true));
        this.setIsFemaleEventsEnabled(preferences.getBoolean("female_events", true));
    }

    public boolean isSpouseLineEnabled() {
        return isSpouseLineEnabled;
    }

    public void setIsSpouseLineEnabled(boolean isSpouseLineEnabled) {
        this.isSpouseLineEnabled = isSpouseLineEnabled;
    }

    public boolean isFamilyTreeEnabled() {
        return isFamilyTreeEnabled;
    }

    public void setIsFamilyTreeEnabled(boolean isFamilyTreeEnabled) {
        this.isFamilyTreeEnabled = isFamilyTreeEnabled;
    }

    public boolean isLifeStoryEnabled() {
        return isLifeStoryEnabled;
    }

    public void setIsLifeStoryEnabled(boolean isLifeStoryEnabled) {
        this.isLifeStoryEnabled = isLifeStoryEnabled;
    }

    public boolean isFatherSideEnabled() {
        return isFatherSideEnabled;
    }

    public void setIsFatherSideEnabled(boolean isFatherSideEnabled) {
        this.isFatherSideEnabled = isFatherSideEnabled;
    }

    public boolean isMotherSideEnabled() {
        return isMotherSideEnabled;
    }

    public void setIsMotherSideEnabled(boolean isMotherSideEnabled) {
        this.isMotherSideEnabled = isMotherSideEnabled;
    }

    public boolean isMaleEventsEnabled() {
        return isMaleEventsEnabled;
    }

    public void setIsMaleEventsEnabled(boolean isMaleEventsEnabled) {
        this.isMaleEventsEnabled = isMaleEventsEnabled;
    }

    public boolean isFemaleEventsEnabled() {
        return isFemaleEventsEnabled;
    }

    public void setIsFemaleEventsEnabled(boolean isFemaleEventsEnabled) {
        this.isFemaleEventsEnabled = isFemaleEventsEnabled;
    }

    public void fillData(ServerProxy serverProxy) {
        fillPeopleData(serverProxy);
        fillEventData(serverProxy);
        fillAncestorData();
    }

    private void fillPeopleData(ServerProxy serverProxy) {
        AllPeopleResult peopleResult = serverProxy.getPeople();
        people = new HashMap<>();
        for(Person person : peopleResult.getData()) {
            people.put(person.getPersonID(), person);
        }
    }

    private void fillAncestorData() {
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

    private HashSet<String> getPersonAncestors(String personID) {
        HashSet<String> ancestors = new HashSet<>();
        getPersonAncestorsHelper(ancestors, personID);
        return ancestors;
    }

    private void getPersonAncestorsHelper(HashSet<String> ancestors, String personID) {
        ancestors.add(personID);

        Person person = getPersonByID(personID);

        if(person.getFatherID() != null) {
            getPersonAncestorsHelper(ancestors, person.getFatherID());
        }

        if(person.getMotherID() != null) {
            getPersonAncestorsHelper(ancestors, person.getMotherID());
        }
    }

    private void fillEventData(ServerProxy serverProxy) {
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

    public Person getPersonByID(String personID) {
        return people.get(personID);
    }

    public String getUserFullName() {
        return getFullName(userId);
    }

    public String getFullName(String personId) {
        Person person = getPersonByID(personId);
        return person.getFirstName() + " " + person.getLastName();
    }

    public void setEventColors() {
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

    public Map<String, Float> getEventColors() {
        return eventColors;
    }

    public boolean isEventActivity() {
        return isEventActivity;
    }

    public void setIsEventActivity(boolean isEventActivity) {
        this.isEventActivity = isEventActivity;
    }

    public String eventInfoString(Event event) {
        return event.getEventType().toUpperCase() + ": "
                + event.getCity() + ", " + event.getCountry()
                + " (" + event.getYear() + ")";
    }

    public List<Event> getPersonEvents(Person person) {
        String personID = person.getPersonID();
        return getPersonEvents(personID);
    }

    public List<Event> getPersonEvents(String personID) {
        List<Event> events = personEvents.get(personID);
        assert events != null;
        Collections.sort(events);
        return events;
    }

    public List<FamilyMember> getSelectedPersonFamily() {
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

    private void addChildrenToFamily () {
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

    private void addToFamily(String personID, String relation) {
        if(personID != null) {
            FamilyMember familyMember = new FamilyMember(getFullName(personID), relation, personID);
            family.add(familyMember);
        }
    }

    public Event getPersonFirstEvent(String personID) {
        return getPersonEvents(personID).get(0);
    }

    public boolean isEventShown(Event event) {
        String gender = getGenderFromEvent(event);
        String personID = event.getPersonID();

        //If the event falls into a category that is currently disabled, return false; else, return true
        return !(paternalAncestors.contains(personID) && !this.isFatherSideEnabled()
        || maternalAncestors.contains(personID) && !this.isMotherSideEnabled
        || gender.equals("m") && !this.isMaleEventsEnabled()
        || gender.equals("f") && !this.isFemaleEventsEnabled());

    }

    private String getGenderFromEvent(Event event) {
        String personID = event.getPersonID();
        return getPersonByID(personID).getGender();
    }

    public List<Person> filterPeopleByQuery(String query) {
        List<Person> matches = new ArrayList<>();

        for(Person person : people.values()) {
            String name = getFullName(person.getPersonID()).toLowerCase();
            if(name.contains(query)) {
                matches.add(person);
            }
        }

        return matches;
    }

    public List<Event> filterEventsByQuery(String query) {
        List<Event> matches = new ArrayList<>();

        for(Event event : events.values()) {
            String details = eventInfoString(event).toLowerCase();
            if(details.contains(query) && isEventShown(event)) {
                matches.add(event);
            }
        }

        return matches;
    }

    public IconDrawable getGenderIcon(Person person, Context context) {
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

    public IconDrawable getEventIcon(Context context) {
        return new IconDrawable(context, FontAwesomeIcons.fa_map_marker)
                .colorRes(R.color.white)
                .sizeRes(R.dimen.icon_size);
    }
}

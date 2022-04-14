package com.example.familymap;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import model.Event;
import model.FamilyMember;
import model.Person;
import request.LoginRequest;

public class DataCacheTest {
    private final DataCache dataCache = DataCache.getInstance();
    private final ServerProxy serverProxy = new ServerProxy("localhost", "8080");

    @Before
    public void setUp() {
        LoginRequest request = new LoginRequest("sheila", "parker");
        serverProxy.login(request);
    }

    @Test
    public void testAllFamilyRelations() {
        dataCache.setSelectedPerson("Betty_White");
        List<FamilyMember> family = dataCache.getSelectedPersonFamily();

        assertNotNull(family);
        assertEquals(4, family.size());

        for(FamilyMember familyMember : family) {
            String ID = familyMember.getPersonID();
            String relation = familyMember.getRelation();
            String expectedRelation;

            switch(ID) {
                case "Sheila_Parker":
                    expectedRelation = "Child";
                    break;
                case "Blaine_McGary":
                    expectedRelation = "Spouse";
                    break;
                case "Frank_Jones":
                    expectedRelation = "Father";
                    break;
                case "Mrs_Jones":
                    expectedRelation = "Mother";
                    break;
                default:
                    expectedRelation = "We should not reach the default statement";
                    break;
            }

            assertEquals(expectedRelation, relation);
        }
    }

    @Test
    public void testOnlyParentRelations() {
        LoginRequest request = new LoginRequest("patrick", "spencer");
        serverProxy.login(request);

        dataCache.setSelectedPerson("Patrick_Spencer");
        List<FamilyMember> family = dataCache.getSelectedPersonFamily();

        assertNotNull(family);
        assertEquals(2, family.size());

        for (FamilyMember familyMember : family) {
            String ID = familyMember.getPersonID();
            String relation = familyMember.getRelation();
            String expectedRelation;

            if (ID.equals("Happy_Birthday")) {
                expectedRelation = "Father";
            }
            else {
                expectedRelation = "Mother";
            }

            assertEquals(expectedRelation, relation);
        }
    }

    @Test
    public void testMultipleChildRelations() {
        Person parent = new Person("parent", "x", "x", "x", "f", null, null, null);
        Person child1 = new Person("child1", "x", "x", "x", "f", null, "parent", null);
        Person child2 = new Person("child2", "x", "x", "x", "f", null, "parent", null);

        dataCache.getPeople().put("parent", parent);
        dataCache.getPeople().put("child1", child1);
        dataCache.getPeople().put("child2", child2);

        dataCache.setSelectedPerson(parent);
        List<FamilyMember> family = dataCache.getSelectedPersonFamily();

        assertNotNull(family);
        assertEquals(2, family.size());

        for (FamilyMember familyMember : family) {
            assertEquals("Child", familyMember.getRelation());
        }

        dataCache.getPeople().remove("parent", parent);
        dataCache.getPeople().remove("child1", child1);
        dataCache.getPeople().remove("child2", child2);
    }

    @Test
    public void testFilterFatherSide() {
        setSettingsToTrue();
        dataCache.setIsFatherSideEnabled(false);
        checkFatherSideEvents(false);
        checkMotherSideEvents(true);
    }

    @Test
    public void testFilterMotherSide() {
        setSettingsToTrue();
        dataCache.setIsMotherSideEnabled(false);
        checkFatherSideEvents(true);
        checkMotherSideEvents(false);
    }

    @Test
    public void testFilterBothSides() {
        setSettingsToTrue();
        dataCache.setIsFatherSideEnabled(false);
        dataCache.setIsMotherSideEnabled(false);
        checkFatherSideEvents(false);
        checkMotherSideEvents(false);

        assertTrue(dataCache.isEventShown(dataCache.getPersonFirstEvent("Sheila_Parker")));
        assertTrue(dataCache.isEventShown(dataCache.getPersonFirstEvent("Davis_Hyer")));
    }

    @Test
    public void testFilterMaleEvents() {
        setSettingsToTrue();
        dataCache.setIsMaleEventsEnabled(false);
        checkEventsByGender("m", false);
        checkEventsByGender("f", true);
    }

    @Test
    public void testFilterFemaleEvents() {
        setSettingsToTrue();
        dataCache.setIsFemaleEventsEnabled(false);
        checkEventsByGender("m", true);
        checkEventsByGender("f", false);
    }

    @Test
    public void testFilterAllEventsByGender() {
        setSettingsToTrue();
        dataCache.setIsMaleEventsEnabled(false);
        dataCache.setIsFemaleEventsEnabled(false);

        for(String personID : dataCache.getPeople().keySet()) {
            assertFalse(dataCache.isEventShown(dataCache.getPersonFirstEvent(personID)));
        }
    }

    @Test
    public void testMaleAndMotherSideFilters() {
        setSettingsToTrue();
        dataCache.setIsMaleEventsEnabled(false);
        dataCache.setIsMotherSideEnabled(false);

        assertFalse(dataCache.isEventShown(dataCache.getPersonFirstEvent("Frank_Jones")));
        assertFalse(dataCache.isEventShown(dataCache.getPersonFirstEvent("Mrs_Jones")));
        assertFalse(dataCache.isEventShown(dataCache.getPersonFirstEvent("Betty_White")));
        assertFalse(dataCache.isEventShown(dataCache.getPersonFirstEvent("Ken_Rodham")));
        assertFalse(dataCache.isEventShown(dataCache.getPersonFirstEvent("Blaine_McGary")));
        assertFalse(dataCache.isEventShown(dataCache.getPersonFirstEvent("Davis_Hyer")));

        assertTrue(dataCache.isEventShown(dataCache.getPersonFirstEvent("Mrs_Rodham")));
        assertTrue(dataCache.isEventShown(dataCache.getPersonFirstEvent("Sheila_Parker")));
    }

    @Test
    public void testFemaleAndFatherSideFilters() {
        setSettingsToTrue();
        dataCache.setIsFemaleEventsEnabled(false);
        dataCache.setIsFatherSideEnabled(false);

        assertFalse(dataCache.isEventShown(dataCache.getPersonFirstEvent("Mrs_Jones")));
        assertFalse(dataCache.isEventShown(dataCache.getPersonFirstEvent("Betty_White")));
        assertFalse(dataCache.isEventShown(dataCache.getPersonFirstEvent("Ken_Rodham")));
        assertFalse(dataCache.isEventShown(dataCache.getPersonFirstEvent("Blaine_McGary")));
        assertFalse(dataCache.isEventShown(dataCache.getPersonFirstEvent("Mrs_Rodham")));
        assertFalse(dataCache.isEventShown(dataCache.getPersonFirstEvent("Sheila_Parker")));

        assertTrue(dataCache.isEventShown(dataCache.getPersonFirstEvent("Davis_Hyer")));
        assertTrue(dataCache.isEventShown(dataCache.getPersonFirstEvent("Frank_Jones")));
    }

    private void setSettingsToTrue() {
        dataCache.setIsFatherSideEnabled(true);
        dataCache.setIsMotherSideEnabled(true);
        dataCache.setIsMaleEventsEnabled(true);
        dataCache.setIsFemaleEventsEnabled(true);
    }

    private void checkFatherSideEvents(boolean shouldEventsShow) {
        for(String personID : dataCache.getPaternalAncestors()) {
            assertEquals(shouldEventsShow, dataCache.isEventShown(dataCache.getPersonFirstEvent(personID)));
        }
    }

    private void checkMotherSideEvents(boolean shouldEventsShow) {
        for(String personID : dataCache.getMaternalAncestors()) {
            assertEquals(shouldEventsShow, dataCache.isEventShown(dataCache.getPersonFirstEvent(personID)));
        }
    }

    private void checkEventsByGender(String gender, boolean shouldEventsShow) {
        for(Person person : dataCache.getPeople().values()) {
            if(person.getGender().equals(gender)) {
                Event event = dataCache.getPersonFirstEvent(person.getPersonID());
                assertEquals(shouldEventsShow, dataCache.isEventShown(event));
            }
        }
    }

    @Test
    public void testEventSort() {
        List<Event> testEvents = dataCache.getPersonEvents("Sheila_Parker");

        assertNotNull(testEvents);
        assertEquals(5, testEvents.size());
        assertEquals("Sheila_Birth", testEvents.get(0).getEventID());
        assertEquals("Sheila_Marriage", testEvents.get(1).getEventID());
        assertEquals("completed asteroids", testEvents.get(2).getEventType().toLowerCase());
        assertEquals("completed asteroids", testEvents.get(3).getEventType().toLowerCase());
        assertEquals("Sheila_Death", testEvents.get(4).getEventID());
    }

    @Test
    public void testSingleEventSort() {
        List<Event> testEvents = dataCache.getPersonEvents("Betty_White");

        assertNotNull(testEvents);
        assertEquals(1, testEvents.size());
        assertEquals("Betty_Death", testEvents.get(0).getEventID());
    }

    @Test
    public void testSameYearEventSort() {
        List<Event> testEvents = dataCache.getPersonEvents("Mrs_Rodham");

        assertNotNull(testEvents);
        assertEquals(2, testEvents.size());
        assertEquals("Mrs_Rodham_Backflip", testEvents.get(0).getEventID());
        assertEquals("Mrs_Rodham_Java", testEvents.get(1).getEventID());
    }

    @Test
    public void testSearchQuery() {
        setSettingsToTrue();

        String query = "ar";
        List<Person> matchingPeople = dataCache.filterPeopleByQuery(query);
        List<Event> matchingEvents = dataCache.filterEventsByQuery(query);

        assertNotNull(matchingPeople);
        assertEquals(2, matchingPeople.size());

        assertNotNull(matchingEvents);
        assertEquals(8, matchingEvents.size());
    }

    @Test
    public void testNumericalSearchQuery() {
        setSettingsToTrue();

        String query = "189";
        List<Person> matchingPeople = dataCache.filterPeopleByQuery(query);
        List<Event> matchingEvents = dataCache.filterEventsByQuery(query);

        assertNotNull(matchingPeople);
        assertEquals(0, matchingPeople.size());

        assertNotNull(matchingEvents);
        assertEquals(3, matchingEvents.size());
    }

    @Test
    public void testNoMatchSearchQuery() {
        setSettingsToTrue();

        String query = "zzz";
        List<Person> matchingPeople = dataCache.filterPeopleByQuery(query);
        List<Event> matchingEvents = dataCache.filterEventsByQuery(query);

        assertNotNull(matchingPeople);
        assertEquals(0, matchingPeople.size());

        assertNotNull(matchingEvents);
        assertEquals(0, matchingEvents.size());
    }
}

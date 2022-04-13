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
    private final ServerProxy serverProxy = new ServerProxy("localhost", "8080");

    @Before
    public void setUp() {
        LoginRequest request = new LoginRequest("sheila", "parker");
        serverProxy.login(request);
    }

    @Test
    public void testAllFamilyRelations() {
        DataCache.setSelectedPerson("Betty_White");
        List<FamilyMember> family = DataCache.getSelectedPersonFamily();

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

        DataCache.setSelectedPerson("Patrick_Spencer");
        List<FamilyMember> family = DataCache.getSelectedPersonFamily();

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
    public void testFilterFatherSide() {
        setSettingsToTrue();
        DataCache.setShowFatherSide(false);
        checkFatherSideEvents(false);
        checkMotherSideEvents(true);
    }

    @Test
    public void testFilterMotherSide() {
        setSettingsToTrue();
        DataCache.setShowMotherSide(false);
        checkFatherSideEvents(true);
        checkMotherSideEvents(false);
    }

    @Test
    public void testFilterBothSides() {
        setSettingsToTrue();
        DataCache.setShowFatherSide(false);
        DataCache.setShowMotherSide(false);
        checkFatherSideEvents(false);
        checkMotherSideEvents(false);
    }

    @Test
    public void testFilterMaleEvents() {
        setSettingsToTrue();
        DataCache.setShowMaleEvents(false);
        checkEventsByGender("m", false);
        checkEventsByGender("f", true);
    }

    @Test
    public void testFilterFemaleEvents() {
        setSettingsToTrue();
        DataCache.setShowFemaleEvents(false);
        checkEventsByGender("m", true);
        checkEventsByGender("f", false);
    }

    @Test
    public void testFilterAllEventsByGender() {
        setSettingsToTrue();
        DataCache.setShowMaleEvents(false);
        DataCache.setShowFemaleEvents(false);

        for(Event event : DataCache.getEvents().values()) {
            assertFalse(DataCache.isEventShown(event));
        }
    }

    public void testMaleAndMotherSideFilters() {
        setSettingsToTrue();
        DataCache.setShowMaleEvents(false);
        DataCache.setShowMotherSide(false);


    }

    private void setSettingsToTrue() {
        DataCache.setShowFatherSide(true);
        DataCache.setShowMotherSide(true);
        DataCache.setShowMaleEvents(true);
        DataCache.setShowFemaleEvents(true);
    }

    private void checkFatherSideEvents(boolean shouldEventsShow) {
        for(String ID : DataCache.getPaternalAncestors()) {
            for(Event event : DataCache.getPersonEvents(ID)) {
                assertEquals(shouldEventsShow, DataCache.isEventShown(event));
            }
        }
    }

    private void checkMotherSideEvents(boolean shouldEventsShow) {
        for(String ID : DataCache.getMaternalAncestors()) {
            for(Event event : DataCache.getPersonEvents(ID)) {
                assertEquals(shouldEventsShow, DataCache.isEventShown(event));
            }
        }
    }

    private void checkEventsByGender(String gender, boolean shouldEventsShow) {
        for(Person person : DataCache.getPeople().values()) {
            if(person.getGender().equals(gender)) {
                for(Event event : DataCache.getPersonEvents(person)) {
                    assertEquals(shouldEventsShow, DataCache.isEventShown(event));
                }
            }
        }
    }

}

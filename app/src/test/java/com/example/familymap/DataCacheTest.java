package com.example.familymap;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

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
    public void testMultipleChildRelations() {
        //TODO: Finish or delete this test (need my own data)
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

        assertTrue(DataCache.isEventShown(DataCache.getPersonFirstEvent("Sheila_Parker")));
        assertTrue(DataCache.isEventShown(DataCache.getPersonFirstEvent("Davis_Hyer")));
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

        for(String personID : DataCache.getPeople().keySet()) {
            assertFalse(DataCache.isEventShown(DataCache.getPersonFirstEvent(personID)));
        }
    }

    @Test
    public void testMaleAndMotherSideFilters() {
        setSettingsToTrue();
        DataCache.setShowMaleEvents(false);
        DataCache.setShowMotherSide(false);

        assertFalse(DataCache.isEventShown(DataCache.getPersonFirstEvent("Frank_Jones")));
        assertFalse(DataCache.isEventShown(DataCache.getPersonFirstEvent("Mrs_Jones")));
        assertFalse(DataCache.isEventShown(DataCache.getPersonFirstEvent("Betty_White")));
        assertFalse(DataCache.isEventShown(DataCache.getPersonFirstEvent("Ken_Rodham")));
        assertFalse(DataCache.isEventShown(DataCache.getPersonFirstEvent("Blaine_McGary")));
        assertFalse(DataCache.isEventShown(DataCache.getPersonFirstEvent("Davis_Hyer")));

        assertTrue(DataCache.isEventShown(DataCache.getPersonFirstEvent("Mrs_Rodham")));
        assertTrue(DataCache.isEventShown(DataCache.getPersonFirstEvent("Sheila_Parker")));
    }

    @Test
    public void testFemaleAndFatherSideFilters() {
        setSettingsToTrue();
        DataCache.setShowFemaleEvents(false);
        DataCache.setShowFatherSide(false);

        assertFalse(DataCache.isEventShown(DataCache.getPersonFirstEvent("Mrs_Jones")));
        assertFalse(DataCache.isEventShown(DataCache.getPersonFirstEvent("Betty_White")));
        assertFalse(DataCache.isEventShown(DataCache.getPersonFirstEvent("Ken_Rodham")));
        assertFalse(DataCache.isEventShown(DataCache.getPersonFirstEvent("Blaine_McGary")));
        assertFalse(DataCache.isEventShown(DataCache.getPersonFirstEvent("Mrs_Rodham")));
        assertFalse(DataCache.isEventShown(DataCache.getPersonFirstEvent("Sheila_Parker")));

        assertTrue(DataCache.isEventShown(DataCache.getPersonFirstEvent("Davis_Hyer")));
        assertTrue(DataCache.isEventShown(DataCache.getPersonFirstEvent("Frank_Jones")));
    }

    private void setSettingsToTrue() {
        DataCache.setShowFatherSide(true);
        DataCache.setShowMotherSide(true);
        DataCache.setShowMaleEvents(true);
        DataCache.setShowFemaleEvents(true);
    }

    private void checkFatherSideEvents(boolean shouldEventsShow) {
        for(String personID : DataCache.getPaternalAncestors()) {
            assertEquals(shouldEventsShow, DataCache.isEventShown(DataCache.getPersonFirstEvent(personID)));
        }
    }

    private void checkMotherSideEvents(boolean shouldEventsShow) {
        for(String personID : DataCache.getMaternalAncestors()) {
            assertEquals(shouldEventsShow, DataCache.isEventShown(DataCache.getPersonFirstEvent(personID)));
        }
    }

    private void checkEventsByGender(String gender, boolean shouldEventsShow) {
        for(Person person : DataCache.getPeople().values()) {
            if(person.getGender().equals(gender)) {
                Event event = DataCache.getPersonFirstEvent(person.getPersonID());
                assertEquals(shouldEventsShow, DataCache.isEventShown(event));
            }
        }
    }

    @Test
    public void testEventSort() {
        //probably Sheila, except what about the double event?
    }

    @Test
    public void testSingleEventSort() {
        //use Betty White - only death
    }

    @Test
    public void testSameYearEventSort() {
        //would need to use my own data?
    }

    @Test
    public void testSearchQuery() {

    }

    @Test
    public void testNumericalSearchQuery() {

    }

    @Test
    public void testNoMatchSearchQuery() {

    }

    @Test
    public void testNonAlphaNumericSearchQuery() {

    }

}

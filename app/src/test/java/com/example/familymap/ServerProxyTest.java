package com.example.familymap;

import static org.junit.Assert.*;

import org.junit.Test;

import request.LoginRequest;
import request.RegisterRequest;
import result.AllEventsResult;
import result.AllPeopleResult;
import result.LoginRegisterResult;

public class ServerProxyTest {
    private final ServerProxy serverProxy = new ServerProxy("localhost", "8080");

    @Test
    public void testLoginPass() {
        LoginRequest request = new LoginRequest("sheila", "parker");

        LoginRegisterResult result = serverProxy.login(request);

        assertNotNull(result);
        assertNull(result.getMessage());
        assertEquals("sheila", result.getUsername());
        assertNotNull(result.getAuthtoken());
        assertEquals("Sheila_Parker", result.getPersonID());

        assertEquals("Sheila_Parker", DataCache.getUserID());
        assertNotNull(DataCache.getPeople());
        assertEquals(8, DataCache.getPeople().size());
        assertNotNull(DataCache.getEvents());
        assertEquals(16, DataCache.getEvents().size());
    }

    @Test
    public void testLoginFail() {
        LoginRequest request = new LoginRequest();
        request.setUsername("sheila");
        request.setPassword("wrongPassword");

        LoginRegisterResult result = serverProxy.login(request);

        assertNotNull(result);
        assertEquals("Error: Invalid password", result.getMessage());
        assertFalse(result.isSuccess());
    }

    @Test
    public void testLoginEmptyFail() {
        LoginRequest request = new LoginRequest();
        request.setUsername("");
        request.setPassword("");

        LoginRegisterResult result = serverProxy.login(request);

        assertNotNull(result);
        assertEquals("Error: Invalid username", result.getMessage());
        assertFalse(result.isSuccess());
    }

    @Test
    public void testRegisterPass() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("new_username");
        request.setPassword("password");
        request.setFirstName("First");
        request.setLastName("Last");
        request.setEmail("email");
        request.setGender("f");

        LoginRegisterResult result = serverProxy.register(request);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("new_username", result.getUsername());

    }

    @Test
    public void testRegisterFail() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("sheila");
        request.setPassword("parker");
        request.setFirstName("First");
        request.setLastName("Last");
        request.setEmail("email");
        request.setGender("f");

        LoginRegisterResult result = serverProxy.register(request);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Error: This username is not available", result.getMessage());
    }

    @Test
    public void testGetPeoplePass() { //8 ppl 16 events
        LoginRequest request = new LoginRequest("sheila", "parker");
        serverProxy.login(request);

        AllPeopleResult result = serverProxy.getPeople();

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(8, result.getData().length);
    }

    @Test
    public void testGetPeopleFail() {
        ServerProxy serverProxy = new ServerProxy("localhost", "8080");

        AllPeopleResult result = serverProxy.getPeople();

        assertNotNull(result);
        assertEquals("Error: Invalid authtoken", result.getMessage());
        assertFalse(result.isSuccess());
    }

    @Test
    public void testGetEventsPass() {
        LoginRequest request = new LoginRequest("sheila", "parker");
        serverProxy.login(request);

        AllEventsResult result = serverProxy.getEvents();

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(16, result.getData().length);
    }

    @Test
    public void testGetEventsFail() {
        ServerProxy serverProxy = new ServerProxy("localhost", "8080");
        AllEventsResult result = serverProxy.getEvents();

        assertNotNull(result);
        assertEquals("Error: Invalid authtoken", result.getMessage());
        assertFalse(result.isSuccess());
    }
}

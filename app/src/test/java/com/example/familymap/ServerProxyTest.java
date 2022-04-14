package com.example.familymap;

import static org.junit.Assert.*;

import org.junit.Test;

import request.LoginRequest;
import request.RegisterRequest;
import result.AllEventsResult;
import result.AllPeopleResult;
import result.LoginRegisterResult;

public class ServerProxyTest {
    private final DataCache dataCache = DataCache.getInstance();
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

        assertEquals("Sheila_Parker", dataCache.getUserID());
        assertNotNull(dataCache.getPeople());
        assertEquals(8, dataCache.getPeople().size());
        assertNotNull(dataCache.getEvents());
        assertEquals(16, dataCache.getEvents().size());
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
    public void testGetPeoplePass() {
        LoginRequest request = new LoginRequest("sheila", "parker");
        serverProxy.login(request);

        AllPeopleResult result = serverProxy.getPeople();

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(8, result.getData().length);
    }

    @Test
    public void testGetPeopleFail() {
        dataCache.setAuthtoken(null);
        AllPeopleResult result = serverProxy.getPeople();

        assertNotNull(result);
        assertEquals("Error: Invalid authtoken", result.getMessage());
        assertFalse(result.isSuccess());
    }

    @Test
    public void testGetEventsFail() {
        dataCache.setAuthtoken(null);
        AllEventsResult result = serverProxy.getEvents();

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
}

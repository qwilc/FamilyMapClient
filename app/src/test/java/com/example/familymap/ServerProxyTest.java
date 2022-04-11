package com.example.familymap;

import static org.junit.Assert.*;

import org.junit.Test;

import request.LoginRequest;
import request.RegisterRequest;
import result.LoginRegisterResult;

public class ServerProxyTest {
    private final ServerProxy serverProxy = new ServerProxy("localhost", "8080");;

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

    }

    @Test
    public void testRegisterFail() {

    }
}

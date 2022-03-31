package service;

import dao.*;
import result.PersonResult;

import java.sql.Connection;

/**
 * The PersonService class performs the functionality for person requests with a specified ID
 */
public class PersonService extends GetDataService {
    /**
     * Finds and returns data for the person specified by the person ID if that person is associated with the user indicated by the authentication token
     *
     * @param authtoken the authentication token
     * @param personID the person ID
     * @return a PersonResult object with the outcome of the person request
     */
    public PersonResult person(String authtoken, String personID) {
        return (PersonResult) super.getData(authtoken, personID);
    }

    @Override
    protected void initializeResult() {
        result = new PersonResult(null, false, null, null, null, null, null, null, null, null);
    }

    @Override
    protected DAO initializeDAO(Connection conn) {
        return new PersonDAO(conn);
    }
}

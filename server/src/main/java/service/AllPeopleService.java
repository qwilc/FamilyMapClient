package service;

import dao.*;
import model.Model;
import model.Person;
import result.AllPeopleResult;
import result.Result;

import java.sql.Connection;

/**
 * The AllPeopleService class performs the functionality for person requests without a specified ID
 */
public class AllPeopleService extends GetDataService {
    /**
     * Finds and returns data for all the people associated with the username associated with the given authentication token
     *
     * @param authtoken the authentication token
     * @return an AllPeopleResult object with the outcome of the request
     */
    public AllPeopleResult people(String authtoken) {
        return (AllPeopleResult) super.getData(authtoken, null);
    }

    @Override
    protected void initializeResult() {
        result = new AllPeopleResult(null, false, null);
    }

    @Override
    protected DAO initializeDAO(Connection conn) {
        return new PersonDAO(conn);
    }

    @Override
    protected void setResultData(Result result, Model[] array) {
        Person[] people = new Person[array.length];
        for(int i = 0; i < array.length; i++) {
            people[i] = (Person) array[i];
        }

        ((AllPeopleResult) result).setData(people);
    }
}

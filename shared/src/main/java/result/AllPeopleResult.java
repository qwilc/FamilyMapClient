package result;

import model.Person;

/**
 * The AllPeopleResult class stores the data for the result body for person requests without a personID
 */
public class AllPeopleResult extends Result {
    /**
     * an array of all the people associated with a given user
     */
    private Person[] data;

    /**
     * Creates an AllPeopleResult object
     *
     * @param message The message to return in case of an error
     * @param success indicates whether the request was successful
     * @param data the people
     */
    public AllPeopleResult(String message, boolean success, Person[] data) {
        super(message, success);
        this.data = data;
    }

    public Person[] getData() {
        return data;
    }

    public void setData(Person[] data) {
        this.data = data;
    }
}

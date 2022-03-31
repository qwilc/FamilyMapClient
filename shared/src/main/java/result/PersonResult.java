package result;

import model.Model;
import model.Person;

/**
 * The PersonResult class stores the data for the result body for a person request where an ID was specified
 */
public class PersonResult extends Result {
    /**
     * The username of the user associated with the person
     */
    private String associatedUsername;
    /**
     * The person's ID
     */
    private String personID;
    /**
     * The first name
     */
    private String firstName;
    /**
     * The last name
     */
    private String lastName;
    /**
     * The gender
     */
    private String gender;
    /**
     * The father's ID
     */
    private String fatherID;
    /**
     * The mother's ID
     */
    private String motherID;
    /**
     * The spouse's ID
     */
    private String spouseID;

    /**
     * Creates a PersonResult object
     *
     * @param message the message to return in case of an error
     * @param success indicates whether the request was successful
     * @param personID the person's ID
     * @param associatedUsername the associated user's username
     * @param firstName the person's first name
     * @param lastName the person's last name
     * @param gender the person's gender
     * @param fatherID the ID of the person's father
     * @param motherID the ID of the person's mother
     * @param spouseID the ID of the person's spouse
     */
    public PersonResult(String message, boolean success, String personID, String associatedUsername, String firstName, String lastName, String gender,
                        String fatherID, String motherID, String spouseID) {
        super(message, success);
        this.associatedUsername = associatedUsername;
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.fatherID = fatherID;
        this.motherID = motherID;
        this.spouseID = spouseID;
    }

    @Override
    public void setDataFromModel(Model model) {
        Person person = (Person) model;
        personID = person.getPersonID();
        associatedUsername = person.getAssociatedUsername();
        firstName = person.getFirstName();
        lastName = person.getLastName();
        gender = person.getGender();
        fatherID = person.getFatherID();
        motherID = person.getMotherID();
        spouseID = person.getSpouseID();
    }

    public String getAssociatedUsername() {
        return associatedUsername;
    }

    public void setAssociatedUsername(String associatedUsername) {
        this.associatedUsername = associatedUsername;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFatherID() {
        return fatherID;
    }

    public void setFatherID(String fatherID) {
        this.fatherID = fatherID;
    }

    public String getMotherID() {
        return motherID;
    }

    public void setMotherID(String motherID) {
        this.motherID = motherID;
    }

    public String getSpouseID() {
        return spouseID;
    }

    public void setSpouseID(String spouseID) {
        this.spouseID = spouseID;
    }
}

package model;

import java.util.Objects;
import java.util.UUID;

/**
 * The Person class stores the data for a row of the person table in the database.
 * Contains fields for personID, associated username, firstName, lastName, gender, father ID, mother ID, and spouse ID as well as the corresponding getters and setters
 */
public class Person extends Model {
    /**
     * The unique ID
     */
    private String personID;
    /**
     * The username of the associated user
     */
    private String associatedUsername;
    /**
     * The first name
     */
    private String firstName;
    /**
     * The last name
     */
    private String lastName;
    /**
     * The gender, either "f" or "m"
     */
    private String gender;
    /**
     * The ID of the person's father (optional)
     */
    private String fatherID;
    /**
     * The ID of the person's mother (optional)
     */
    private String motherID;
    /**
     * The ID of the person's spouse (optional)
     */
    private String spouseID;

    /**
     * Creates a Person object
     *
     * @param personID the person's unique identifier
     * @param associatedUsername the username of the user associated with the person
     * @param firstName the person's first name
     * @param lastName the person's last name
     * @param gender the person's gender, either "m" or "f"
     * @param fatherID the ID of the person's father (may be null)
     * @param motherID the ID of the person's mother (may be null)
     * @param spouseID the ID of the person's spouse (may be null)
     */
    public Person(String personID, String associatedUsername, String firstName, String lastName,
                  String gender, String fatherID, String motherID, String spouseID) {
        this.personID = personID;
        this.associatedUsername = associatedUsername;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.fatherID = fatherID;
        this.motherID = motherID;
        this.spouseID = spouseID;
    }

    public Person(String associatedUsername, String firstName, String lastName,
                  String gender, String fatherID, String motherID, String spouseID) {
        this.personID = UUID.randomUUID().toString();
        this.associatedUsername = associatedUsername;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.fatherID = fatherID;
        this.motherID = motherID;
        this.spouseID = spouseID;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    @Override
    public String getAssociatedUsername() {
        return associatedUsername;
    }

    public void setAssociatedUsername(String associatedUsername) {
        this.associatedUsername = associatedUsername;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(personID, person.personID)
                && Objects.equals(associatedUsername, person.associatedUsername)
                && Objects.equals(firstName, person.firstName)
                && Objects.equals(lastName, person.lastName)
                && Objects.equals(gender, person.gender)
                && Objects.equals(fatherID, person.fatherID)
                && Objects.equals(motherID, person.motherID)
                && Objects.equals(spouseID, person.spouseID);
    }
}

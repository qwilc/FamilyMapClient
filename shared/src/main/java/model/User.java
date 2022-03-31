package model;

/**
 * The User class stores the data for a row of the user table in the database.
 * Contains fields for username, password, email, firstName, lastName, gender, and personID, as well as the corresponding getters and setters
 */
public class User extends Model {
    /**
     * The unique username
     */
    private String username;

    /**
     * The password
     */
    private String password;

    /**
     * The unique email address
     */
    private String email;

    /**
     * The user's first name
     */
    private String firstName;

    /**
     * The user's last name
     */
    private String lastName;

    /**
     * The user's gender, either "f" or "m"
     */
    private String gender;

    /**
     * The unique ID
     */
    private String personID;


    /**
     * Creates a user
     *
     * @param username The user's unique username
     * @param password The user's password
     * @param email The user's unique email address
     * @param firstName The user's first name
     * @param lastName The user's last name
     * @param gender The user's gender, either "f" or "m"
     * @param personID The user's unique identifier
     */
    public User(String username, String password, String email, String firstName, String lastName,
                String gender, String personID) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.personID = personID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username)
                && password.equals(user.password)
                && email.equals(user.email)
                && firstName.equals(user.firstName)
                && lastName.equals(user.lastName)
                && gender.equals(user.gender)
                && personID.equals(user.personID);
    }
}

package result;

/**
 * The LoginResult class stores the data for the result body for a login request
 */
public class LoginRegisterResult extends Result {
    /**
     * The authentication token generated for this login
     */
    private String authtoken;
    /**
     * The username of the user logging in
     */
    private String username;
    /**
     * The personID of the user logging in
     */
    private String personID;

    /**
     * Creates a LoginResult object
     *
     * @param message the message to return in case of an error
     * @param success indicates whether the request was successful
     * @param authtoken the authentication token
     * @param username the username
     * @param personID the password
     */
    public LoginRegisterResult(String message, boolean success, String authtoken, String username, String personID) {
        super(message, success);
        this.authtoken = authtoken;
        this.username = username;
        this.personID = personID;
    }

    public String getAuthtoken() {
        return authtoken;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }
}

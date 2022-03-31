package model;

import java.util.Objects;

/**
 *
 *
 * The AuthToken class stores the data for a row of the authtoken table in the database.
 * Contains fields for authtoken and year, as well as the corresponding getters and setters
 */
public class AuthToken extends Model {
    /**
     * The authentication token
     */
    private String authtoken;
    /**
     * The username associated with the authentication token
     */
    private String username;

    /**
     * Creates an AuthToken object
     *
     * @param authtoken the authentication token
     * @param username the username associated with the token
     */
    public AuthToken(String authtoken, String username) {
        this.authtoken = authtoken;
        this.username = username;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthToken authToken = (AuthToken) o;
        return Objects.equals(authtoken, authToken.authtoken) && Objects.equals(username, authToken.username);
    }
}

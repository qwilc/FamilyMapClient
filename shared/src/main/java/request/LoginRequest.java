package request;

/**
 * The LoginRequest class stores the data for the request body of a login request
 */
public class LoginRequest {
    /**
     * The username
     */
    private String username;
    /**
     * The password
     */
    private String password;

    public LoginRequest() {}

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
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
}

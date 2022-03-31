package service;

import dao.AuthTokenDAO;
import dao.DataAccessException;
import dao.Database;
import dao.UserDAO;
import model.AuthToken;
import model.User;
import request.LoginRequest;
import result.LoginRegisterResult;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

/**
 * The LoginService class performs the functionality for login requests
 */
public class LoginService {
    /**
     * Logs the user in and provides an authentication token if the username and password are valid
     *
     * @param request a request containing the entered username and password
     * @return a LoginResult object indicating the outcome of the login request
     */
    public LoginRegisterResult login(LoginRequest request) {
        Database database = new Database();
        LoginRegisterResult result = new LoginRegisterResult(null, false, null, null, null);

        try(Connection conn = database.openConnection()) {
            String username = request.getUsername();
            String password = request.getPassword();

            User user = new UserDAO(conn).query(username);

            if(user == null) {
                result.setMessage("Error: Invalid username");
                result.setSuccess(false);
            }
            else if(Objects.equals(user.getPassword(), password)) {
                result.setPersonID(user.getPersonID());

                result.setUsername(username);
                result.setSuccess(true);

                String authToken = UUID.randomUUID().toString();
                new AuthTokenDAO(conn).insert(new AuthToken(authToken, username));
                result.setAuthtoken(authToken);
            }
            else {
                result.setMessage("Error: Invalid password");
                result.setSuccess(false);
            }
            database.closeConnection(true);
        }
        catch (SQLException | DataAccessException ex) {
            result.setMessage("Error: Login was unsuccessful");
            result.setSuccess(false);
            ex.printStackTrace();
        }

        return result;
    }
}

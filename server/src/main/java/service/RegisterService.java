package service;

import dao.*;
import model.AuthToken;
import model.User;
import request.RegisterRequest;
import result.LoginRegisterResult;
import treeGenerator.TreeGenerator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

/**
 * The RegisterService class performs the functionality for register requests
 */
public class RegisterService {
    /**
     * Registers the user and provides an authentication token if the inputted information is valid
     *
     * @param request the request containing the inputted information
     * @return a LoginResult with the outcome of the register request
     */
    public LoginRegisterResult register(RegisterRequest request) {
        Database database = new Database();
        LoginRegisterResult result = new LoginRegisterResult(null, false, null, null, null);

        try(Connection conn = database.openConnection()) {
            String username = request.getUsername();
            String password = request.getPassword();
            String email = request.getEmail();
            String firstName = request.getFirstName();
            String lastName = request.getLastName();
            String gender = request.getGender();

            UserDAO dao = new UserDAO(conn);

            if(hasInvalidField(request)) {
                result.setMessage("Error: Invalid or empty field");
                result.setSuccess(false);
            }
            else if(dao.query(username) != null) {
                result.setMessage("Error: This username is not available");
                result.setSuccess(false);
            }
            else {
                String personID = UUID.randomUUID().toString();
                result.setPersonID(personID);

                User user = new User(username, password, email, firstName, lastName, gender, personID);
                dao.insert(user);

                //TODO: These 6 lines are in both register and login services
                result.setUsername(username);
                result.setSuccess(true);

                String authToken = UUID.randomUUID().toString();
                new AuthTokenDAO(conn).insert(new AuthToken(authToken, username));
                result.setAuthtoken(authToken);

                TreeGenerator generator = new TreeGenerator(conn, user);
                generator.generateTree(4);
            }
            database.closeConnection(true);
        }
        catch (SQLException | DataAccessException ex) {
            result.setMessage("Error: Registration was unsuccessful");
            result.setSuccess(false);
            ex.printStackTrace();
        }
        return result;
    }

    private boolean hasInvalidField(RegisterRequest request) {
        return isEmptyField(request.getUsername())
        || isEmptyField(request.getPassword())
        || isEmptyField(request.getEmail())
        || isEmptyField(request.getFirstName())
        || isEmptyField(request.getLastName())
        || !isValidGender(request.getGender());
    }

    private boolean isEmptyField(String field) {
        return field == null || field.equals("");
    }

    private boolean isValidGender(String gender) {
        return Objects.equals(gender, "m") || Objects.equals(gender, "f");
    }

}

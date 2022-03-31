package service;

import dao.DataAccessException;
import dao.Database;
import dao.UserDAO;
import model.User;
import result.Result;
import treeGenerator.TreeGenerator;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * The FillService class performs the functionality for fill requests
 */
public class FillService {
    /**
     * Generates the specified number of generations for the user indicated by the given username
     *
     * @param username the username
     * @param numGenerations the number of generations
     * @return a Result object indicating the outcome of the request
     */
    public Result fill(String username, int numGenerations) {
        assert numGenerations >= 0;

        Database database = new Database();
        Result result = new Result(null, false);

        try(Connection conn = database.getConnection()) {
            UserDAO dao = new UserDAO(conn);

            if(dao.query(username) == null) {
                result.setMessage("Error: Fill was unsuccessful, user does not exist");
                result.setSuccess(false);
            }
            else {
                User user = dao.query(username);
                TreeGenerator generator = new TreeGenerator(conn, user);
                generator.generateTree(numGenerations);

                int numPeople = generator.getNumPeopleAdded();
                int numEvents = generator.getNumEventsAdded();

                result.setMessage("Successfully added " + numPeople + " persons and " + numEvents + " events to the database.");
                result.setSuccess(true);
            }
            database.closeConnection(true);
        }
        catch (SQLException | DataAccessException ex) {
            result.setMessage("Error: Fill was unsuccessful");
            result.setSuccess(false);
            ex.printStackTrace();
        }

        return result;
    }
}

package service;

import dao.*;
import model.Event;
import model.Person;
import model.User;
import request.LoadRequest;
import result.Result;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The LoadService class performs the functionality for load requests
 */
public class LoadService {
    private final Logger logger = Logger.getLogger("LoadService");
    /**
     * Erases all data in the database and loads the given data
     *
     * @param request a request containing the new data to load
     * @return a Result object indicating the outcome of the request
     */
    public Result load(LoadRequest request) {
        Level level = Level.FINE;
        logger.setLevel(level);
        logger.setUseParentHandlers(false);
        java.util.logging.Handler handler = new ConsoleHandler();
        handler.setLevel(level);
        logger.addHandler(handler);

        Database database = new Database();
        Result result = new Result(null, false);

        new ClearService().clear();

        try(Connection conn = database.openConnection()) {
            User[] users = request.getUsers();
            Person[] people = request.getPersons();
            Event[] events = request.getEvents();

            UserDAO userDAO = new UserDAO(conn);
            for(User user : users) {
                userDAO.insert(user);
                logger.finest(user.getUsername());
            }

            PersonDAO personDAO = new PersonDAO(conn); //TODO: Repeated code; "insertArray" method in DAO?
            for(Person person : people) {
                personDAO.insert(person);
                logger.finest(person.getPersonID());
            }

            EventDAO eventDAO = new EventDAO(conn);
            for(Event event : events) {
                eventDAO.insert(event);
                logger.finest(event.getEventID());
            }

            database.closeConnection(true);

            result.setMessage("Successfully added " + users.length + " users, "
                    + people.length + " persons, and " + events.length + " events to the database.");
            result.setSuccess(true);
        }
        catch (SQLException | DataAccessException ex) {
            result.setMessage("Error: Load request unsuccessful");
            result.setSuccess(false);
            ex.printStackTrace();
        }

        return result;
    }
}

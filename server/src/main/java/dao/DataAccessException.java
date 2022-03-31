package dao;

/**
 * An exception thrown by the DAO classes when they catch a SQLException
 */
public class DataAccessException extends Exception {
    public DataAccessException() {
    }

    public DataAccessException(String message) {
        super(message);
    }
}

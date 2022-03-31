package dao;

import model.Model;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The UserDAO class handles connecting to the database in order to query or modify the user table
 */
public class UserDAO extends DAO {

    /**
     * Creates a UserDAO object
     *
     * @param conn the database connection
     */
    public UserDAO(Connection conn) {
        super(conn);
        tableName = "user";
        primaryKey = "username";
    }

    /**
     * Inserts the specified user into the table
     *
     * @param user the user to insert
     * @throws DataAccessException if an SQLException is caught
     */
    public void insert(Model user) throws DataAccessException {
        String sql = "insert into user (username, password, email, firstName, lastName, gender, personID) " +
                "values (?, ?, ?, ?, ?, ?, ?)";

        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ((User) user).getUsername());
            stmt.setString(2, ((User) user).getPassword());
            stmt.setString(3, ((User) user).getEmail());
            stmt.setString(4, ((User) user).getFirstName());
            stmt.setString(5, ((User) user).getLastName());
            stmt.setString(6, ((User) user).getGender());
            stmt.setString(7, ((User) user).getPersonID());

            stmt.executeUpdate();
        }
        catch(SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException();
        }
    }

    /**
     * Returns the user with the specified username
     *
     * @param username the username
     * @return a User object representing the user
     * @throws DataAccessException if an SQLException is caught
     */
    @Override
    public User query(String username) throws DataAccessException {
        return (User) super.query(username);
    }

    @Override
    protected Model getModelFromResultSet(ResultSet rs) throws SQLException {
        return new User(rs.getString("username"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("gender"),
                rs.getString("personID") );
    }

    /**
     * Clears all rows of the user table in the database
     * @throws DataAccessException if an SQLException is caught
     */
    @Override
    public void clear() throws DataAccessException {
        super.clear("user");
    }
}

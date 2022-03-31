package dao;

import model.AuthToken;
import model.Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The AuthTokenDAO class handles connecting to the database in order to query or modify the authtoken table
 */
public class AuthTokenDAO extends DAO {
    public AuthTokenDAO(Connection conn) {
        super(conn);
        tableName = "authtoken";
        primaryKey = "authToken";
    }

    /**
     * Inserts the specified authtoken into the table
     *
     * @param authToken the authtoken to insert
     * @throws DataAccessException if an SQLException is caught
     */
    @Override
    public void insert(Model authToken) throws DataAccessException {
        String sql = "insert into authtoken(authtoken, username) values(?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ((AuthToken) authToken).getAuthtoken());
            stmt.setString(2, ((AuthToken) authToken).getUsername());

            stmt.executeUpdate();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException();
        }
    }

    /**
     * Returns the AuthToken object associated with the specified authentication token
     *
     * @param authToken the authentication token
     * @return an AuthToken object representing the authtoken
     * @throws DataAccessException if an SQLException is caught
     */
    @Override
    public AuthToken query(String authToken) throws DataAccessException {
        return (AuthToken) super.query(authToken);
    }

    @Override
    protected Model getModelFromResultSet(ResultSet rs) throws SQLException {
        return new AuthToken(rs.getString("authtoken"),
                rs.getString("username") );
    }

    /**
     * Clears all rows of the authtoken table in the database
     * @throws DataAccessException if an SQLException is caught
     */
    @Override
    public void clear() throws DataAccessException {
        super.clear("authtoken");
    }
}

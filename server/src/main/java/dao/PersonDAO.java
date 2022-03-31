package dao;

import model.Model;
import model.Person;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The PersonDAO class handles connecting to the database in order to query or modify the person table
 */
public class PersonDAO extends DAO{

    /**
     * Creates a PersonDAO object
     *
     * @param conn the database connection
     */
    public PersonDAO(Connection conn) {
        super(conn);
        tableName = "person";
        primaryKey = "personID";
    }

    /**
     * Inserts the specified person into the table
     *
     * @param person the person to insert
     * @throws DataAccessException if an SQLException is caught
     */
    @Override
    public void insert(Model person) throws DataAccessException {
        String sql = "insert into person(personID, associatedUsername, firstName, lastName, gender, " +
                "fatherID, motherID, spouseID) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?)";

        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ((Person) person).getPersonID());
            stmt.setString(2, ((Person) person).getAssociatedUsername());
            stmt.setString(3, ((Person) person).getFirstName());
            stmt.setString(4, ((Person) person).getLastName());
            stmt.setString(5, ((Person) person).getGender());
            stmt.setString(6, ((Person) person).getFatherID());
            stmt.setString(7, ((Person) person).getMotherID());
            stmt.setString(8, ((Person) person).getSpouseID());

            stmt.executeUpdate();
        }
        catch(SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException();
        }
    }

    /**
     * Returns the person with the specified ID
     *
     * @param personID the ID
     * @return a Person object representing the person
     * @throws DataAccessException if an SQLException is caught
     */
    public Person query(String personID) throws DataAccessException {
        return (Person) super.query(personID);
    }

    protected Person getModelFromResultSet(ResultSet rs) throws SQLException {
        return new Person(
                rs.getString("personID"),
                rs.getString("associatedUsername"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("gender"),
                rs.getString("fatherID"),
                rs.getString("motherID"),
                rs.getString("spouseID") );
    }

    /**
     * Clears all rows of the person table in the database
     * @throws DataAccessException if an SQLException is caught
     */
    @Override
    public void clear() throws DataAccessException {
        super.clear("person");
    }
}

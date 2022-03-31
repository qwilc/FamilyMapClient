package dao;

import model.Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public abstract class DAO {
    protected final Connection conn;
    protected String tableName;
    protected String primaryKey;

    public DAO(Connection conn) {
        this.conn = conn;
    }

    public abstract void insert(Model model) throws DataAccessException;

    public Model query(String identifier) throws DataAccessException {
        Model modelObject;
        ResultSet rs;

        String sql = "select * from " + tableName + " where " + primaryKey + " = ?;";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, identifier);
            rs = stmt.executeQuery();
            if (rs.next()) {
                modelObject = getModelFromResultSet(rs);
                return modelObject;
            }
            else {
                return null;
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException();
        }
    }

    public Model[] queryByUser(String username) throws DataAccessException {
        assert(this.getClass() == PersonDAO.class || this.getClass() == EventDAO.class);

        Model model;
        ArrayList<Model> data = new ArrayList<>();
        ResultSet rs;

        String sql = "select * from " + tableName + " where associatedUsername = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            int i = 0;
            while(rs.next()) {
                model = getModelFromResultSet(rs);
                data.add(model);
                i++;
            }

            if(i == 0) {
                return null;
            }
            else {
                return data.toArray(new Model[0]);
            }
        }
        catch(SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException();
        }
    }

    protected abstract Model getModelFromResultSet(ResultSet rs) throws SQLException;

    public void clear(String table) throws DataAccessException {
        String sql = "delete from " + table;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException();
        }
    }

    public abstract void clear() throws DataAccessException;
}

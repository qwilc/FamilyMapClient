package service;

import dao.*;
import model.AuthToken;
import model.Model;
import result.Result;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class GetDataService {
    Result result;

    public Result getData(String authtoken, String ID) {
        Database database = new Database();
        initializeResult();

        try(Connection conn = database.getConnection()) {
            AuthTokenDAO authTokenDAO = new AuthTokenDAO(conn);

            AuthToken token = authTokenDAO.query(authtoken);

            if(token != null) {
                String username = token.getUsername();

                if(ID == null) {
                    Model[] data = getDataArray(conn, username);
                    setResultData(result, data);
                    result.setSuccess(true);
                }
                else {
                    Model model = getSpecificData(conn, ID);

                    if(model == null) {
                        result.setMessage("Error: Invalid ID");
                        result.setSuccess(false);
                    }
                    else if(model.getAssociatedUsername().equals(username)) {
                        result.setDataFromModel(model);
                        result.setSuccess(true);
                    }
                    else {
                        result.setMessage("Error: This user is not associated with the requested data");
                        result.setSuccess(false);
                    }
                }
                database.closeConnection(true);
            }
            else {
                result.setMessage("Error: Invalid authtoken");
                result.setSuccess(false);
            }
        } catch (SQLException | DataAccessException ex) {
            result.setMessage("Error: Could not get the requested data");
            result.setSuccess(false);
            ex.printStackTrace();
        }

        return result;
    }

    protected abstract void initializeResult();

    protected Model[] getDataArray(Connection conn, String username) throws DataAccessException {
        DAO dao = initializeDAO(conn);
        return dao.queryByUser(username);
    }

    protected Model getSpecificData(Connection conn, String ID) throws DataAccessException {
        DAO dao = initializeDAO(conn);
        return dao.query(ID);
    }

    protected abstract DAO initializeDAO(Connection conn);

    protected void setResultData(Result result, Model[] array) {}
}

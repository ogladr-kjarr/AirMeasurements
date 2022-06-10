package model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostgreSQLAccess implements MeasurementAccess {

    private String host, port, database, user, password, connectionURL;
    private static final Logger logger = LogManager.getLogger();

    public PostgreSQLAccess(String host, String port, String database, String user, String password){
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;

        this.connectionURL = "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.database + "?user=" +
                this.user + "&password=" + this.password;

        logger.atDebug().log("Created PostgreSQL object");
    }

    @Override
    public boolean saveMeasurements(List<Measurement> measurements){

        String preparedSQLStatement = """
                INSERT INTO measurements (MDATE, LOCATION, PARAMETER, INTERVAL, UNIT, VALUE, STATUS)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        logger.atDebug().log("Creating connection and prepared statement for insert into PostgreSQL. Consists of " + measurements.size() + " measurements");
        int[] insertStatementResult = new int[1];
        insertStatementResult[0] = 0;

        try(Connection conn = DriverManager.getConnection(connectionURL);
            PreparedStatement measurementStatement = conn.prepareStatement(preparedSQLStatement)){

            logger.atDebug().log("Adding measurements to batch query");
            for(Measurement measurement: measurements){
                measurementStatement.setObject(1, measurement.date());
                measurementStatement.setString(2, measurement.location());
                measurementStatement.setString(3, measurement.parameter());
                measurementStatement.setString(4, measurement.interval());
                measurementStatement.setString(5, measurement.unit());
                measurementStatement.setDouble(6, measurement.value().get());
                measurementStatement.setString(7, measurement.status());
                measurementStatement.addBatch();
            }
            logger.atDebug().log("Executing the batch insert statement");
            insertStatementResult = measurementStatement.executeBatch();
            logger.atDebug().log("Finished executing batch insert statement");


        }catch(java.sql.BatchUpdateException e){
            logger.atError().log("SQL Exception while submitting batch query");
        }catch(java.sql.SQLException e){
            logger.atError().log("SQL Exception while attempting to create connection to PostgreSQL for saveMeasurements");
            logger.atError().log(e);
        }

        return checkInsertResultSet(insertStatementResult);
    }

    /*
    The return object from the batch prepared statement is an integer array, each index corresponding to the query
    in that position in the statement. If the statement was successful then its index holds a 1 value. Here we check
    that the number of entries matches the number of successes.
     */
    private boolean checkInsertResultSet(int[] results){
        int resultTally = 0;
        for(int i = 0; i < results.length; i++){
            resultTally += results[i];
        }
        if(resultTally == results.length){
            logger.atDebug().log("All measurements added to DB successfully");
            return true;
        }else{
            logger.atError().log("Not all measurements saved. " + results.length + " measurements, only " + resultTally + " saved to DB");
            return false;
        }
    }
    @Override
    public ArrayList<Measurement> retrieveMeasurements() {
        logger.atDebug().log("Submitting query to retrieval method");
        return queryDatabase("SELECT * FROM measurements ORDER BY mdate ASC");
    }

    @Override
    public ArrayList<Measurement> retrieveMeasurementSubset(int startYear, int endYear) {
        logger.atDebug().log("Submitting query to retrieval method");
        return queryDatabase("SELECT * FROM measurements WHERE mdate >= '%s/01/01T00:00' AND mdate <= '%s/12/31T23:59' ORDER BY mdate ASC".formatted(startYear, endYear));
    }

    private ArrayList<Measurement> queryDatabase(String queryString) {
        ArrayList<Measurement> queryResult = new ArrayList<>();
        logger.atDebug().log("Opening connection, statement, and result set");
        try (Connection conn = DriverManager.getConnection(connectionURL);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(queryString)) {
            logger.atDebug().log("Iterating over result set");
                while(rs.next()){
                    Measurement measurement = new Measurement(
                            rs.getObject("mdate", OffsetDateTime.class),
                            rs.getString("location"),
                            rs.getString("parameter"),
                            rs.getString("interval"),
                            rs.getString("unit"),
                            Optional.of(rs.getDouble("value")),
                            rs.getString("status")
                    );

                    queryResult.add(measurement);

                }

        } catch (java.sql.SQLException e) {
            logger.atError().log("Query database attempt failed for query: " + queryString);
            logger.atError().log(e);
            //Clear to avoid semi-filled result set
            queryResult.clear();
        }

        return queryResult;
    }
}
package dbUtils;

import constantsUtils.CommonConstants;
import Enumerations.MessageLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.object.GenericStoredProcedure;
import org.springframework.jdbc.object.StoredProcedure;
import tableUtils.PrettyPrintTable;

import java.sql.*;
import java.util.*;
import static propertyUtils.PropertyUtils.getGlobalPropertyEntity;
import static ReportUtils.Report.reportAndLog;

/**
 * this class will handle Oracle DB Queries, it implements DBConnectionManager methods.
 * @author zvika.sela
 * @since 15.06.2021
 *
 */
public class OracleDatabaseUtil extends DbBase implements DBConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(OracleDatabaseUtil.class);
    public int queryTimeout = getGlobalPropertyEntity().getIntProperty("query_timeout");

    /**
     * This method sets the query timeout
     * @author zvika.sela
     * @since 28.06.2021
     * @param queryTimeout the amount of timeout in sec
     *
     */
    public void setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }


    /**
     * This method returns an Oracle DB connection
     *
     * @author zvika.sela
     * @since 28.06.2021
     * @param dbConnString the server address
     * @param dbUser the user for the connection
     * @param dbPass the user's pass
     * @return a DB Connection
     */
    @Override
    public Connection connect(String dbConnString, String dbUser, String dbPass) throws ClassNotFoundException, SQLException {
        reportAndLog("Connecting to:" + dbConnString + " with user: " + dbUser, MessageLevel.INFO);
        return DriverManager.getConnection(dbConnString, dbUser, dbPass);
    }



    /**
     * This method returns a single value from the query, please note that the query will return the upper-left
     * value even if more than 1 value exists in table
     *
     * @param query            the given query we wish to execute of type 'select user_name'
     * @param dbConnString the server address
     * @param dbUser       valid user name for the server login
     * @param dbPass   valid password for the server login
     * @return a String representing the username
     */
    public String getSingleValueFromFirstRowAndColumn(String query, String dbConnString,String dbUser, String dbPass){
        Connection connection=null;
        Statement statement =null;
        ResultSet resultSet =null;
        try {
            reportAndLog("Connecting to:" + dbConnString + " with user: " + dbUser, MessageLevel.INFO);
            reportAndLog("Executing Query: " + query , MessageLevel.INFO);

            connection = connect(dbConnString,dbUser,dbPass);
            statement = connection.createStatement();
            statement.setQueryTimeout(queryTimeout);
            resultSet = statement.executeQuery(query);
                if (!resultSet.next()) {
                    reportAndLog(CommonConstants.FailureClassificationErrors.MISSING_DATA,MessageLevel.INFO);
                }
                    else{
                        String value = resultSet.getString(1);
                        reportAndLog("Value from query: " + value, MessageLevel.INFO);
                        return value;
                    }


        } catch (SQLException | ClassNotFoundException sqlException) {
            reportAndLog("Query failed with error " + sqlException.getMessage(),MessageLevel.ERROR);
            sqlException.printStackTrace();
            throw new Error(sqlException);
        }
        finally {
            disconnect(connection,resultSet,statement);
        }
        return null;
    }



    /**
     * This method receives a Query then loops through the query results and stores them into a list
     * This is best for a single column count queries
     * Please note that each data in the list is already trimmed
     *
     * @param query            the given query we wish to execute
     * @param dbConnString the server address
     * @param dbUser       valid user name for the server login
     * @param dbPass   valid password for the server login
     * @return a List of database row
     */
    public List<String> getResultsFromQuery(String query, String dbConnString, String dbUser, String dbPass) {
        List<String> resultsList = new ArrayList<>();
        Connection connection=null;
        Statement statement =null;
        ResultSet resultSet =null;
        try {

            reportAndLog("Connecting to:" + dbConnString + " with user: " +dbUser, MessageLevel.INFO);
            reportAndLog("<b>Executing Query: </b><br>" + query , MessageLevel.INFO);

            connection = connect(dbConnString,dbUser,dbPass);
            statement = connection.createStatement();
                statement.setQueryTimeout(queryTimeout);
                resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    String value = resultSet.getString(1);
                    if(value == null)
                        resultsList.add(value);
                    else
                        resultsList.add(value.trim());
                }
                if(resultsList.isEmpty())
                    reportAndLog(CommonConstants.FailureClassificationErrors.MISSING_DATA,MessageLevel.INFO);
                else
                    reportAndLog("Query results: <br>" + Arrays.toString(resultsList.toArray()),MessageLevel.INFO);

        } catch (SQLException | ClassNotFoundException sqlException) {
            reportAndLog("Query failed with error " + sqlException.getMessage(),MessageLevel.ERROR);
            sqlException.printStackTrace();
            throw new Error(sqlException);
        }
        finally {
            disconnect(connection,resultSet,statement);
        }
        return resultsList;
    }


    /**
     * This method receives a Query then loops through the query results and stores them into a list
     * This is best for a multi-column count queries
     *
     * @param query            the given query we wish to execute
     * @param dbConnString the server address
     * @param dbUser       valid user name for the server login
     * @param dbPass   valid password for the server login
     * @return a List that holds a list of each database row
     */
    public List<List<String>> getQueryResultTable(String query, String dbConnString,String dbUser, String dbPass) {
        List<List<String>> queryResultsList = new ArrayList<>();
        Connection connection=null;
        Statement statement =null;
        ResultSet resultSet =null;
        try {

            reportAndLog("Connecting to:" + dbConnString + " with user: " + dbUser, MessageLevel.INFO);
            reportAndLog("Executing Query: " + query , MessageLevel.INFO);


            connection = connect(dbConnString,dbUser,dbPass);
            statement = connection.createStatement();
            statement.setQueryTimeout(queryTimeout);
            resultSet = statement.executeQuery(query);
            int queryColumnCount = resultSet.getMetaData().getColumnCount();

                while (resultSet.next()) {
                    List<String> singleRowResults = new ArrayList<>();
                    for (int i = 1; i <= queryColumnCount; i++) {
                        singleRowResults.add(resultSet.getString(i));
                    }
                    queryResultsList.add(singleRowResults);
                }
                if(queryResultsList.isEmpty())
                    reportAndLog(CommonConstants.FailureClassificationErrors.MISSING_DATA,MessageLevel.INFO);
                else
                    PrettyPrintTable.print(queryResultsList);
        } catch (SQLException | ClassNotFoundException sqlException) {
            reportAndLog("Query failed with error " + sqlException.getMessage(),MessageLevel.ERROR);
            sqlException.printStackTrace();
            throw new Error(sqlException);
        }
        finally {
            disconnect(connection,resultSet,statement);
        }
        return queryResultsList;
    }



    /**
     * This method receives a Query then loops through the query results and stores them into a list
     * This is best for a single-row query results
     *
     * @author zvika.sela
     * @since 18.05.2021
     * @param query            the given query we wish to execute
     * @param dbConnString the server address
     * @param dbUser       valid user name for the server login
     * @param dbPass   valid password for the server login
     * @return a List that holds the resulting row, if more than 1 row is fetched it will only return the first one.
     */
    public List<String> getSingleRowResult(String query, String dbConnString,String dbUser, String dbPass)  {
        List<String> singleRowResults = new ArrayList<>();
        Connection connection=null;
        Statement statement =null;
        ResultSet resultSet =null;

        try {

            reportAndLog("Connecting to:" + dbConnString + " with user: " +dbUser, MessageLevel.INFO);
            reportAndLog("Executing Query: " + query , MessageLevel.INFO);

            connection = connect(dbConnString,dbUser,dbPass);
            statement = connection.createStatement();
            statement.setQueryTimeout(queryTimeout);
            resultSet = statement.executeQuery(query);
            int queryColumnCount = resultSet.getMetaData().getColumnCount();


                int count = 0;
                if (resultSet.next()) {
                    singleRowResults = new ArrayList<>();
                    for (int i = 1; i <= queryColumnCount; i++) {
                        singleRowResults.add(resultSet.getString(i));
                    }

                }
            if(singleRowResults.isEmpty())
                reportAndLog(CommonConstants.FailureClassificationErrors.MISSING_DATA,MessageLevel.INFO);
            else
                reportAndLog("Query results: <br>" + Arrays.toString(singleRowResults.toArray()),MessageLevel.INFO);
        } catch (SQLException | ClassNotFoundException sqlException) {
            reportAndLog("Query failed with error " + sqlException.getMessage(),MessageLevel.ERROR);
            sqlException.printStackTrace();
            throw new Error(sqlException);
        }
        finally {
            disconnect(connection,resultSet,statement);
        }
        return singleRowResults;
    }

    /**
     * Disconnect from database (close statement, resultsset and connection)
     * @param connection Instance of Connection to close
     * @param resultSet Instance of ResultSet to close
     * @param statement Instance of Statement to close
     */
    @Override
    public void disconnect(Connection connection, ResultSet resultSet, Statement statement){
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) { logger.error("Failed to close result set"); logger.error(Arrays.toString(e.getStackTrace()));}
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) { logger.error("Failed to close statement"); logger.error(Arrays.toString(e.getStackTrace()));}
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) { logger.error("Failed to close connection"); logger.error(Arrays.toString(e.getStackTrace()));}
        }
    }

    /**
     * This method returns the count of the query results, please note that the query type needs to be 'Count'
     *
     * @param query            the given query we wish to execute of the type 'Count'
     * @param iqShipConnection the server address
     * @param iqShipUser       valid user name for the server login
     * @param iqShipPassword   valid password for the server login
     * @return an integer representing the results count
     */
    public int getQueryCountTypeResults(String query, String iqShipConnection, String iqShipUser, String iqShipPassword) {
        List<String> resultsFromQuery = getResultsFromQuery(query, iqShipConnection, iqShipUser, iqShipPassword);
        logger.info("Query count results " + resultsFromQuery);
        return Integer.parseInt(resultsFromQuery.get(0));
    }

    /**
     * Execute an SQL stored procedure with 1 int parameter
     * @param statement Sql statement to execute
     * @param intParameter Parameter of type int to be integrated into statement
     * @param iqShipConnection DB connection string
     * @param iqShipUser DB user
     * @param iqShipPassword DB password
     * @author plot.ofek
     * @since 22.05.2021
     */
    public void callableStatement(String statement, int intParameter, String iqShipConnection, String iqShipUser, String iqShipPassword)
    {

        Connection connection=null;
        CallableStatement stmt =null;
        ResultSet resultSet =null;

        try{
            connection = connect(iqShipConnection, iqShipUser, iqShipPassword);

            stmt = connection
                    .prepareCall(statement);

            stmt.setInt(1, intParameter);
            stmt.setQueryTimeout(queryTimeout);
            stmt.execute();

            logger.info("Callable statement executed");

            stmt.close();
        } catch (SQLException | ClassNotFoundException sqlException) {
            reportAndLog("Query failed with error " + sqlException.getMessage(),MessageLevel.ERROR);
            sqlException.printStackTrace();
            throw new Error(sqlException);
        }
        finally {
            disconnect(connection,resultSet,stmt);
        }
    }

    /**
     * Execute an SQL stored procedure with 1 String parameter
     * @param statement Sql statement to execute
     * @param stringParameter Parameter of type String to be integrated into statement
     * @param iqShipConnection DB connection string
     * @param iqShipUser DB user
     * @param iqShipPassword DB password
     * @author plot.ofek
     * @since 26.07.2021
     */
    public void callableStatement(String statement, String stringParameter, String iqShipConnection, String iqShipUser, String iqShipPassword)
    {

        Connection connection=null;
        CallableStatement stmt =null;
        ResultSet resultSet =null;

        try{
            connection = connect(iqShipConnection, iqShipUser, iqShipPassword);

            stmt = connection
                    .prepareCall(statement);

            stmt.setString(1, stringParameter);
            stmt.setQueryTimeout(queryTimeout);
            stmt.execute();

            logger.info("Callable statement executed");

            stmt.close();
        } catch (SQLException | ClassNotFoundException sqlException) {
            reportAndLog("Query failed with error " + sqlException.getMessage(),MessageLevel.ERROR);
            sqlException.printStackTrace();
            throw new Error(sqlException);
        }
        finally {
            disconnect(connection,resultSet,stmt);
        }
    }


    /**
     * Execute an SQL stored procedure with any number of parameters, the method doesn't support output params
     * @param statement Sql statement to execute
     * @param dbConnString DB connection string
     * @param dbUser DB user
     * @param dbPass DB password
     * @param params Array of all parameters
     * @author tzvika.sela
     * @since 02.08.2021
     */
    public <K, V, T extends Map<K, V>> T runGenericStoredProcedureWithoutOutput(String statement, String dbConnString, String dbUser, String dbPass, Object... params)
    {
        StoredProcedure storedProcedure;

        DriverManagerDataSource dataSource = new DriverManagerDataSource(dbConnString, dbUser, dbPass);

        storedProcedure = new GenericStoredProcedure();
        storedProcedure.setDataSource(dataSource);
        storedProcedure.setSql(statement);
        storedProcedure.setFunction(false);
        int i = 1;

        for (Object param:params){
            if (param instanceof Integer)
                storedProcedure.declareParameter(new SqlParameter("param"+i, Types.INTEGER));
            if (param instanceof String)
                storedProcedure.declareParameter(new SqlParameter("param" +i, Types.CHAR));
            i++;
        }

        try{
            storedProcedure.setQueryTimeout(queryTimeout);

            Map<String, Object> results =  storedProcedure.execute(params);

            logger.info("Stored Procedure Executed");

            return (T)results;

        } catch (Exception e) {
            reportAndLog("Query failed with error " + e.getMessage(),MessageLevel.ERROR);
            throw new Error("Stored Procedure Failed");
        }
    }

    /**
     * Return a Hash map containing the query result as a list of HashMaps of String, Object
     * @param query query to the DB
     * @param dbConnString DB connection string
     * @param dbUser DB user
     * @param dbPass DB password
     * @return a Hash map containing the query result. i.e column names and their values
     *
     * @author Dafna Genosar
     * @since 09.11.2021
     */
    public <K, V, T extends Map<K, V>, L extends List<T>> L getQueryResultMap(String query, String dbConnString,String dbUser, String dbPass) {

        LinkedList<LinkedHashMap<String, Object>> queryResultsList = new LinkedList<>();
        Connection connection=null;
        Statement statement =null;
        ResultSet resultSet =null;
        try {

            reportAndLog("Connecting to:" + dbConnString + " with user: " + dbUser, MessageLevel.INFO);
            reportAndLog("Executing Query: " + query , MessageLevel.INFO);

            connection = connect(dbConnString,dbUser,dbPass);
            statement = connection.createStatement();
            statement.setQueryTimeout(queryTimeout);

            resultSet = super.executeQuery(statement, query);
            ResultSetMetaData resultMetaData = resultSet.getMetaData();

            int queryColumnCount = resultMetaData.getColumnCount();

            while (resultSet.next()) {

                LinkedHashMap<String, Object> rowResults = new LinkedHashMap<>();

                for (int i = 1; i <= queryColumnCount; i++) {
                    String columnName = resultMetaData.getColumnName(i);

                    //If key already exists, create a new key with appendix "1".
                    while (rowResults.containsKey(columnName))
                    {
                        columnName +="1";
                    }
                    rowResults.put(columnName, resultSet.getObject(i));
                }
                queryResultsList.add(rowResults);
            }
            if(queryResultsList.isEmpty())
                reportAndLog(CommonConstants.FailureClassificationErrors.MISSING_DATA,MessageLevel.INFO);

        } catch (SQLException | ClassNotFoundException sqlException) {
            reportAndLog("Query failed with error " + sqlException.getMessage(),MessageLevel.ERROR);
            sqlException.printStackTrace();
            throw new Error(sqlException);
        }
        finally {
            disconnect(connection,resultSet,statement);
        }
        return (L)queryResultsList;
    }

    /**
     * Return a Hash map containing the query result as a list of HashMaps of String, string
     * @param query query to the DB
     * @param dbConnString DB connection string
     * @param dbUser DB user
     * @param dbPass DB password
     * @return a Hash map containing the query result. i.e column names and their values
     *
     * @author Dafna Genosar
     * @since 26.12.2021
     * @author genosar.dafna
     * @since 06.10.2022
     */
    public <K, V, T extends Map<K, V>, L extends List<T>> L getQueryStringResultMap(String query, String dbConnString, String dbUser, String dbPass) {

        LinkedList<LinkedHashMap<String, String>> queryResultsList;
        Connection connection=null;
        Statement statement =null;
        ResultSet resultSet =null;
        try {
            connection = connect(dbConnString,dbUser,dbPass);
            statement = connection.createStatement();
            statement.setQueryTimeout(queryTimeout);

            reportAndLog("Executing Query: " + query , MessageLevel.INFO);

            resultSet = super.executeQuery(statement, query);

            queryResultsList = super.getQueryStringResultMap(resultSet);

            if(queryResultsList.isEmpty())
                reportAndLog(CommonConstants.FailureClassificationErrors.MISSING_DATA,MessageLevel.INFO);

        }
        catch (SQLException | ClassNotFoundException sqlException) {
            reportAndLog("Query failed with error " + sqlException.getMessage(),MessageLevel.ERROR);
            throw new Error(String.format("Failed to retrieve Query String Result Map.<br>Error: %s<br><br>", sqlException.getMessage()));
        }
        finally {
            disconnect(connection,resultSet,statement);
        }
        return (L)queryResultsList;
    }

    /**
     * Return a Hash map containing the query result as a list of HashMaps of String, string
     * @param query query to the DB
     * @param dbConnString DB connection string
     * @param dbUser DB user
     * @param dbPass DB password
     * @param reportQueryToLog true id to write the query string to the report
     * @return a Hash map containing the query result. i.e column names and their values
     * @author Dafna Genosar
     * @since 06.08.2023
     */
    public <K, V, T extends Map<K, V>, L extends List<T>> L getQueryStringResultMap(String query, String dbConnString, String dbUser, String dbPass, boolean reportQueryToLog) {

        LinkedList<LinkedHashMap<String, String>> queryResultsList;
        Connection connection=null;
        Statement statement =null;
        ResultSet resultSet =null;
        try {
            connection = connect(dbConnString,dbUser,dbPass);
            statement = connection.createStatement();
            statement.setQueryTimeout(queryTimeout);

            if(reportQueryToLog)
                reportAndLog("Executing Query: " + query , MessageLevel.INFO);
            else
                logger.info("Executing Query: " + query);

            resultSet = super.executeQuery(statement, query);

            queryResultsList = super.getQueryStringResultMap(resultSet);

            if(queryResultsList.isEmpty())
                reportAndLog(CommonConstants.FailureClassificationErrors.MISSING_DATA,MessageLevel.INFO);

        }
        catch (SQLException | ClassNotFoundException sqlException) {
            reportAndLog("Query failed with error " + sqlException.getMessage(),MessageLevel.ERROR);
            throw new Error(String.format("Failed to retrieve Query String Result Map.<br>Error: %s<br><br>", sqlException.getMessage()));
        }
        finally {
            disconnect(connection,resultSet,statement);
        }
        return (L)queryResultsList;
    }
}

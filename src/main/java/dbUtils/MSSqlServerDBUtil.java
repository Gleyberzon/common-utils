package dbUtils;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import constantsUtils.CommonConstants;
import Enumerations.MessageLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.object.GenericStoredProcedure;
import org.springframework.jdbc.object.StoredProcedure;
import tableUtils.PrettyPrintTable;
import javax.annotation.Nullable;
import java.sql.*;
import java.util.*;

import static propertyUtils.PropertyUtils.getGlobalPropertyEntity;
import static ReportUtils.Report.reportAndLog;

/**
 * this class will handle MSSQL DB Queries, it implements DBConnectionManager methods.
 * @author zvika.sela
 * @since 15.06.2021
 *
 */
public class MSSqlServerDBUtil extends DbBase implements DBConnectionManager {
    private final static Logger logger = LoggerFactory.getLogger(MSSqlServerDBUtil.class);
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
     * This method frees up the connection,resultset and statement
     *
     * @author zvika.sela
     * @since 15.06.2021
     * @param connection the connection to the server
     * @param resultSet the resultSet Object
     * @param statement the statement Object
     *
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
     * This method returns a Microsoft SQL connection
     *
     * @author zvika.sela
     * @since 15.06.2021
     * @param dbConnString the server address+user+pass
     * @return a DB Connection
     */
    @Override
    public Connection connect(String dbConnString, String dbUser, String dbPass) throws ClassNotFoundException, SQLException {

        reportAndLog("Connecting to:" + dbConnString + " with user: " + dbUser, MessageLevel.INFO);

        //Loading the required JDBC Driver class
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        // Declare the JDBC objects.
        Connection con;
        // Establish the connection.
        con = DriverManager.getConnection(dbConnString,dbUser,dbPass);

        return con;
    }

    /** Set connection to the Azure DB
     *
     * @param serverName - server name
     * @param dbName - DB name
     * @param dbUser - user
     * @param dbPassword - password
     * @return connection object
     * @throws SQLServerException SQLServerException
     *
     * @author umflat.lior
     * @since 31.5.2023
     */
    public Connection connect(String serverName,String dbName,String dbUser,String dbPassword) throws SQLServerException {
        // Establish the connection.
        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setServerName(serverName); // Replace with your server name
        ds.setDatabaseName(dbName); // Replace with your database name
        ds.setAuthentication("ActiveDirectoryPassword");
        ds.setUser(dbUser);
        ds.setPassword(dbPassword);
        ds.setHostNameInCertificate("*.database.windows.net");
        ds.setTrustServerCertificate(true);
        ds.setLoginTimeout(30);

        Connection connection = ds.getConnection();
        System.out.println("Connected to Azure SQL database.");

        return  connection;
    }


    /**
     * This method returns a single value from the query, please note that the query will return the upper-left
     * value even if more than 1 value exists in table
     * @author zvika.sela
     * @since 15.06.2021
     * @param query            the given query we wish to execute
     * @param dbConnString the server address+user+pass
     * @return a String representing the value returned from DB
     */
    @Override
    public String getSingleValueFromFirstRowAndColumn(String query, String dbConnString, String dbUser, String dbPass) {


        ResultSet resultSet = null;
        Statement statement = null;
        Connection connection = null;
        String singleValue = null;

        reportAndLog("Connecting to:" + dbConnString + "with user " + dbUser,MessageLevel.INFO);
        reportAndLog("Executing Query: <br>" + query , MessageLevel.INFO);

        try {
            connection =  connect(dbConnString,dbUser,dbPass);
            statement = connection.createStatement();
            statement.setQueryTimeout(queryTimeout);
            resultSet = statement.executeQuery(query);
            if (!resultSet.next()) {
                reportAndLog(CommonConstants.FailureClassificationErrors.MISSING_DATA,MessageLevel.INFO);
            }
            else{
                singleValue = resultSet.getString(1);
                reportAndLog("value from query: " + singleValue,MessageLevel.INFO);

            }

        } catch (SQLException | ClassNotFoundException sqlException) {
            reportAndLog("Query failed with error " + sqlException.getMessage(),MessageLevel.ERROR);
            sqlException.printStackTrace();
        }
        finally {
            disconnect(connection,resultSet,statement);
        }
        return singleValue;
    }


    /**
     * This method receives a Query then loops through the query results and stores them into a list
     * if more than 1 row is fetched it will only return the first one.
     * This is best for a single-row query results
     *
     * @author zvika.sela
     * @since 15.06.2021
     * @param query  the given query we wish to execute
     * @param dbConnString the server address+user+pass
     * @return a List that holds the resulting row, if more than 1 row is fetched it will only return the first one.
     */
    @Override
    public List<String> getSingleRowResult(String query, String dbConnString, String dbUser, String dbPass) {
        List<String> singleRowResults = null;
        ResultSet resultSet = null;
        Statement statement = null;
        Connection connection = null;

        reportAndLog("Connecting to:" + dbConnString + "with user " + dbUser,MessageLevel.INFO);
        reportAndLog("Executing Query: <br>" + query , MessageLevel.INFO);

        try {
            connection = connect(dbConnString,dbUser,dbPass);
            statement = connection.createStatement();
            statement.setQueryTimeout(queryTimeout);

            resultSet = super.executeQuery(statement, query);
            int queryColumnCount = resultSet.getMetaData().getColumnCount();

            int count = 0;

            if (!resultSet.next()) {
                reportAndLog(CommonConstants.FailureClassificationErrors.MISSING_DATA,MessageLevel.INFO);
            }
            else{
                singleRowResults = new ArrayList<>();
                for (int i = 1; i <= queryColumnCount; i++) {
                    singleRowResults.add(resultSet.getString(i));
                }

            }

            if (singleRowResults!=null)
                reportAndLog("Query results: <br>" + Arrays.toString(singleRowResults.toArray()),MessageLevel.INFO);


        } catch (SQLException | ClassNotFoundException sqlException) {
            reportAndLog("Query failed with error " + sqlException.getMessage(),MessageLevel.ERROR);
            sqlException.printStackTrace();
        }
        finally {
            disconnect(connection,resultSet,statement);
        }
        return singleRowResults;
    }

    /**
     * Execute a query
     * @param query the query to execute
     * @param dbConnString connection string
     * @param dbUser db user
     * @param dbPass db password
     * @param ml  message level in case the insert did not insert a new record
     * @author genosar.dafna
     * @since 15.05.2023
     */
    public void executeUpdate(String query, String dbConnString, String dbUser, String dbPass, MessageLevel ml) throws SQLException, ClassNotFoundException {

        PreparedStatement statement;
        Connection connection;

        reportAndLog("Connecting to:" + dbConnString + "with user " + dbUser, MessageLevel.INFO);
        reportAndLog("Executing Query: <br>" + query, MessageLevel.INFO);

        connection = connect(dbConnString, dbUser, dbPass);
        statement = connection.prepareStatement(query);
        statement.setQueryTimeout(queryTimeout);

        // Execute the statement
        int rowsInserted = statement.executeUpdate();

        // Check if the statement was executed successfully
        if (rowsInserted > 0) {
            reportAndLog("A new record was inserted into the table.", MessageLevel.INFO);
        }
        else {
            reportAndLog("A new record was not inserted into the table.", ml);
        }

        // Close the statement and connection
        statement.close();
        connection.close();
    }

    /**
     * This method receives a Query then loops through the query results and stores them into a list
     * This is best for a multi-column  queries
     *
     * @author zvika.sela
     * @since 15.06.2021
     * @param query        the given query we wish to execute
     * @param dbConnString the server address+user+pass
     * @return a List that holds a list of the table's rows
     */
    @Override
    public List<List<String>> getQueryResultTable (String query, String dbConnString, String dbUser, String dbPass) {
        List<List<String>> queryResultsList = new ArrayList<>();
        ResultSet resultSet = null;
        Statement statement = null;
        Connection connection = null;



        reportAndLog("Connecting to:" + dbConnString + " with user " + dbUser,MessageLevel.INFO);
        reportAndLog("Executing Query: <br>" + query , MessageLevel.INFO);
        try {
            connection = DriverManager.getConnection(dbConnString);
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



        } catch (SQLException sqlException) {
            reportAndLog("Query failed with error " + sqlException.getMessage(),MessageLevel.ERROR);
            sqlException.printStackTrace();

        }
        finally {
            disconnect(connection,resultSet,statement);
        }

        return queryResultsList;
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
//    public List<String> getResultsFromQuery(String query, String dbConnString, String dbUser, String dbPass) {
//        List<String> resultsList = new ArrayList<>();
//        Connection connection=null;
//        Statement statement =null;
//        ResultSet resultSet =null;
//        try {
//
//            connection = connect(dbConnString,dbUser,dbPass);
//            statement = connection.createStatement();
//            statement.setQueryTimeout(queryTimeout);
//
//            reportAndLog("Executing Query: <br>" + query , MessageLevel.INFO);
//            resultSet = statement.executeQuery(query);
//
//            while (resultSet.next()) {
//                String value = resultSet.getString(1);
//                if(value == null)
//                    resultsList.add(value);
//                else
//                    resultsList.add(value.trim());
//            }
//
//            if(resultsList.isEmpty())
//                reportAndLog(Constants.FailureClassificationErrors.MISSING_DATA,MessageLevel.INFO);
//            else
//                reportAndLog("Query results: <br>" + Arrays.toString(resultsList.toArray()),MessageLevel.INFO);
//
//        }
//        catch (SQLException | ClassNotFoundException sqlException) {
//            sqlException.printStackTrace();
//            logger.error(sqlException.getMessage());
//        }
//        finally {
//            disconnect(connection,resultSet,statement);
//        }
//        return resultsList;
//    }

    public List<String> getResultsFromQuery(String query, String dbConnString, String dbUser, String dbPass) {

        return getResultsFromQuery(query, dbConnString, dbUser, dbPass, true, true);
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
     * @param reportQuery true/false if to add the query string to the report (in cases when the query is very long)
     * @param reportResults true/false if to add the results string to the report (in cases when the results are very long)
     * @return a List of database row
     */
    public List<String> getResultsFromQuery(String query, String dbConnString, String dbUser, String dbPass, boolean reportQuery, boolean reportResults) {
        List<String> resultsList = new ArrayList<>();
        Connection connection=null;
        Statement statement =null;
        ResultSet resultSet =null;
        try {

            connection = connect(dbConnString,dbUser,dbPass);
            statement = connection.createStatement();
            statement.setQueryTimeout(queryTimeout);

            if(reportQuery)
                reportAndLog("<b>Executing Query:</b> <br>" + query , MessageLevel.INFO);
            else
                logger.info("Executing Query: <br>" + query);

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
            else{
                if(reportResults)
                    reportAndLog("Query results: <br>" + Arrays.toString(resultsList.toArray()),MessageLevel.INFO);
                else
                    logger.info("Query results: <br>" + Arrays.toString(resultsList.toArray()));
            }
        }
        catch (SQLException | ClassNotFoundException sqlException) {
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
     * Return a Hash map containing the query result as a list of HashMaps of String, string
     * @param query query to the DB
     * @param dbConnString DB connection string
     * @param dbUser DB user
     * @param dbPass DB password
     * @return a Hash map containing the query result. i.e column names and their values
     *
     * @author Dafna Genosar
     * @since 09.10.2022
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

            reportAndLog("Executing Query: <br>" + query , MessageLevel.INFO);

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

    /**
     * Return a Hash map containing the query result as a list of HashMaps of String, string
     * @param query query to the DB
     * @param dbName DB Name
     * @param serverName server name
     * @param dbUser DB user
     * @param dbPassword DB password
     * @return a Hash map containing the query result. i.e column names and their values
     *
     * @author umflat.lior
     * @since 31.5.2023
     */
    public <K, V, T extends Map<K, V>, L extends List<T>> L getQueryStringResultMapForAzureSQL(String query, String serverName,String dbName,String dbUser,String dbPassword) {

        LinkedList<LinkedHashMap<String, String>> queryResultsList;
        Connection connection=null;
        Statement statement =null;
        ResultSet resultSet =null;
        try {
            connection = connect(serverName,dbName,dbUser,dbPassword);
            statement = connection.createStatement();
            statement.setQueryTimeout(queryTimeout);

            reportAndLog("Executing Query: <br>" + query , MessageLevel.INFO);

            resultSet = super.executeQuery(statement, query);

            queryResultsList = super.getQueryStringResultMap(resultSet);

            if(queryResultsList.isEmpty())
                reportAndLog(CommonConstants.FailureClassificationErrors.MISSING_DATA,MessageLevel.INFO);

        }
        catch (SQLException sqlException) {
            reportAndLog("Query failed with error " + sqlException.getMessage(),MessageLevel.ERROR);
            throw new Error(String.format("Failed to retrieve Query String Result Map.<br>Error: %s<br><br>", sqlException.getMessage()));
        }
        finally {
            disconnect(connection,resultSet,statement);
        }
        return (L)queryResultsList;
    }

    /**
     * Execute an SQL stored procedure with any number of parameters. params can also be null
     * @param storeProcedureName Sql statement to execute
     * @param dbConnString DB connection string
     * @param dbUser DB user
     * @param dbPass DB password
     * @param params Array of all parameters
     * @return Map<String, Object> of results
     * @author Dafna Genosar (copied tzvika.sela implementation from Oracle)
     * @since 27.03.2023
     */
    public <K, V, T extends Map<K, V>> T runStoredProcedure(String storeProcedureName, String dbConnString, String dbUser, String dbPass, @Nullable Object... params)
    {
        StoredProcedure storedProcedure;

        DriverManagerDataSource dataSource = new DriverManagerDataSource(dbConnString, dbUser, dbPass);

        storedProcedure = new GenericStoredProcedure();
        storedProcedure.setDataSource(dataSource);
        storedProcedure.setSql(storeProcedureName);
        storedProcedure.setFunction(false);

        storedProcedure.setQueryTimeout(queryTimeout);

        if(params == null){
            Map<String, ?> inParams = new HashMap<>();
            try{
                return (T)storedProcedure.execute(inParams);
            } catch (Exception e) {
                reportAndLog("Query failed with error " + e.getMessage(), MessageLevel.ERROR);
                throw new Error("Stored Procedure Failed");
            }
        }
        else {
            int i = 1;

            for (Object param : params) {

                int sqlType;

                if (param instanceof Integer)
                    sqlType = Types.INTEGER;
                else if (param instanceof String)
                    sqlType = Types.CHAR;
                else if (param instanceof Arrays)
                    sqlType = Types.ARRAY;
                else if (param instanceof Boolean)
                    sqlType = Types.BOOLEAN;
                else if (param instanceof Double)
                    sqlType = Types.DOUBLE;
                else
                    throw new Error(String.format("Parameter %d received in MSSqlServerDBUtil.runStoredProcedure is not supported in the method: %s", i, param.getClass().getName()));

                storedProcedure.declareParameter(new SqlParameter("param" + i, sqlType));
                i++;
            }

            try{
                return (T)storedProcedure.execute(params);
            } catch (Exception e) {
                reportAndLog("Query failed with error " + e.getMessage(), MessageLevel.ERROR);
                throw new Error("Stored Procedure Failed");
            }
        }
    }
}

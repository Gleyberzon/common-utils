package AssertionsWithReport;

import com.relevantcodes.extentreports.LogStatus;
import Enumerations.MessageLevel;
import org.junit.jupiter.api.Assertions;
import ReportUtils.ExtentReportUtils;

import javax.annotation.Nullable;

import static ReportUtils.Report.reportAndLog;

/**
 * This class contain various type of assert and send a message to report accordingly
 */
public class AssertsWithReport {

    /**
     * Commit assert true and write to the report success/error accordingly
     * @param condition - true of false
     * @param successMessage - Message to log In case of success
     * @param errorMessage - Message to log in case of failure
     * @modifier dafna genosar
     * @since 28.11.2022
     * @since 05.04.2023
     */
    public static void assertTrue(boolean condition, @Nullable String successMessage, String errorMessage)
    {
        if (!condition) {
            reportAndLog(errorMessage, MessageLevel.FAIL);
            Assertions.fail(errorMessage);
        }
        else
        {
            if(successMessage != null && !successMessage.contains(">null<"))
                ExtentReportUtils.extentLogger(LogStatus.PASS,successMessage);
            Assertions.assertTrue(true);
        }
    }

    /**
     * Commit assert false and write to the report success/error accordingly
     * @param condition - true of false
     * @param successMessage - Message to log In case of success
     * @param errorMessage - Message to log in case of failure
     * @modifier dafna genosar
     * @since 28.11.2022
     * @since 05.04.2023
     */
    public static void assertFalse(boolean condition, @Nullable String successMessage,String errorMessage)
    {
        if (condition) {
            reportAndLog(errorMessage, MessageLevel.FAIL);
            Assertions.fail(errorMessage);
        }
        else
        {
            if(successMessage != null && !successMessage.contains(">null<"))
                ExtentReportUtils.extentLogger(LogStatus.PASS,successMessage);
            Assertions.assertTrue(true);
        }
    }

    /**
     * Commit assert equals between 2 objects and write to the report success/error accordingly
     * @param expected Object
     * @param Actual Object
     * @param successMessage String
     * @param errorMessage String
     */
    public static void assertEquals(Object expected, Object Actual, @Nullable String successMessage, String errorMessage)
    {
        try {
            Assertions.assertEquals(expected, Actual,errorMessage);
            if(successMessage != null && !successMessage.contains(">null<")) ExtentReportUtils.extentLogger(LogStatus.PASS,successMessage);
        }
        catch (Throwable e){
            reportAndLog(errorMessage, MessageLevel.FAIL);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Commit assert not equals between 2 objects and write to the report success/error accordingly
     * @param unexpected Object
     * @param actual Object
     * @param successMessage String
     * @param errorMessage String
     * @author genosar.dafna
     * @since 21.11.2022
     * @since 05.04.2023
     */
    public static void assertNotEquals(Object unexpected, Object actual, @Nullable String successMessage, String errorMessage)
    {
        try {
            Assertions.assertNotEquals(unexpected, actual, errorMessage);
            if(successMessage != null && !successMessage.contains(">null<")) ExtentReportUtils.extentLogger(LogStatus.PASS,successMessage);
        }
        catch (Throwable e){
            reportAndLog(errorMessage, MessageLevel.FAIL);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Commit assert object is Null and write to the report success/error accordingly
     * @param actual Object
     * @param successMessage String
     * @param errorMessage String
     * @author genosar.dafna
     * @since 21.11.2022
     * @since 05.04.2023
     */
    public static void assertNull(Object actual, @Nullable String successMessage, String errorMessage)
    {
        try {
            Assertions.assertNull(actual,errorMessage);
            if(successMessage != null && !successMessage.contains(">null<")) ExtentReportUtils.extentLogger(LogStatus.PASS,successMessage);
        }
        catch (Throwable e){
            reportAndLog(errorMessage, MessageLevel.FAIL);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Commit assert object is Not Null and write to the report success/error accordingly
     * @param actual Object
     * @param successMessage String
     * @param errorMessage String
     * @author genosar.dafna
     * @since 21.11.2022
     * @since 05.04.2023
     */
    public static void assertNotNull(Object actual, @Nullable String successMessage, String errorMessage)
    {
        try {
            Assertions.assertNotNull(actual,errorMessage);
            if(successMessage != null && !successMessage.contains(">null<")) ExtentReportUtils.extentLogger(LogStatus.PASS,successMessage);
        }
        catch (Throwable e){
            reportAndLog(errorMessage, MessageLevel.FAIL);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Commit assert equals between 2 objects and write to the report only when fails
     * @param expected Object
     * @param Actual Object
     * @param errorMessage String
     */
    public static void assertEqualsWriteOnlyFailuresToReport(Object expected, Object Actual, String errorMessage)
    {
        try {
            Assertions.assertEquals(expected, Actual,errorMessage);
        }
        catch (Throwable e){
            reportAndLog(errorMessage, MessageLevel.FAIL);
            Assertions.fail(errorMessage);
        }
    }

    /**
     * Commit assert true and write to the report success/error accordingly
     * @param condition - condition to be asserted
     * @param message - Message to log example:message should be display
     * @author Yael Rozenfeld
     * @since 17.11.2021
     * @modifier dafna genosar
     * @since 28.11.2022
     */
    public static void assertTrue(boolean condition, String message)
    {
        if (!condition) {
            reportAndLog(message, MessageLevel.FAIL);
            Assertions.fail(message);
        }
        else
        {
            ExtentReportUtils.extentLogger(LogStatus.PASS,message);
            Assertions.assertTrue(true);
        }
    }


    /**
     * Commit assert false and write to the report success/error accordingly
     * @param condition - true of false
     * @param message - Message to log. example:message shouldn't be display
     * @author Yael Rozenfeld
     * @since 17.11.2021
     * @modifier dafna genosar
     * @since 28.11.2022
     */
    public static void assertFalse(boolean condition,String message)
    {
        if (condition) {
            reportAndLog(message, MessageLevel.FAIL);
            Assertions.fail(message);
        }
        else
        {
            ExtentReportUtils.extentLogger(LogStatus.PASS,message);
            Assertions.assertTrue(true);
        }
    }

    /**
     * Commit assert equals between 2 objects and write to the report success/error accordingly
     * @param expected Object
     * @param Actual Object
     * @param message String message will be written in the report<br/>,
     *               example: for field xxx expected value is '' and current is ''
     * @author Yael Rozenfeld
     * @since 17.11.2021
     */
    public static void assertEquals(Object expected, Object Actual, String message)
    {
        try {
            Assertions.assertEquals(expected, Actual,message);
            ExtentReportUtils.extentLogger(LogStatus.PASS,message);
        }
        catch (Throwable e){
            reportAndLog(message, MessageLevel.FAIL);
            Assertions.fail(message);
        }
    }

    /**
     * Commit assert not equals between 2 objects and write to the report success/error accordingly
     * @param unexpected Object
     * @param actual Object
     * @param message String message will be written in the report<br/>,
     * example: for field xxx expected value is '' and current is ''
     * @author genosar.dafna
     * @since 21.11.2022
     */
    public static void assertNotEquals(Object unexpected, Object actual, String message)
    {
        try {
            Assertions.assertNotEquals(unexpected, actual, message);
            ExtentReportUtils.extentLogger(LogStatus.PASS,message);
        }
        catch (Throwable e){
            reportAndLog(message, MessageLevel.FAIL);
            Assertions.fail(message);
        }
    }
}

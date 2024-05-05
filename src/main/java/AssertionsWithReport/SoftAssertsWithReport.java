package AssertionsWithReport;

import Managers.ReportInstanceManager;
import Managers.WebDriverInstanceManager;
import com.relevantcodes.extentreports.LogStatus;
import org.openqa.selenium.WebDriver;
import ReportUtils.ReportStyle;

import java.util.ArrayList;
import java.util.List;

import static imageUtils.ScreenCaptureUtils.takeScreenShot;

/**
 * SoftAssertsWithReport - assertion will not stop the test.<br/>
 * in the end of using SoftAssertsWithReport user assertAll to fail the test
 * example <code>
 * SoftAssertsWithReport softAssert = new SoftAssertsWithReport();
 * softAssert.assertFalse(false,"test success assert false" ,"test Fail assert false");
 * softAssert.assertTrue(false,"test success assert true" ,"test Fail assert true");
 * softAssert.assertEquals("a","b","test success assert equals","test fail assert equals");
 * softAssert.assertEquals("a","a","test success assert equals","test fail assert equals");
 * softAssert.assertAll();
 * </code>
 *
 * @author Rozenfeld.Yael
 * @since 04/2021
 */

public class SoftAssertsWithReport {
    private List<Throwable> assertions;

    /**
     * Constructor. Initializes global variable assertions as empty list of Throwables
     */
    public SoftAssertsWithReport() {
        assertions = new ArrayList<>();
    }

    /**
     * assertTrue - test will continue running anyway.
     *
     * @param condition boolean condition
     * @param successMessage Message to be written if condition is true
     * @param errorMessage Message to be written if condition is false
     * @author Rozenfeld.Yael
     * @since 04/2021
     */
    public void assertTrue(boolean condition, String successMessage, String errorMessage) {
        try {
            AssertsWithReport.assertTrue(condition, ReportStyle.getSuccessMessage(successMessage), ReportStyle.getFailureMessage(errorMessage));
        } catch (Throwable t) {
            onSoftAssertFail(t);
        }
    }

    /**
     * assertFalse - test will  continue running anyway
     *
     * @param condition boolean condition
     * @param successMessage Message to be written if condition is false
     * @param errorMessage Message to be written if condition is true
     * @author Rozenfeld.Yael
     * @since 04/2021
     */
    public void assertFalse(boolean condition, String successMessage, String errorMessage) {
        try {
            AssertsWithReport.assertFalse(condition, ReportStyle.getSuccessMessage(successMessage), ReportStyle.getFailureMessage(errorMessage));
        } catch (Throwable t) {
            onSoftAssertFail(t);
        }
    }

    /**
     * assertEquals - test will  continue running anyway
     *
     * @param expected Expected result
     * @param Actual Actual result
     * @param successMessage Message to be written if actual equals expected
     * @param errorMessage Message to be written if actual doesn't equal expected
     * @author Rozenfeld.Yael
     * @since 04/2021
     */
    public void assertEquals(Object expected, Object Actual, String successMessage, String errorMessage) {
        try {
            AssertsWithReport.assertEquals(expected, Actual, ReportStyle.getSuccessMessage(successMessage), ReportStyle.getFailureMessage(errorMessage));
        } catch (Throwable t) {
            onSoftAssertFail(t);
        }
    }

    /**
     * assertNotEquals - test will  continue running anyway
     *
     * @param unexpected unexpected result
     * @param actual Actual result
     * @param successMessage Message to be written if actual equals expected
     * @param errorMessage Message to be written if actual doesn't equal expected
     * @author genosar.dafna
     * @since 21.11.2022
     */
    public void assertNotEquals(Object unexpected, Object actual, String successMessage, String errorMessage) {
        try {
            AssertsWithReport.assertNotEquals(unexpected, actual, ReportStyle.getSuccessMessage(successMessage), ReportStyle.getFailureMessage(errorMessage));
        } catch (Throwable t) {
            onSoftAssertFail(t);
        }
    }

    /**
     * assertNull - test will  continue running anyway
     *
     * @param actual Actual result
     * @param successMessage Message to be written if actual equals expected
     * @param errorMessage Message to be written if actual doesn't equal expected
     * @author genosar.dafna
     * @since 21.11.2022
     */
    public void assertNull(Object actual, String successMessage, String errorMessage) {
        try {
            AssertsWithReport.assertNull(actual, ReportStyle.getSuccessMessage(successMessage), ReportStyle.getFailureMessage(errorMessage));
        } catch (Throwable t) {
            onSoftAssertFail(t);
        }
    }

    /**
     * assertNotNull - test will  continue running anyway
     *
     * @param actual Actual result
     * @param successMessage Message to be written if actual equals expected
     * @param errorMessage Message to be written if actual doesn't equal expected
     * @author genosar.dafna
     * @since 21.11.2022
     */
    public void assertNotNull(Object actual, String successMessage, String errorMessage) {
        try {
            AssertsWithReport.assertNotNull(actual, ReportStyle.getSuccessMessage(successMessage), ReportStyle.getFailureMessage(errorMessage));
        } catch (Throwable t) {
            onSoftAssertFail(t);
        }
    }

    /**
     * assertAll - in the end of using SoftAssertsWithReport <br/>
     * call this function to fail the test in case soft assert failed
     *
     * @author Rozenfeld.Yael
     * @since 04/2021
     */
    public void assertAll() {
        AssertsWithReport.assertTrue(this.assertions.isEmpty(), ReportStyle.getSuccessMessage("all soft assert test passed"), ReportStyle.getFailureMessage("test fail on soft assert"));
    }

    /**
     * onSoftAssertFail  - catch the  throwable on soft assert<br/>
     * and write it to report and attach Screenshot
     *
     * @param throwable - the throwable was caught
     * @author Rozenfeld.Yael
     * @since 04/2021
     */
    private void onSoftAssertFail(Throwable throwable) {
        assertions.add(throwable);
        System.out.println(throwable.getStackTrace());
        WebDriver driver = WebDriverInstanceManager.getDriverFromMap();

        //Don't take screenshots if there is no web driver
        if (driver != null)
        {
            String path = takeScreenShot(driver);
            ReportInstanceManager.getCurrentTestReport().log(LogStatus.FAIL, ReportInstanceManager.getCurrentTestReport().addScreenCapture(path));
        }
    }

    /**
     * Commit assert equals between 2 objects and write to the report only when fails
     *
     * @param expected     Object
     * @param actual       Object
     * @param errorMessage String
     */
    public void assertEqualsWriteOnlyFailuresToReport(Object expected, Object actual, String errorMessage) {
        try {
            AssertsWithReport.assertEqualsWriteOnlyFailuresToReport(expected, actual, ReportStyle.getFailureMessage(errorMessage));
        } catch (Throwable e) {
            onSoftAssertFail(e);
        }
    }



    /**
     * assertTrue - test will continue running anyway.
     * @param condition boolean condition
     * @param message Message to log. example:message should be display
     * @author Rozenfeld.Yael
     * @since 11/2021
     */
    public void assertTrue(boolean condition, String message) {
        try {
            AssertsWithReport.assertTrue(condition, message);
        } catch (Throwable t) {
            onSoftAssertFail(t);
        }
    }

    /**
     * assertFalse - test will  continue running anyway
     *
     * @param condition boolean condition
     * @param message Message to log. example:message shouldn't be display
     * @author Rozenfeld.Yael
     * @since 11/2021
     */
    public void assertFalse(boolean condition, String message) {
        try {
            AssertsWithReport.assertFalse(condition, message);
        } catch (Throwable t) {
            onSoftAssertFail(t);
        }
    }

    /**
     * assertEquals - test will  continue running anyway
     *
     * @param expected Expected result
     * @param Actual Actual result
     * @param message Message to be written in the report<br/>,
     *      *         example: for field xxx expected value is '' and current is ''
     * @author Rozenfeld.Yael
     * @since 11/2021
     */
    public void assertEquals(Object expected, Object Actual, String message) {
        try {
            AssertsWithReport.assertEquals(expected, Actual, message);
        } catch (Throwable t) {
            onSoftAssertFail(t);
        }
    }

    /**
     * assertEquals - test will  continue running anyway
     *
     * @param unexpected unexpected result
     * @param actual Actual result
     * @param message Message to be written in the report<br/>,
     *      *         example: for field xxx expected value is '' and current is ''
     * @author genosar.dafna
     * @since 21.11.2022
     */
    public void assertNotEquals(Object unexpected, Object actual, String message) {
        try {
            AssertsWithReport.assertNotEquals(unexpected, actual, message);
        } catch (Throwable t) {
            onSoftAssertFail(t);
        }
    }
}

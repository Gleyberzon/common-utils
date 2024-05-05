package miscellaneousUtils;

import ReportUtils.ExtentReportUtils;
import com.relevantcodes.extentreports.LogStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ReportUtils.ExtentReportUtils.extentLogger;
public class Verifications
{
    /**
     * Method compares two objects and writes results to log
     * @param actual object for test
     * @param expected how the tested object should be like
     * @param description description of the object for test
     * @return true if the objects are equal, false otherwise
     */
    private static Logger logger = LoggerFactory.getLogger(Verifications.class);
    public static boolean verifyActualIsAsExpected(Object actual, Object expected, String description)
    {
        boolean verified = true;
        try
        {
            logger.info("Verifying that actual " + description + " is " + expected);
            if (expected.equals(actual))
            {
                logger.info("PASS. Expected " + description + " -" + expected + "- verified");
                extentLogger(LogStatus.PASS, "Expected " + description + " -" + expected + "- verified");
            }

            else
            {
                verified = false;
                logger.info("FAIL. Expected " + description + " -" + expected + "- not verified");
                extentLogger(LogStatus.FAIL, "Expected " + description + " -" + expected + "- not verified");
            }
        }
        catch(Exception e)
        {
            verified = false;
            logger.info("Caught exception: " + e);
        }

        return verified;
    }

    public static boolean verifyActualIsAsExpected(Object actual, Object expected, String description, String screenshotFilePath)
    {
        boolean isVerified = verifyActualIsAsExpected(actual, expected, description);
        if (!isVerified)
        {
            ExtentReportUtils.attachScreenshotToExtentReport(screenshotFilePath);
        }

        return isVerified;
    }


}

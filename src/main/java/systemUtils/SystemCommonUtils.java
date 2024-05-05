package systemUtils;

import Enumerations.MessageLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ReportUtils.Report.reportAndLog;

public class SystemCommonUtils {

    private static Logger logger = LoggerFactory.getLogger(SystemCommonUtils.class);

    /**
     * Wrapping sleep method
     * @param milisecs Number of miliseconds to pause the run
     */
    public static void sleep(int milisecs)
    {
        try
        {
            Thread.sleep(milisecs);
            logger.info("Pausing run for " + milisecs + " miliseconds");
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the test is running on Kubernetes, Windows or locally.
     * @return true/false if the test is running locally
     * @author genosar.dafna
     * @since 15.05.2022
     * @since 13.02.2023
     * @since 14.02.2023
     */
    public static boolean isTestRunningLocally()
    {
        if(System.getProperty("hubURL") == null)
        {
            reportAndLog("Test is running locally. Property 'hubURL' is null", MessageLevel.INFO);
            return true;
        }
        else if(System.getProperty("hubURL").equalsIgnoreCase("NA"))
        {
            reportAndLog("Test is running on Windows. Property 'hubURL' is " + System.getProperty("hubURL"), MessageLevel.INFO);
            return false;
        }
        else
        {
            reportAndLog("Test is running on Kubernetes. Property 'hubURL' is " + System.getProperty("hubURL"), MessageLevel.INFO);
            return false;
        }
    }
}

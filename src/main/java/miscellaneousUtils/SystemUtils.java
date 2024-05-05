package miscellaneousUtils;

import Enumerations.MessageLevel;

import static ReportUtils.Report.reportAndLog;

public class SystemUtils {

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

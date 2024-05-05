package ReportUtils;

import Managers.ReportInstanceManager;
import Managers.WebDriverInstanceManager;
import com.relevantcodes.extentreports.LogStatus;
import Enumerations.MessageLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

import static ReportUtils.ReportStyle.removeHtmlTagsForLog;

public class Report {

    private static final Logger logger = LoggerFactory.getLogger(Report.class);

    /**
     * Write the current URL to the report including a link to this page
     * @param pageName optional name of the page, if you wish to add the name to the log
     * @return the current URL
     * @author genosar.dafna
     * @since 18.10.2023
     */
    public static String reportCurrentUrl(@Nullable String pageName){

        pageName = pageName == null? "" : pageName;
        String url = WebDriverInstanceManager.getDriverFromMap().getCurrentUrl();
        ReportUtils.Report.reportAndLog(String.format("Opened page:%s<br>" +
                "<a target='_blank' href='%s'</a>%s", pageName, url, url), MessageLevel.INFO);

        return url;
    }

    /**
     * This method prints a message (of certain cardinality) to both report and log
     *
     * @param msg  message to print both to log and report
     * @param ml the message level from the MessageLevel Enumerator
     * @author zvika.sela
     * @since 26.04.2021
     * @author Dafna Genosar
     * @since 15.05.2023
     */
    public static void reportAndLog(String msg, MessageLevel ml){

        String reportMsg = msg.replace("/n", "<br/>").replace("\n", "<br/>");
        String logMsg = removeHtmlTagsForLog(msg);

        switch (ml) {

            case INFO:
                logger.info(logMsg);
                if (ReportInstanceManager.getCurrentTestReport() != null)
                    ExtentReportUtils.extentLogger(LogStatus.INFO, reportMsg);
                break;

            case WARN:
                logger.warn(logMsg);
                if (ReportInstanceManager.getCurrentTestReport() != null) {
                    ExtentReportUtils.extentLogger(LogStatus.WARNING, reportMsg);
                }
                break;

            case ERROR:
                logger.error(logMsg);
                if (ReportInstanceManager.getCurrentTestReport() != null) {
                    ExtentReportUtils.extentLogger(LogStatus.ERROR, reportMsg);
                }
                break;

            case FAIL:
                logger.error(logMsg);
                if (ReportInstanceManager.getCurrentTestReport() != null)
                    ExtentReportUtils.extentLogger(LogStatus.FAIL, reportMsg);
                break;
        }

    }

    /**
     * Report a highlighted bug line
     * @param details the text line to report
     * @author genosar.dafna
     * @since 07.08.2022
     */
    public static void reportBug(String details)
    {
        details = "<mark>" + details + "</mark>";
        ReportInstanceManager.getCurrentTestReport().log(LogStatus.WARNING, details);
    }
}

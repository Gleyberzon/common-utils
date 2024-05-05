package listeners;

import Managers.ReportInstanceManager;
import com.relevantcodes.extentreports.LogStatus;
import miscellaneousUtils.MiscellaneousUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;

import static imageUtils.ScreenCaptureUtils.takeScreenShot;

/**
 * Take screenshot and report on exception on driver
 * @author Yael Rozenfeld
 * @since 5.5.2021
 */
public class WebDriverTakeSSListener extends AbstractWebDriverEventListener {

    /**
     * Take screenshot and report on exception on driver
     * @param throwable Instance of Throwable
     * @param driver Instance of WebDriver
     */
    public void onException(Throwable throwable, WebDriver driver)  {
        ReportInstanceManager.getCurrentTestReport().log(LogStatus.FAIL,throwable.getStackTrace().toString());
        String path = takeScreenShot(driver);
        ReportInstanceManager.getCurrentTestReport().log(LogStatus.FAIL, ReportInstanceManager.getCurrentTestReport().addScreenCapture(path));
    }

}

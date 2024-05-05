package listeners;

import Managers.ReportInstanceManager;
import Managers.WebDriverInstanceManager;
import com.relevantcodes.extentreports.LogStatus;
import jdk.jfr.Category;
import miscellaneousUtils.MiscellaneousUtils;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static imageUtils.ScreenCaptureUtils.takeScreenShot;

/**
 * Listener to Junit that takes the screenshot upon failure on test execution
 * @author sela.zvika
 * @since 09.05.2021
 */
public class JunitTakeSSExtension implements
        AfterTestExecutionCallback {


    /**
     * This method runs after test is finished.
     * It saves the page screen shot and adds it to the report in cases of exceptions in the test execution
     * @param context Instance of ExtensionContext
     * @author dafna.genosar
     * @since 15.11.2022
     */
    @Override
    public void afterTestExecution(ExtensionContext context) {

        //in case of exception we save the screenshot
        if (context.getExecutionException().isPresent()) {
            boolean takeScreenShot = true;

            Category category = context.getRequiredTestClass().getAnnotation(Category.class);
            if (category != null) {
                List<String> categories = Arrays.stream(category.value()).map(s -> s.toLowerCase()).collect(Collectors.toList()) ;
                if(categories.contains("non ui"))
                    takeScreenShot = false;
            }
            if(takeScreenShot) {
                String path = takeScreenShot(WebDriverInstanceManager.getDriverFromMap());
                ReportInstanceManager.getCurrentTestReport().log(LogStatus.FAIL, ReportInstanceManager.getCurrentTestReport().addScreenCapture(path));
            }
        }
    }
}

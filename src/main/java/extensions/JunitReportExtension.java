package extensions;

import Managers.ExtentReportInstanceManager;
import Managers.ReportInstanceManager;
import Store.StoreManager;
import Store.StoreType;
import com.relevantcodes.extentreports.LogStatus;
import constantsUtils.CommonConstants;
import CustomAnnotations.TestDescription;
import Enumerations.MessageLevel;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ReportUtils.ExtentReportUtils;

import java.io.File;

import static Enumerations.TestMetricsEnum.REPORT_STATUS;
import static Enumerations.TestMetricsEnum.TEST_REPORT_EXCEPTION;
import static propertyUtils.PropertyUtils.getGlobalProperty;
import static ReportUtils.Report.reportAndLog;

/**
 * Listener to Junit which is in charge of writing events to the Report
 * @author sela.zvika
 * @since 09.05.2021
 *
 * @modified by Lior Umflat
 * @modifiedDate - 24.5.2021
 *
 */
public class JunitReportExtension implements AfterTestExecutionCallback, BeforeEachCallback, BeforeAllCallback, AfterEachCallback, AfterAllCallback {
    private static final Logger logger= LoggerFactory.getLogger(JunitReportExtension.class);

    /**
     * this method starts the report with the class @DisplayName as its report's name.
     *
     * @author - Tzvika Sela
     * @since - 20.5.2021
     */
    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        //set downloads folder
        setDownloadsFolder();


        //for linux set oracle system property to false. Seems Linux doesn't support FAN
        if (SystemUtils.IS_OS_LINUX){
            logger.info("setting Oracle fanEnabled to false");
            System.setProperty("oracle.jdbc.fanEnabled","false");
        }

        DisplayName dn = extensionContext.getRequiredTestClass().getAnnotation(DisplayName.class);
        if (dn != null) {
            //init the Extent Report
            ExtentReportUtils.initExtentReports(dn.value());

        } else {
            throw new Exception("missing @DisplayName for Class");
        }
    }




    /**
     * this method starts the report with the test @DisplayName and custom annotation @TestDescription
     *
     * @author - Yael Rosenfeld and Lior Umflat
     * @since - 11.5.2021
     */
    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        //empty the Thread Store so we get an empty store per test
        StoreManager.removeStore(StoreType.LOCAL_THREAD);

        String classDisplayName = extensionContext.getRequiredTestClass().getAnnotation(DisplayName.class).value();
        TestDescription n = extensionContext.getTestMethod().get().getAnnotation(TestDescription.class);
        if (n != null && extensionContext.getDisplayName() != null) {
            ExtentReportUtils.startTestReport(extensionContext.getDisplayName(), n.value(),ExtentReportInstanceManager.getCurrentExtentReport(classDisplayName));
            //report class name and test name, used for
            String className=extensionContext.getRequiredTestClass().getName();
            className=className.substring(className.lastIndexOf(".")+1);
            reportAndLog(String.format("To run this test by unit test use value <b>%s#%s</b> in 'Files' parameter",className,extensionContext.getRequiredTestMethod().getName()), MessageLevel.INFO);
        } else {
            throw new Exception("missing @DisplayName or @TestDescription");
        }
    }

    /**
     * this method makes sure report's result is synched with test result
     *
     * @param context the context of the test (inner state).
     * @author sela.zvika
     * @since 09.05.2021
     * @modifiedBy plot.ofek
     * @modifiedDate 1-FEB-2022
     */
    @Override
    public void afterTestExecution(ExtensionContext context) {
        //here we make sure report state is equal to test state
        if (context.getExecutionException().isPresent()) {
            String errorMessage = context.getExecutionException().get().getCause() != null? context.getExecutionException().get().getCause().getMessage() : context.getExecutionException().get().getMessage();
            ReportInstanceManager.getCurrentTestReport().log(LogStatus.FAIL, errorMessage);
            //update to ELK report with test exception
            StoreManager.getStore(StoreType.LOCAL_THREAD).putValueInStore(TEST_REPORT_EXCEPTION,errorMessage);
        } else {
            ReportInstanceManager.getCurrentTestReport().log(LogStatus.PASS, "Finished with success");

        }
        //update to ELK report with Extent report status
        StoreManager.getStore(StoreType.LOCAL_THREAD).putValueInStore(REPORT_STATUS,ReportInstanceManager.getCurrentTestReport().getRunStatus().toString());
    }


    /**
     * close the ExtentTest afterEach.
     *
     * @author - Lior Umflat
     * @since - 24.5.2021
     */
    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        String classDisplayName = extensionContext.getRequiredTestClass().getAnnotation(DisplayName.class).value();
        ExtentReportUtils.endTestReport(ExtentReportInstanceManager.getCurrentExtentReport(classDisplayName));

    }

    /**
     * close the ExtentReport afterAll
     *
     * @author - Lior Umflat
     * @since - 24.5.2021
     */
    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        String classDisplayName = extensionContext.getRequiredTestClass().getAnnotation(DisplayName.class).value();
        ExtentReportUtils.finalizeExtentReport(ExtentReportInstanceManager.getCurrentExtentReport(classDisplayName));

    }


    /**
     * Change download folder path in case we run from K8s and create the structure
     *
     * @author Sela Zvika
     * @since 05.18.2022
     */
    public static synchronized void setDownloadsFolder(){
        // if gridHubUrl is K8s then change Download folder constant to K8s Node's shared folder
        String jobName = System.getProperty("jobName");
        String buildId = System.getProperty("BuildID");
        String gridVersion = System.getProperty("seleniumGridVersion");

        if(gridVersion!=null && gridVersion.toLowerCase().startsWith("v") && jobName!=null && buildId!=null){
            CommonConstants.EnvironmentParams.DOWNLOADS_FOLDER = getGlobalProperty("kubernetes_download_path") + File.separator
                    + jobName + File.separator + buildId + File.separator;
        }
        //in case of Kuberenetes MSO cluster we don't have a persistent file storage
        if(jobName!=null && buildId!=null && jobName.equalsIgnoreCase("mvn-test")){
            CommonConstants.EnvironmentParams.DOWNLOADS_FOLDER = getGlobalProperty("kubernetes_download_path") + File.separator;
        }

        File downloadFolderPath = new File(CommonConstants.EnvironmentParams.DOWNLOADS_FOLDER);
        if (!downloadFolderPath.exists())
            downloadFolderPath.mkdirs();

    }

}

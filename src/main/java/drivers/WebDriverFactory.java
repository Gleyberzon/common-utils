package drivers;

import Managers.WebDriverInstanceManager;
import Store.StoreManager;
import Store.StoreType;
import constantsUtils.CommonConstants;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import propertyUtils.Property;
import seleniumUtils.GenericMobileCapabilities;
import systemUtils.ExecuteRunTimeCmd;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static Enumerations.Browsers.CHROME;
import static ReportUtils.Report.reportAndLog;


/**
 * this class initializes the driver according to platform and browser type (in web tests) that we get by dependency injection.
 * to use dependency injection by terminal copy and change accordingly the following line -
 * mvn -Dtest=<class name>#<test name> test -Dplatform=<platform name> -Dbrowser=<browser name>
 * <platform name> can be web / android / ios / windows (default web)
 * <browser name> can be firefox /chrome / ie (default chrome)
 * example for web command -
 * mvn -Dtest=User_management_login_and_forgot_password_Test#enter_to_forgot_password_page test -Dplatform=web -Dbrowser=chrome
 */
public class WebDriverFactory {
    private static final Logger logger= LoggerFactory.getLogger(WebDriverFactory.class);

    final public String[] MINIMUM_CAPS_ANDROID = {"appPackage", "appActivity", "platformName"};
    final public String[] MINIMUM_CAPS_IOS = {"bundleId"};


    /**
     * Initialize the driver according to platform name that will be given using dependency injection
     * after the driver is created it will be added to the WebDriverInstanceManager
     *
     * @author - Yael Rozenfeled and Lior Umflat
     * @since - 10.5.2021
     */
    public void initDriver() {

        // Step 1 - Get the platform (web, android, ios, windows, etc). Default = web
        String platform = System.getProperty("platform");
        String propPath = "./src/main/resources/static_params.properties";
        if (platform == null) {
            Property prop = new Property(propPath);
            platform = prop.getProperty("platform");
            if (platform == null || platform.isEmpty())
                platform = "web";
        }
        switch (platform) {
            case "mobile":
            case "android":
                // No break on purpose. Both android and ios goes to initMobile()
            case "ios":
                initMobileDriver(platform,propPath);
                break;
            case "windows":
                //TODO add winAppDriver init
                break;
            //default = web
            default:
                initBrowser();
                break;
        }

    }

    /**
     * Get the desidred capabilities for mobile activation based on platform name only, and use the initMobileDriver to
     * actually initizalize the driver
     * TODO: in the future, this method should be deleted and ALL activations of driver - mobile/web/Desktop should
     * be invoked using DesiredCapabilities
     *
     * @param platformName Currently supports Android and IOS only
     */
    public void initMobileDriver(String platformName,String propPath){

        GenericMobileCapabilities caps = new GenericMobileCapabilities(platformName,propPath);
        initMobileDriver(caps.getCapabilities());
    }

    /**
     * Get the desidred capabilities for mobile activation based on desired capabilities object, and use the
     * createRemoteDriver to actually initizalize the driver
     * TODO: in the future, this method should be changed from initMobileDriver to initDriver since all driver activation
     * should be invoked using DesiredCapabilities
     *
     * @param capabilities A capability object to be used to initialize the mobile driver
     */
    public void initMobileDriver(Capabilities capabilities){
        String address;
        if (capabilities.getCapabilityNames().contains("accessKey"))
            address = "https://cloud.seetest.io/wd/hub";
        else{
            address = "http://local-";
            address += capabilities.getCapability("platformName").toString().toLowerCase();
        }

        try{

            createRemoteDriver(new URL(address), capabilities);
        } catch(Throwable t){
            logger.error("Error instantiating local TesnetDriver", t);
            throw new RuntimeException(t);
        }
    }

    /**
     * Get the desidred capabilities and remote URL for mobile activation, and use the createRemoteDriver to actually
     * initizalize the driver
     * TODO: in the future, this method should be changed from initMobileDriver to initDriver since all driver activation
     * should be invoked using DesiredCapabilities
     * @param remoteAddress remote url address (mainly used form ExperiTest, BrowserStack, Perfecto Mobile etc.)
     * @param capabilities Capabilities object to connect to mobile device
     */
    public void initMobileDriver(URL remoteAddress, Capabilities capabilities) {
        try{
            createRemoteDriver(remoteAddress, capabilities);
        }
        catch(Throwable t){
            logger.error("Error instanciate local TesnetDriver", t);
            throw new RuntimeException(t);
        }

    }


    /**
     * Handles the initialization and creation of the mobile driver instance. It supports both local and remote
     * activation of the mobile driver. It uses platformName to determine the type of activation needed. Currently,
     * only IOS and Android are supported. <b>Note! Your DesiredCapabiliteis must incluse 'platformName' in order
     * for this method to work properly</b>
     * @param remoteAddress The address to invoke the mobile driver. Can be either a remote address or a local one.
     * @param capabilities Object with all the needed parameters. Make sure you include platformName within it.
     */
    private void createRemoteDriver(URL remoteAddress, Capabilities capabilities){

        // Validation
        if (!meetsMinimum(capabilities)){
            String[] minimumCaps = capabilities.getCapability("platformName") == Platform.ANDROID?
                    MINIMUM_CAPS_ANDROID: MINIMUM_CAPS_IOS;

            throw new RuntimeException("Desired capabilities does not meet minimum requirements. \n" +
                    "Capabilities are: " + capabilities.toString() + "\n"+
                    "Minimum is: " + Arrays.toString(minimumCaps));
        }

        String browserName = capabilities.getBrowserName();
        String strRemoteAddress = remoteAddress.toString();

        TesnetMobileDriver driver;

        // Local driver
        if (strRemoteAddress.contains("local")){
            if (strRemoteAddress.contains("local-android")) {
                try {
                    //Kill node to d/c any open ports left from previous sessions
                    Runtime.getRuntime().exec("taskkill /im node.exe /f");
                    //clear cache before starting
                    Runtime.getRuntime().exec("adb shell pm clear " + capabilities.getCapability("appPackage"));
                }
                catch(Exception e){
                    reportAndLog("Failed to delete application cache", Enumerations.MessageLevel.INFO);
                }
                try {
                    driver = new TesnetMobileDriver(new AndroidDriver(capabilities));
                }
             catch(Throwable t){
                logger.error("Error instantiating Tesnet Mobile driver", t);
                throw new RuntimeException(t);
            }

            }
                // Create a new driver object - ios
            else if (strRemoteAddress.contains("local-ios"))
                driver = new TesnetMobileDriver(new IOSDriver(capabilities));
                // Appium generic
            else
                driver = new TesnetMobileDriver(new AppiumDriver(capabilities));
        }
        else{
            if (capabilities.getCapability("platformName").toString().equalsIgnoreCase(Platform.ANDROID.toString()))
                driver = new TesnetMobileDriver(new AndroidDriver(remoteAddress, capabilities));
            else if (capabilities.getCapability("platformName").toString().equalsIgnoreCase(Platform.IOS.toString()))
                driver = new TesnetMobileDriver(new IOSDriver(remoteAddress, capabilities));
            else
                driver = new TesnetMobileDriver(new AppiumDriver(remoteAddress, capabilities));
        }


        WebDriverInstanceManager.addDriverToMap(driver);
    }



    /**
     * This method initiates driver according to browser type
     * (In future, need to move to DriverFactory)
     */

    public void initBrowser() {

        WebDriver driver = null;
        String browserType = System.getProperty("browser");
        if (browserType == null) {
            browserType = "chrome";
        }

        String idleTimeOut = System.getProperty("idleTimeOut");
        if (idleTimeOut == null)
            idleTimeOut = new Property().getProperty("webdriver_idle_timeout");

        //if gridHubURL is provided then run with RemoteWebDriver else run on local desktop
        String gridHubURL = System.getProperty("hubURL");
        if(gridHubURL == null || gridHubURL.equals("NA")) {
            gridHubURL = "no-grid";
        }

        //get the project name from path, for example: D:\Workspace\customer_area_new
        String projectPath = System.getProperty("user.dir");
        File file = new File(projectPath);
        String projectName = file.getName();

        reportAndLog("Project name: " + projectName, Enumerations.MessageLevel.INFO);

        switch (browserType.toLowerCase()) {
            case "firefox":
                /*DesiredCapabilities capabilities = new DesiredCapabilities();
                capabilities.setPlatform(Platform.ANY);
                capabilities.setBrowserName("firefox");
                capabilities.setCapability("idleTimeout", idleTimeOut);
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addPreference("plugins.always_open_pdf_externally", true);
                firefoxOptions.addPreference("profile.default_content_settings.popups", 0);
                firefoxOptions.addPreference("download.prompt_for_download", false);
                firefoxOptions.addPreference("safebrowsing.enabled", true);
                firefoxOptions.merge(capabilities);
                WebDriverManager.firefoxdriver().setup();
                if (gridHubURL.equalsIgnoreCase("no-grid"))
                    driver = new TesnetWebDriver(firefoxOptions);
                else {
                     try {
                         driver = initGrid(gridHubURL, firefoxOptions);
                     }
                     catch(Throwable t){
                         MiscellaneousUtils.reportAndLog("Error instantiating remote TesnetWebDriver " + t , MessageLevel.ERROR);
                         throw new RuntimeException(t);
                     }
                }*/
                logger.info("Unsupported Driver");

                break;
            case "chrome":
                ChromeOptions chromeOptions = new ChromeOptions();
                HashMap<String, Object> chromeOptionsMap = new HashMap<String, Object>();
                chromeOptionsMap.put("plugins.plugins_disabled", new String[]{"Chrome PDF Viewer"});
                chromeOptionsMap.put("plugins.always_open_pdf_externally", true);
                chromeOptionsMap.put("profile.default_content_settings.popups", 0);
                chromeOptionsMap.put("download.prompt_for_download", false);
                chromeOptionsMap.put("safebrowsing.enabled", true);
                chromeOptionsMap.put("download.default_directory", CommonConstants.EnvironmentParams.DOWNLOADS_FOLDER);
                chromeOptions.setExperimentalOption("prefs", chromeOptionsMap);
                chromeOptions.addArguments("unlimited-storage");
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage"); // temporarily disabled
                chromeOptions.addArguments("ignore-certificate-errors");
                chromeOptions.addArguments("--window-size=1920,1080");

                if(System.getProperty("BuildID")!=null && projectName.equalsIgnoreCase("allocation")) {
                    reportAndLog("Running headless", Enumerations.MessageLevel.INFO);
                    chromeOptions.addArguments("--headless"); // Run in headless mode options.addArguments("--disable-gpu");
                }

                boolean loadWithCash = loadChromeWithCash();
                if(loadWithCash){
                    chromeOptions.addArguments("user-data-dir=" + System.getProperty("user.home").replace("\\", "/") + "/AppData/Local/Google/Chrome/User Data");
                }

                //for cases where extension is not implemented in Base test we want to regard null as false.
                boolean loadWithExtendedLogs = Boolean.TRUE.equals(StoreManager.getStore(StoreType.LOCAL_THREAD).getValueFromStore("isFullLogging"));
                if (loadWithExtendedLogs){
                    // enable Network logging
                    LoggingPreferences logPrefs = new LoggingPreferences();
                    logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
                    chromeOptions.setCapability("goog:loggingPrefs", logPrefs);
                }

                DesiredCapabilities cap = new DesiredCapabilities();
                cap.setCapability(ChromeOptions.CAPABILITY, chromeOptionsMap);
                cap.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
                cap.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
                cap.setCapability("idleTimeout", idleTimeOut);

                logger.info("idle timeout is "+idleTimeOut);
                chromeOptions.addArguments("--remote-allow-origins=*");
                chromeOptions.merge(cap);
                logger.info("Chrome capabilities are: " + cap);
                WebDriverManager.chromedriver().setup();
                if (gridHubURL.equalsIgnoreCase("no-grid")) {
                    try {
                        driver = new TesnetWebDriver(chromeOptions);
                    }
                 catch(Throwable t){
                     reportAndLog("Error instantiating local TesnetWebDriver " + t , Enumerations.MessageLevel.ERROR);
                    throw new RuntimeException(t);
                     }
                }
                else {
                    try {
                        driver = initGrid(gridHubURL, chromeOptions);
                    }
                    catch(Throwable t){
                        reportAndLog("Error instantiating remote TesnetWebDriver " + t , Enumerations.MessageLevel.ERROR);
                        throw new RuntimeException(t);
                    }
                }
                break;
            case "edge":
                EdgeOptions edgeOptions = new EdgeOptions();

                cap = new DesiredCapabilities();
                cap.setCapability(EdgeOptions.CAPABILITY, edgeOptions);
                cap.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
                cap.setCapability("idleTimeout", idleTimeOut);
                cap.setCapability("newCommandTimeout", idleTimeOut);
                cap.setCapability("newSessionWaitTimeout", idleTimeOut);

                logger.info("idle timeout is "+idleTimeOut);
                edgeOptions.setAcceptInsecureCerts(true);
                edgeOptions.addArguments("--remote-allow-origins=*");
                edgeOptions.addArguments("--no-sandbox");

                edgeOptions.merge(cap);

                if(System.getProperty("BuildID")!=null && projectName.equalsIgnoreCase("allocation")) {

                    reportAndLog("Running headless", Enumerations.MessageLevel.INFO);

                    edgeOptions.addArguments("--headless");
                    edgeOptions.addArguments("--window-size=1920,1080");
                }

                HashMap<String, Object> edgeOptionsMap = new HashMap<String, Object>();
                edgeOptionsMap.put("download.prompt_for_download", false);
                edgeOptionsMap.put("download.default_directory", CommonConstants.EnvironmentParams.DOWNLOADS_FOLDER);
                edgeOptions.setExperimentalOption("prefs", edgeOptionsMap);

                WebDriverManager.edgedriver().setup();
                if (gridHubURL.equalsIgnoreCase("no-grid")) {
                    try {
                        driver = new TesnetWebDriver(edgeOptions);
                    }
                    catch(Throwable t){
                        reportAndLog("Error instantiating local TesnetWebDriver " + t, Enumerations.MessageLevel.ERROR);
                        throw new RuntimeException(t);
                    }
                }
                else {
                    try {
                        driver = initGrid(gridHubURL, edgeOptions);
                        }
                    catch(Throwable t){
                        reportAndLog("Error instantiating remote TesnetWebDriver " + t, Enumerations.MessageLevel.ERROR);
                        throw new RuntimeException(t);
                    }
                }

                break;
            default:
                throw new Error(browserType + " is not supported");
        }

        // Add driver to driver map
        Managers.WebDriverInstanceManager.addDriverToMap(driver);

        if(projectName.equalsIgnoreCase("allocation")) {
             reportAndLog("Set windows dimensions", Enumerations.MessageLevel.INFO);
             Dimension dimensions = new Dimension(1920, 1080);
             driver.manage().window().setSize(dimensions);
        }
    }


    /**
     * Initialize the driver according to Browser type and Grid
     *
     *
     * @author - Tzvika.Sela
     * @since - 18.07.2021
     * @param gridHubURL the URL to the Selenium GRID HUB
     * @param options Capabilities of driver
     * @return WebDriver an initialized driver
     *
     */
    private WebDriver initGrid(String gridHubURL, MutableCapabilities options) {


        //if grid then look for a Grid hub to connect to

            URL url = null;
            try {
                logger.info("Starting in GRID mode");
                url = new URL(gridHubURL);
            } catch (MalformedURLException e) {
                logger.error(Arrays.toString(e.getStackTrace()));
                throw new Error("Failed to get proper GRID HUB URL");
            }



            WebDriver driver = new TesnetWebDriver(new RemoteWebDriver(url, options));



            return driver;
    }






    /**
     * This method initializes Explorer driver
     * (In future, need to move to DriverFactory)
     * //TODO check if can remove this func
     */
    public WebDriver initIEDriver() {
        WebDriverManager.iedriver().setup();
        return new InternetExplorerDriver();

    }

    /**
     * Verifies a capability set has a minimum of mimimum requirements, as assigned on class level
     * MINIMUM_CAPS_ANDROID and MINIMUM_CAPS_IOS
     *
     * @param capabilities Capabilitiies to be examined
     * @return true - capabilities meets minimum requirements, false - otherwise
     */
    private boolean meetsMinimum(Capabilities capabilities) {

        // validations
        if (capabilities == null)
            return false;

        if (!capabilities.getCapability("platformName").toString().equalsIgnoreCase(Platform.ANDROID.toString()) &&
                !capabilities.getCapability("platformName").toString().equalsIgnoreCase(Platform.IOS.toString()))
            return false;

        // Set compareable objects
        Set<String> capabilitiesList = capabilities.getCapabilityNames();
        Set<String> minimumCapsList = null; // nulll to satisfy compiler

        if (capabilities.getCapability("platformName").toString().equalsIgnoreCase(Platform.ANDROID.toString()))
            minimumCapsList = new HashSet<>(Arrays.asList(this.MINIMUM_CAPS_ANDROID));
        else if (capabilities.getCapability("platformName").toString().equalsIgnoreCase(Platform.IOS.toString())) {
            minimumCapsList = new HashSet<>(Arrays.asList(this.MINIMUM_CAPS_IOS));
        }


        return capabilitiesList.containsAll(minimumCapsList);
    }

    /**
     * check if need to load chrome driver with cash, if yes close all open chrome.exe
     * @return true if need to load with cash, otherwise false
     * @author Yael Rozenfeld
     * @since 5.4.2022
     */
    public static boolean loadChromeWithCash() {
        String loadWithCash = System.getProperty("loadWithCash");

        //loadWithCash default is false
        if (loadWithCash == null) {
            String propPath = "./src/main/resources/static_params.properties";
            Property prop = new Property(propPath);
            loadWithCash = prop.getProperty("loadWithCash");
            if (loadWithCash == null || loadWithCash.isEmpty())
                loadWithCash = "false";
        }

        //load chrome with cash
        if (loadWithCash.equalsIgnoreCase("true")) {
            logger.info("#################### load with cash ######################");
            //kill all open chrome.exe - due to use of default chrome user dir
            try {
                ExecuteRunTimeCmd.BrowserKiller(CHROME);
            } catch (IOException ioException) {
                logger.warn("opened chrome browsers were not killed. load chrome with cash may fail" + ioException.getMessage());
            }
            return true;
        }
        return false;
    }


}

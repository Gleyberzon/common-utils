
import drivers.TesnetWebDriver;
import org.openqa.selenium.chrome.ChromeOptions;


import static systemUtils.SystemCommonUtils.sleep;

public class Run_selenium_feature {
    public static void main(String[] args) {
        ChromeOptions options = new ChromeOptions();
        TesnetWebDriver tesnetWebDriver = new TesnetWebDriver(options);

        tesnetWebDriver.get("https://www.google.com");
        sleep(3000);
        tesnetWebDriver.close();
        tesnetWebDriver.quit();
    }
}

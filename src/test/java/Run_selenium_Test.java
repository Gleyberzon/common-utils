
import drivers.TesnetWebDriver;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;


import static systemUtils.SystemCommonUtils.sleep;

public class Run_selenium_Test {

    @Test
    public void main_Test() {
        ChromeOptions options = new ChromeOptions();
        TesnetWebDriver tesnetWebDriver = new TesnetWebDriver(options);

        tesnetWebDriver.get("https://www.google.com");
        sleep(3000);
        tesnetWebDriver.close();
        tesnetWebDriver.quit();
    }
}

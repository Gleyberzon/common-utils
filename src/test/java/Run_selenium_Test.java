
import drivers.TesnetWebDriver;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeOptions;


import static systemUtils.SystemCommonUtils.sleep;

public class Run_selenium_Test {

    @Test
    public void main_Test() {
        ChromeOptions options = new ChromeOptions();
        TesnetWebDriver tesnetWebDriver = new TesnetWebDriver(options);

        tesnetWebDriver.get("https://www.google.com");
        sleep(2000);
        tesnetWebDriver.findElement(By.name("q")).sendKeys("N12");
        tesnetWebDriver.close();
        tesnetWebDriver.quit();
    }
}

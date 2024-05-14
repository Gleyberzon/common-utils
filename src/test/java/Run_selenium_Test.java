
import drivers.TesnetWebDriver;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;


import static systemUtils.SystemCommonUtils.sleep;

public class Run_selenium_Test {

    @Test
    public void main_Test() {
        ChromeOptions options = new ChromeOptions();
        TesnetWebDriver tesnetWebDriver = new TesnetWebDriver(options);

        tesnetWebDriver.get("https://www.google.com");
        sleep(2000);
        WebElement searchBox = tesnetWebDriver.findElement(By.name("q"));
        searchBox.sendKeys("N12");
        sleep(2000);
        searchBox.sendKeys(Keys.RETURN);
        sleep(2000);
        WebElement firstResult = tesnetWebDriver.findElement(By.xpath("(//span[contains(text(),' | N12')])[1]/../.."));
        String href = firstResult.getAttribute("href");
        sleep(2000);
        tesnetWebDriver.get(href);
        sleep(2000);
        tesnetWebDriver.close();
        tesnetWebDriver.quit();
    }
}

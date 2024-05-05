package seleniumUtils;

import drivers.TesnetMobileDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static propertyUtils.PropertyUtils.getGlobalProperty;

/**
 * Class is used for defining parameters and methods that are relevant for all pages in all projects
 *
 */
public class BasePage<T extends WebDriver> {

    protected T driver;
    protected int timeout;
    protected Duration timeoutDuration;
    private final Logger logger = LoggerFactory.getLogger(BasePage.class);

    public BasePage(T driver) {
        this.driver = driver;
        this.timeout = Integer.parseInt(getGlobalProperty("timeout"));
        this.timeoutDuration = Duration.ofSeconds(timeout);
        if (this.driver instanceof TesnetMobileDriver)
            // TODO: this is a bug. Currently cannot activate AppiumFieldDecorator.
            //PageFactory.initElements(new AppiumFieldDecorator(driver),this);
            PageFactory.initElements(driver,this);

        else
            PageFactory.initElements(driver,this);

    }

    /**
     * Refresh the page
     * @author genosar.dafna
     * @since 02.11.2022
     */
    public void refresh(){
        driver.navigate().refresh();
    }

    /**
     * This is a default implementation of isOnPage.
     * It receives one or more WebElements and checks if they are present
     *
     * @param elements One or more WebElements to check for presence
     * @return true- all elements specified were present, false - otherwise
     * @author Nir.Gallner
     * @modified 16.05.2021
     * @modifiedBy Zvika.Sela
     */
    protected boolean isOnPage(WebElement... elements) {
        try {
            WebDriverWait Waits = new WebDriverWait(driver, timeoutDuration);
            Waits.until(new ExpectedCondition<List<WebElement>>() {
                @Override
                public @Nullable
                List<WebElement> apply(WebDriver d) {
                    for (WebElement webElement : elements) {
                        if (webElement.getLocation() == null)
                            return null;
                    }
                    return elements.length > 0 ? Arrays.asList(elements) : null;
                }
            });
            logger.info("elements " + Arrays.toString(elements) + "were present on page");
            return true;
        } catch (Exception e) {
            logger.info("elements " + Arrays.toString(elements) + "weren't present on page");
            return false;
        }
    }



    /**
     *
     * @param elem element type: combo box
     * @param index index number in cmb list
     * @author jan.naor
     * @since 23.05.21
     */
    public void selectDropDownByIndex(WebElement elem, int index)
    {
        Select myValue=new Select(elem);
        myValue.selectByIndex(index);
    }

    /**
     *
     * @param elem element type: combo box
     * @param value value to select from cmb list
     * @author jan.naor
     * @since 23.05.21
     */
    public void selectDropDownByValue(WebElement elem, String value)
    {
        Select myValue=new Select(elem);
        myValue.selectByValue(value);
    }



}


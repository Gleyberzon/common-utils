package seleniumUtils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ActionsWrapper
{
    private static Logger logger = LoggerFactory.getLogger(ActionsWrapper.class);

    /**
     * Method to press on objects in web page (usually will be used when click fails)
     * @param driver Webdriver instance
     * @param elem Element to press on
     */
    public static void press(WebDriver driver, WebElement elem)
    {
            Actions action = new Actions(driver);
            action.moveToElement(elem).click().build().perform();
            logger.info("Action of press on "+elem+" succeeded");
    }

    /**
     * Hover over an element
     * @param driver WebDriver object
     * @param element the element to hover over
     * @author genosar.dafna
     * @since 18.07.2022
     */
    public static void mouseOver(WebDriver driver, WebElement element)
    {
        Actions action = new Actions(driver);
        action.moveToElement(element).build().perform();
    }

    /**
     * Hover over an element and click on it
     * @param driver WebDriver object
     * @param element the element to hover and click on
     * @author genosar.dafna
     * @since 18.07.2022
     */
    public static void mouseOverAndClick(WebDriver driver, WebElement element)
    {
        Actions action = new Actions(driver);
        action.moveToElement(element).click().build().perform();
    }

    /**
     * Double-click on an element
     * @param driver WebDriver object
     * @param element the element to double-click on
     * @author genosar.dafna
     * @since 02.08.2022
     */
    public static void doubleClick(WebDriver driver, WebElement element)
    {
        Actions action = new Actions(driver);
        action.doubleClick(element).perform();
    }
}

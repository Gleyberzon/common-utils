package waitUtils;




import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import seleniumUtils.ElementWrapper;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class WaitWrapper
{
    private static Logger logger = LoggerFactory.getLogger(WaitWrapper.class);
    /**
     * wait for the element to disappear
     *
     * @param elem - element that we wait to disappear
     * @param timeout - timeout in seconds
     * @author Nir.Gallnar
     * @since 19.05.2021
     * @return True if the elem has disappeared after <timeout> seconds, else false
     */
    public static boolean waitForElementToDisappear(WebElement elem, int timeout) {
        boolean result = false;
        for (int i = 0; i < timeout; i++) {

            try {
                elem.isDisplayed();
                Thread.sleep(1000);
            } catch (Throwable e) {
                result = true;
                break;
            }
        }
        //if we are here than result is set

        return result;

    }

    /**
     * Waiting for element to appear on page
     * @param driver WebDriver instance
     * @param element WebElement to appear
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return WebElement we waited for
     */
    public static WebElement waitForElementToAppear(WebDriver driver, WebElement element, int timeoutInSeconds)
    {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Waiting for WebElement to be invisible
     * @param driver WebDriver instance
     * @param by -find by for the element
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return true/false for success
     * @author genosar.dafna
     * @since 29.06.2022
     */
    public static boolean waitForElementToDisappear(WebDriver driver, By by, int timeoutInSeconds)
    {
        try {
            return (new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds))).until(ExpectedConditions.invisibilityOfElementLocated(by));
        }
        catch (NoSuchElementException e)
        {
            return true;
        }
    }

    /**
     * Waiting for element to disappear from page
     * @param driver WebDriver instance
     * @param element WebElement to disappear
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return boolean (true if element disappeared, false otherwise).
     * @modifier genosar.dafna
     * @modified 29.06.2022
     */
    public static boolean waitForElementToDisappear(WebDriver driver, WebElement element, int timeoutInSeconds)
    {
        try {
            if(!ElementWrapper.elementExistsAndDisplayed(element))
                return true;

            return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.invisibilityOf(element));
        }
        catch (NoSuchElementException e)
        {
            return true;
        }
        catch (TimeoutException e)
        {
            return !ElementWrapper.elementExistsAndDisplayed(element);
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    /**
     * Waiting for all WebElements in a list to appear on page
     * @param driver WebDriver instance
     * @param elementsList List of WebElements to appear
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return List of WebElements we waited for
     */
    public static List<WebElement> waitForAllElementsToAppear(WebDriver driver, List<WebElement> elementsList, int timeoutInSeconds)
    {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.visibilityOfAllElements(elementsList));
    }

    /**
     * Waiting for WebElement to be clickable
     * @param driver WebDriver instance
     * @param element WebElement to be clickable
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return WebElement we waited for
     */
    public static WebElement waitForElementTobeClickable(WebDriver driver, WebElement element, int timeoutInSeconds)
    {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Waiting for page title to be displayed, then matching it with the given param
     * Might not work when executing headless browser
     * @param driver WebDriver instance
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @param pageTitle The exact page title string as presented in the <title></title> tag
     * @return boolean value indicates if the title displayed\matches or not
     */
    public static boolean waitForPageTitleToBeDisplayed(WebDriver driver, int timeoutInSeconds, String pageTitle) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.titleIs(pageTitle));
    }

    /**
     * Waiting for page title to contains the given param
     * Might not work when executing headless browser
     * @param driver WebDriver instance
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @param pageTitle The page title string or part of it as presented in the <title></title> tag
     * @return boolean value indicates if the title displayed\matches or not
     * @author genosar.dafna
     * @since 29.06.2022
     */
    public static boolean waitForPageTitleToContain(WebDriver driver, String pageTitle, int timeoutInSeconds) {
        logger.info(String.format("Waiting for page title to contain '%s'", pageTitle));
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.titleContains(pageTitle));
        }
        catch (Exception e)
        {
            throw new Error(String.format("Waiting for page title to be '%s' failed. Error: %s", pageTitle, e.getMessage()));
        }
    }

    /**
     * Waiting for element to found by By object on page
     * @param driver WebDriver instance
     * @param by by to be found
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return WebElement we waited for
     */
    public static WebElement waitForVisibilityOfElementLocated(WebDriver driver, By by, int timeoutInSeconds)
    {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    /**
     * Waiting for element to found under another element by a locator
     * @param driver WebDriver instance
     * @param rootElement element to search under
     * @param by by to be found
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return WebElement we waited for
     * @author genosar.dafna
     * @since 18.07.2022
     */
    public static WebElement waitForVisibilityOfElementLocated(WebDriver driver, WebElement rootElement, By by, int timeoutInSeconds)
    {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(visibilityOfElementLocated(rootElement, by));
    }

    /**
     * Waiting for all elements to be found under another element by a locator
     * @param driver WebDriver instance
     * @param rootElement element to search under
     * @param by by to be found
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return WebElements we waited for
     * @author genosar.dafna
     * @since 25.07.2022
     */
    public static List<WebElement> waitForVisibilityOfAllElementsLocatedBy(WebDriver driver, WebElement rootElement, By by, int timeoutInSeconds)
    {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(visibilityOfAllElementsLocatedBy(rootElement, by));
    }

    /**
     * Wait for a specific frame and switch to it
     * @param driver WebDriver instance
     * @param frameName the frame name/id
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return the web driver instance after switching to the frame
     * @author genosar.dafna
     * @since 24.07.2022
     */
    public static WebDriver waitForFrameToBeAvailableAndSwitchToIt(WebDriver driver, String frameName, int timeoutInSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameName));
    }

    /**
     * Support private method to wait and search an element under another element
     * @param rootElement the root element to search from
     * @param locator By locator to search with
     * @return a WebElement if was found or null otherwise
     * @author genosar.dafna
     * @since 18.07.2022
     */
    private static ExpectedCondition<WebElement> visibilityOfElementLocated(WebElement rootElement, final By locator) {
        return new ExpectedCondition<WebElement>() {
            public WebElement apply(WebDriver driver) {
                try {
                    WebElement webElement = rootElement.findElement(locator);
                    return webElement.isDisplayed() ? webElement : null;
                } catch (StaleElementReferenceException var3) {
                    return null;
                }
            }
            public String toString() {
                return "visibility of element located by " + locator;
            }
        };
    }

    /**
     * Support private method to wait and search all elements under another element
     * @param rootElement the root element to search from
     * @param locator By locator to search with
     * @return all WebElements if was found or null otherwise
     * @author genosar.dafna
     * @since 25.07.2022
     */
    private static ExpectedCondition<List<WebElement>> visibilityOfAllElementsLocatedBy(WebElement rootElement, final By locator) {
        return new ExpectedCondition<List<WebElement>>() {
            public List<WebElement> apply(WebDriver driver) {
                List<WebElement> elements = rootElement.findElements(locator);
                Iterator var3 = elements.iterator();

                WebElement element;
                do {
                    if (!var3.hasNext()) {
                        return elements.size() > 0 ? elements : null;
                    }

                    element = (WebElement)var3.next();
                } while(element.isDisplayed());

                return null;
            }

            public String toString() {
                return "visibility of all elements located by " + locator;
            }
        };
    }

    /**
     * Waiting for all WebElements found on page by By object
     * @param driver WebDriver instance
     * @param by by to be found
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return List of WebElements we waited for
     */
    public static List<WebElement> waitForVisibilityOfAllElementsLocatedBy(WebDriver driver, By by, int timeoutInSeconds)
    {   return new  WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));

    }

    /**
     * Waiting for WebElement to be clickable
     * @param driver WebDriver instance
     * @param by -find by to be clickable
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return WebElement we waited for
     */
    public static WebElement waitForElementTobeClickable(WebDriver driver, By by, int timeoutInSeconds)
    {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.elementToBeClickable(by));
    }

    /**
     * Waiting for WebElement to be disabled
     * @param driver WebDriver instance
     * @param by -find by for the element
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return true/false for success
     * @author genosar.dafna
     * @since 29.06.2022
     */
    public static boolean waitForElementToBeDisabled(WebDriver driver, By by, int timeoutInSeconds)
    {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.not(ExpectedConditions.elementToBeClickable(by)));
    }

    /**
     * Waiting for WebElement to be disabled
     * @param driver WebDriver instance
     * @param element - the element
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return true/false for success
     * @author genosar.dafna
     * @since 29.06.2022
     */
    public static boolean waitForElementToBeDisabled(WebDriver driver, WebElement element, int timeoutInSeconds)
    {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.not(ExpectedConditions.elementToBeClickable(element)));
    }

    /**
     * Waiting for WebElement to be enabled
     * @param driver WebDriver instance
     * @param by -find by for the element
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return true/false for success
     * @author genosar.dafna
     * @since 29.06.2022
     */
    public static boolean waitForElementToBeEnabled(WebDriver driver, By by, int timeoutInSeconds)
    {
        WebElement element = (WebElement)ExpectedConditions.visibilityOfElementLocated(by).apply(driver);
        return waitForElementToBeEnabled(driver, element, timeoutInSeconds);
    }

    /**
     * Waiting for WebElement to be enabled
     * @param driver WebDriver instance
     * @param element - the element
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return true/false for success
     * @author genosar.dafna
     * @since 29.06.2022
     */
    public static boolean waitForElementToBeEnabled(WebDriver driver, WebElement element, int timeoutInSeconds)
    {
        return (new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds))).until(d -> element.isEnabled());
    }


    /**
     * Checking if element is displayed or not
     * @param driver WebDriver instance
     * @param element WebElement to appear
     * @param timeout Maximum number of seconds to wait
     * @return boolean (true if element appeared, false otherwise).
     */
    public static boolean isElementDisplayed(WebDriver driver,WebElement element, int timeout) {
        try {
            //driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);
            waitForElementToAppear(driver,element, timeout).isDisplayed();
            return true;
        } catch (Exception e) {
            if (element == null) {
                logger.info("Element does not exist in DOM");
            }
            else logger.info("Element:" + element.toString() + "is not displayed");
            return false;
        }
        finally {
            // driver.manage().timeouts().implicitlyWait(MiscellaneousUtils.getGlobalPropertyEntity().getIntProperty("timeout"), TimeUnit.SECONDS);
        }
    }

    /**
     * Wait for the number of window handles to be as expected
     * @param driver WebDriver instance
     * @param expectedNumOfWindowHandles optional expected number of window handles. If null - will expect 2
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return true/false if the number of windows is as expected
     * @author genosar.dafna
     * @since 28.07.2022
     */
    public static boolean waitForNumberOfWindowHandlesToBe(WebDriver driver, @Nullable Integer expectedNumOfWindowHandles, int timeoutInSeconds){

        expectedNumOfWindowHandles = (expectedNumOfWindowHandles == null)? 2 : expectedNumOfWindowHandles;
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.numberOfWindowsToBe(expectedNumOfWindowHandles));
    }

    /**
     * Wait for a specific window handle and switch to it
     * @param driver WebDriver instance
     * @param expectedWindowTitle the window handle title
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return the web driver instance after switching to the new window handle
     * @author genosar.dafna
     * @since 28.07.2022
     */
    public static WebDriver waitForWindowHandleAndSwitchToIt(WebDriver driver, String expectedWindowTitle, int timeoutInSeconds)
    {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));

        return wait.until(
                new ExpectedCondition<WebDriver>() {
                    public WebDriver apply(WebDriver driver) {
                        try {
                            //Get the initial window handle
                            String initialWindowHandle = driver.getWindowHandle();

                            //Get all opened window handles
                            Set<String> windowHandles = driver.getWindowHandles();

                            for (String handle : windowHandles)
                            {
                                //Switch to the next window handle
                                driver.switchTo().window(handle);

                                String title = driver.getTitle();
                                if(driver.getTitle().equals(expectedWindowTitle))
                                {
                                    return driver;
                                }
                            }
                            driver.switchTo().window(initialWindowHandle);

                            throw new Error(String.format("Window handle '%s' could not be found after %d seconds", expectedWindowTitle, timeoutInSeconds));
                        }
                        catch (Exception var3) {
                            throw new Error(String.format("Cannot switch to Window handle '%s'. Error: %s", expectedWindowTitle, var3.getMessage()));
                        }
                    }
                    public String toString() {
                        return "Window to be available: " + expectedWindowTitle;
                    }
                });
    }

    /**
     * Checking if element is displayed or not
     * @param driver WebDriver instance
     * @param by WebElement locator
     * @param timeout Maximum number of seconds to wait
     * @return boolean (true if element appeared, false otherwise).
     */
    public static boolean isElementDisplayed(WebDriver driver,By by, int timeout) {

        try {
            //driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);
            waitForVisibilityOfElementLocated(driver,by,timeout);
            return true;
        } catch (Exception e) {
            logger.info("Element:" + by.toString() + "is not displayed");
            return false;
        }
        finally {
            //driver.manage().timeouts().implicitlyWait(MiscellaneousUtils.getGlobalPropertyEntity().getIntProperty("timeout"), TimeUnit.SECONDS);
        }

    }



    /**
     * Waiting for <timeout> seconds till the value attribute changes from <originalValue> to any different value.
     *
     * @author Sela.Tzvika
     * @since 27.05.2021
     * @param driver WebDriver instance
     * @param webElement WebElement
     * @param timeout Maximum number of seconds to wait
     * @return String (null if element value attribute did not change, returns new value otherwise).
     */
    public static String waitForWebElementValueAttributeToUpdate(WebDriver driver, WebElement webElement,int timeout,String originalValue){
        WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(timeout));


        String value = wait.until(new ExpectedCondition<String>() {
            public String apply(WebDriver driver) {
                String value = webElement.getAttribute("value");
                if (value==null || value.equals(originalValue))
                    return null;
                return value;
            }
        });
        return value;

    }


    /**
     * wait until count of element with specific xpath changes to numOfElements
     *
     * @param driver
     * @param by - locator of the element
     * @param timeout - time to wait for the element count to be numOfElements
     * @param numOfElements - num of elements to wait for
     * @return true - if count of element changes to numOfElements, else false
     * */
    public static boolean waitUntilCountChanges(WebDriver driver,By by, int timeout, int numOfElements) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        return wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                int elementCount = driver.findElements(by).size();
                if (elementCount == numOfElements)
                    return true;
                else
                    return false;
            }
        });
    }

    /** wait till number of elements in a list is greater than a given value number
     *
     * @param driver - web-driver
     * @param elements - list of the elements
     * @param timeout - time to wait till the number of elements are greater than the given value
     * @param numOfElements - the given number to wait till the number of elements are greater then
     * @return True if the number of elements in the list is greater than the numOfElements, else false
     *
     * @author umflat.lior
     * @since 17.8.2022
     */
    public static boolean waitUntilNumberOfElementsGreaterThan(WebDriver driver, final List<WebElement> elements, int timeout, final int numOfElements) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        return (Boolean)wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                int elementCount = elements.size();
                return elementCount > numOfElements ? true : false;
            }
        });
    }

    /**
     * Wait until element's attribute is as desired
     * @param driver the driver
     * @param element the element
     * @param attribute the name of the attribute
     * @param value the desired value of attribute
     * @param timeoutInSeconds timeout to wait
     * @return returns true/false if the attribute is as desired
     * @author Dafna Genosar
     * @since 1.12.2021
     */
    public static Boolean waitForAttributeToBe(WebDriver driver, WebElement element, String attribute, String value, int timeoutInSeconds)
    {
        logger.info(String.format("Waiting for attribute '%s' to be '%s'", attribute, value));
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.attributeToBe(element, attribute, value));
        }
        catch (Exception e)
        {
            throw new Error(String.format("Waiting for attribute '%s' to be '%s' failed. Error: %s", attribute, value, e.getMessage()));
        }
    }

    /**
     * Wait until element's attribute contains the word received
     * @param driver the driver
     * @param element the element
     * @param attribute the name of the attribute
     * @param value the desired attribute value or part of value
     * @param timeoutInSeconds timeout to wait
     * @return returns true/false if the attribute value contains the desired value
     * @author Dafna Genosar
     * @since 1.12.2021
     */
    public static Boolean waitForAttributeToContains(WebDriver driver, WebElement element, String attribute, String value, int timeoutInSeconds)
    {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.attributeContains(element, attribute, value));
    }

    /**
     * Wait until the text is present in the element
     * @param driver the driver
     * @param element the element
     * @param text the expected text
     * @param timeoutInSeconds timeout to wait
     * @return returns true/false if the text is present in the element
     * @author Dafna Genosar
     * @since 13.02.2022
     */
    public static Boolean waitForTextToBePresentInElement(WebDriver driver, WebElement element, String text, int timeoutInSeconds)
    {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.textToBePresentInElement(element, text));
    }

    /**
     * Wait until the text is NOT present in the element
     * @param driver the driver
     * @param element the element
     * @param text the text
     * @param timeoutInSeconds timeout to wait
     * @return returns true if the text is not present in the element / flase if is it present
     * @author Dafna Genosar
     * @since 26.07.2022
     */
    public static Boolean waitForTextNotToBePresentInElement(WebDriver driver, WebElement element, String text, int timeoutInSeconds)
    {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(element, text)));
    }

    /**
     * Wait until the element has no text inside it
     * @param driver the driver
     * @param element the element
     * @param timeoutInSeconds timeout to wait
     * @return returns true if no text is present in the element / false if text is present
     * @author Dafna Genosar
     * @since 26.07.2022
     */
    public static Boolean waitForElementToHaveNoText(WebDriver driver, WebElement element, int timeoutInSeconds)
    {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));

        Boolean value = wait.until(new ExpectedCondition<Boolean>()
        {
            public Boolean apply(WebDriver driver)
            {
                try {
                    return element.getText() == null || element.getText().equals("");
                }
                catch (StaleElementReferenceException var3) {
                    return null;
                }
            }

            public String toString() {
                return String.format("No text to be present in element ", element);
            }
        });
        return value;
    }

    /**
     * Wait until the text not contains in url.
     * @param text The text that should not be included in the url
     * @param driver the driver
     * @param timeout -
     * @return returns true if text does not present in url,otherwise false
     * @author Yael.Rozenfeld
     * @since 03.04.2022
     */
    public static boolean waitUntilUrlDoesNotContain(String text,WebDriver  driver, int timeout)  {
        return (new WebDriverWait(driver, Duration.ofSeconds(timeout)).until(ExpectedConditions.not(ExpectedConditions.urlContains(text))));

    }
    /**
     * Wait until the text not contains in url.
     * @param text -The text that should be included in the url
     * @param driver the driver
     * @param timeout
     * @return returns true if text presents in url,otherwise false
     * @author Yael.Rozenfeld
     * @since 03.04.2022
     */
    public static boolean waitUntilUrlContains(String text,WebDriver  driver, int timeout){
        return (new WebDriverWait(driver, Duration.ofSeconds(timeout))).until(ExpectedConditions.urlContains(text));
    }

    /** Wait until new tab is opened and switch to it
     *
     * @param driver instance of WebDriver
     * @param expectedTabNumber expected tab number to wait for.
     *                             for example 3 - > wait till tab 3 will be open
     * @author umflat.lior
     * @since 19.1.2023
     */
    public static void waitForNewTabToOpenAndSwitchToIt(WebDriver driver, int expectedTabNumber) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Wait for the tab with index expectedTabNumber to be opened
        wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return driver.getWindowHandles().size() == expectedTabNumber;
            }
        });

        // Switch to the tab with index expectedTabNumber
        ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(expectedTabNumber-1));
    }

}

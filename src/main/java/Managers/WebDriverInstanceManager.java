package Managers;

import org.openqa.selenium.WebDriver;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds all the instances of WebDriver
 * This class is a singletone. It holds a map of all the instances of WebDriver created during tests.
 * It puts the WebDriver object along with it's thread id, so the class will work properly even for
 * multi thread environment
 *
 * @author Lior Umflat
 * @since April 21
 */
public class WebDriverInstanceManager {

    private static Map<Integer, WebDriver> driverMap = new HashMap<Integer, WebDriver>();

    private WebDriverInstanceManager() {
    }

    /**
     * Add a driver to the map, with the thread id as key
     *
     * @param driver Instance of WebDriver
     */
    public static void addDriverToMap(WebDriver driver) {
        Integer threadId = (int) (Thread.currentThread().getId());
        driverMap.put(threadId, driver);
    }

    /**
     * Get the driver from the map using the thread id
     *
     * @return Instance of WebDriver
     */
    public static WebDriver getDriverFromMap() {
        Integer threadId = (int) (Thread.currentThread().getId());
        return (WebDriver) driverMap.get(threadId);
    }

    /**
     * Delete the driver from the map using the thread id
     * @author genosar.dafna
     * @since 29.06.2023
     */
    public static void deleteDriverFromMap() {

        Integer threadId = (int) Thread.currentThread().getId();
        driverMap.put(threadId, null);
    }
}



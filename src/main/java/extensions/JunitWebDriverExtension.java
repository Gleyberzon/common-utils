package extensions;

import Managers.WebDriverInstanceManager;
import drivers.WebDriverFactory;
import jdk.jfr.Category;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static propertyUtils.PropertyUtils.getGlobalProperty;

/**
 * this Junit listener starts the driver beforeEach and quits the driver afterEach
 *
 * @author - Lior Umflat
 * @since - 24.5.2021
 */
public class JunitWebDriverExtension implements AfterEachCallback, BeforeEachCallback {

    /**
     * create the driver BeforeEach
     *
     * @author - Lior Umflat
     * @since - 24.5.2021
     * @author dafna.genosar
     * @since 15.11.2022
     * @since 29.06.2022
     */
    @Override
    public void beforeEach(ExtensionContext extensionContext) {

        boolean initDriver = true;

        Category category = extensionContext.getRequiredTestClass().getAnnotation(Category.class);
        if (category != null) {
            List<String> categories = Arrays.stream(category.value()).map(s -> s.toLowerCase()).collect(Collectors.toList()) ;
            if(categories.contains("non ui")) {
                initDriver = false;
                WebDriverInstanceManager.deleteDriverFromMap();
            }
        }
        if(initDriver) {
            WebDriverFactory driverFactory = new WebDriverFactory();
            driverFactory.initDriver();
            WebDriverInstanceManager.getDriverFromMap().manage().timeouts().
                    pageLoadTimeout(Duration.ofSeconds(Integer.valueOf(getGlobalProperty("timeout_page_load"))));
        }
    }

    /**
     * quit the driver AfterEach
     *
     * @author - Lior Umflat
     * @since - 24.5.2021
     * @author dafna.genosar
     * @since 15.11.2022
     */
    @Override
    public void afterEach(ExtensionContext extensionContext) {
        boolean quitDriver = true;

        Category category = extensionContext.getRequiredTestClass().getAnnotation(Category.class);
        if (category != null) {
            List<String> categories = Arrays.stream(category.value()).map(s -> s.toLowerCase()).collect(Collectors.toList()) ;
            if(categories.contains("non ui"))
                quitDriver = false;
        }
        if(quitDriver)
            WebDriverInstanceManager.getDriverFromMap().quit();
    }
}

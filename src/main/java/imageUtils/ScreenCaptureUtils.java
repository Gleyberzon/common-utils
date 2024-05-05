package imageUtils;

import Managers.WebDriverInstanceManager;
import dateTimeUtils.DateUtils;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ScreenCaptureUtils {

    private static Logger logger = LoggerFactory.getLogger(ScreenCaptureUtils.class);

    /**
     * This method capture the screen, not the browser, meaning it take a screenshot of the focused window
     * The capture image is in .png format
     * @param filePath the desired file path
     * @param fileName the desired file name
     * @return the captured screenshot absolute file path as String
     */
    public static String getCapturedScreenImageFilePath(String filePath, String fileName) {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage capture;
        String completeFileNameAndPath = filePath + fileName + ".png";
        try {
            capture = new Robot().createScreenCapture(screenRect);
            ImageIO.write(capture, "png", new File(completeFileNameAndPath));
        } catch (AWTException | IOException e) {
            e.printStackTrace();
        }
        return completeFileNameAndPath;
    }

    /**
     * take screenshot for reporting
     * @param driver WebDriver instance
     * @return SSpath string new location of file
     */
    public static String takeScreenShot(WebDriver driver)
    {
        String SSpath = null;

        try
        {
            SSpath= "images" + File.separator +  DateUtils.getUniqueTimestamp() +".png" ;
            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(scrFile, new File("report" + File.separator + SSpath));
            logger.info(SSpath);
        }

        //if invalid session id exception exists - take driver from map
        catch (NoSuchSessionException exc) {
            try {
                SSpath= "images" + File.separator +  DateUtils.getUniqueTimestamp() +".png" ;
                File scrFile = ((TakesScreenshot) WebDriverInstanceManager.getDriverFromMap()).getScreenshotAs(OutputType.FILE);
                FileUtils.copyFile(scrFile, new File("report" + File.separator + SSpath));
                logger.info(SSpath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        catch(Exception e)
        {
            logger.error(e.toString());
        }

        return SSpath;
    }

}

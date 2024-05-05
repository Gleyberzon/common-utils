package imageUtils;

import Managers.WebDriverInstanceManager;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 *  Class that holds methods to handle images
 */
public class ImageUtils {

    private static Logger logger = LoggerFactory.getLogger(ImageUtils .class);

    /** image verification method to verify if images are the same.
     * before using this method an image of the element needs to be created once so that future comparison will work.
     *
     * example of how to create image to compare too:
     * Screenshot logoImageScreenshot = new AShot().takeScreenshot(driver, draftIcon);
     * ImageIO.write(logoImageScreenshot.getImage(),"png",new File(REPORT_IMAGE_FOLDER_PATH + "LinuxDraftIcon.png"));
     *
     * pay attention that the image you create localy is not the same that is captured when running with linux.
     * to create the linux image run the capture images lines (24-25) with the desired path using unit test.
     * save the image that was created on https://devops.corp.zim.com/selenium_unittests/l2a_uiux_new/qa0/seperated_reports/images/ to the ExpectedImagePath
     *
     * @param imageElement the element of the image
     * @param ExpectedImagePath the path of the image to compare too
     * @return true if there is a difference or false if not
     * @author umflat.lior
     * @since 1.1.2022
     */
    public static boolean verifyImageElement(WebElement imageElement, String ExpectedImagePath) throws Exception {
        BufferedImage expectedImage = null;
        try
        {
            expectedImage = ImageIO.read(new File(ExpectedImagePath));
        }
        catch (Exception e)
        {
            logger.info("Error Reading Image File: " + ExpectedImagePath);
            throw new Exception("Error Reading Image File: " + ExpectedImagePath);
        }

        Screenshot imageScreenShot = new AShot().takeScreenshot(WebDriverInstanceManager.getDriverFromMap(),imageElement);
        BufferedImage actualImage = imageScreenShot.getImage();
        ImageDiffer imageDiff = new ImageDiffer();
        ImageDiff diff = imageDiff.makeDiff(actualImage,expectedImage);
        //return true if there is a difference or false if not
        return diff.hasDiff();
    }
}

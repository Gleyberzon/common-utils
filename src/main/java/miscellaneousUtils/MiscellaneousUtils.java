package miscellaneousUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import systemUtils.SystemCommonUtils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;

public class MiscellaneousUtils
{
    private static Logger logger = LoggerFactory.getLogger(MiscellaneousUtils.class);

    /**
     * This function take care in case of window certificate
     */
    public static void Robot_CertificateWindow() {
        try {

            Robot r = new Robot();
            Thread.sleep(10000);
            r.keyPress(java.awt.event.KeyEvent.VK_TAB);

            Thread.sleep(2000);
            r.keyPress(java.awt.event.KeyEvent.VK_TAB);

            Thread.sleep(2000);
            r.keyPress(java.awt.event.KeyEvent.VK_ENTER);
            Thread.sleep(2000);

        }catch(Exception e){
            logger.error("Robot_CertificateWindow: "+ e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    /**
     * Use the Robot class to handle the file upload Windows dialog
     * @param filePath file path
     * @author tseva.yehonatan
     * @since 26.09.23
     */
    public static void Robot_uploadFileByWindowsHandler(String filePath){
        try{
            Robot robot = new Robot();

            // Enter the file path (replace with your file path)
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection stringSelection = new StringSelection(filePath);
            clipboard.setContents(stringSelection, null);

            // press Contol+V for pasting
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);

            // release Contol+V for pasting
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyRelease(KeyEvent.VK_V);

            // for pressing and releasing Enter
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);

            // Wait for a few seconds to ensure the file is uploaded
            SystemCommonUtils.sleep(2500);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

package seleniumUtils;

import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.function.Function;

public class LoopWrapper
{
    /**
     * Calling a function as sent to the method  up to 5 times
     * @param f Function to call
     * @param elem WebElement to apply the function on
     * @return true if function succeeded. false otherwise
     */
    private static Logger logger = LoggerFactory.getLogger(LoopWrapper.class);
    public static boolean avoidStaleness(Function<WebElement, Boolean> f, WebElement elem)
    {
        for (int i=0; i<5; i++)
        {
            Boolean result = f.apply(elem);
            logger.info("result = " + result);
            if (result)
            {
                return true;
            }
       }
       return false;
   }
}

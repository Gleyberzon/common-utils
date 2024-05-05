package seleniumUtils.customeElements;

import Managers.WebDriverInstanceManager;
import drivers.TesnetWebElement;
import Enumerations.MessageLevel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import waitUtils.WaitWrapper;

import javax.annotation.Nullable;

import static ReportUtils.Report.reportAndLog;

/**
 * This class supports checkboxes. Writes to the report/log when performing actions on them. Verifies if they are
 * Selected or not before clicking on them etc.
 */
public class CheckBox extends TesnetWebElement
{
    WebElement localElement;

    public CheckBox(WebElement element)
    {
        super(element);
        localElement = element;
    }

    /**
     * @return The checkbox ID
     * @author Dafna Genosar
     * @since 1.12.2021
     */
    public String getId()
    {
        return localElement.getAttribute("id");
    }

    /**
     * Get the input field (input tag) of the checkbox
     * @return the input field (input tag) of the checkbox
     * @author genosar.dafna
     * @since 06.09.2023
     */
    public WebElement getInputField(){
        if(localElement.getTagName().equalsIgnoreCase("input"))
            return localElement;

        try{
            return localElement.findElement(By.tagName("input"));
        }
        catch(Exception e){
            throw new Error("Input field cannot be found in the checkbox element");
        }
    }

    /**
     * Check the checkbox
     * @author Dafna Genosar
     * @since 1.12.2021
     * @since 06.09.2023
     */
    public void check()
    {
        check(getId());
    }

    /**
     * Check the checkbox
     * @checkboxName check box title/name
     * @author Dafna Genosar
     * @since 06.09.2023
     */
    public void check(String checkboxName)
    {
        if (!localElement.isSelected() && isEnabled())
        {
            reportAndLog(String.format("Check '%s' checkbox", checkboxName), MessageLevel.INFO);
            localElement.click();
        }
    }

    /**
     * Uncheck the checkbox
     * @author Dafna Genosar
     * @since 1.12.2021
     * @modifier Dafna Genosar
     * @modified 09.03.2022
     * @since 06.09.2023
     */
    public void uncheck()
    {
        uncheck(getId());
    }

    /**
     * Uncheck the checkbox
     * @param checkboxName checkbox name / title
     * @author Dafna Genosar
     * @since 06.09.2023
     */
    public void uncheck(String checkboxName)
    {
        if (localElement.isSelected() && isEnabled())
        {
            reportAndLog(String.format("Uncheck '%s' checkbox", checkboxName), MessageLevel.INFO);
            localElement.click();
        }
    }

    /**
     * Wait for the checkbox to be unchecked
     * @param timeout
     * @param checkboxName
     * @author genosar.dafna
     * @since 06.09.2023
     */
    public void waitForCheckboxToBeUnchecked(int timeout, @Nullable String checkboxName){

        if(checkboxName != null)
            checkboxName = String.format("'%s' ", checkboxName);

        try {
            WaitWrapper.waitForAttributeToBe(WebDriverInstanceManager.getDriverFromMap(), getInputField(), "aria-checked", "false", timeout);
        }
        catch(Throwable t){
            throw new Error(String.format("The status of check box %sdid not change to unchecked after %d seconds", checkboxName, timeout));
        }
    }

    /**
     * Wait for the checkbox to be checked
     * @param timeout
     * @param checkboxName
     * @author genosar.dafna
     * @since 06.09.2023
     */
    public void waitForCheckboxToBeChecked(int timeout, @Nullable String checkboxName){

        if(checkboxName != null)
            checkboxName = String.format("'%s' ", checkboxName);

        try {
            WaitWrapper.waitForAttributeToBe(WebDriverInstanceManager.getDriverFromMap(), getInputField(), "aria-checked", "true", timeout);
        }
        catch(Throwable t){
            throw new Error(String.format("The status of check box %sdid not change to checked after %d seconds", checkboxName, timeout));
        }
    }

    /**
     * @return true/false if the checkbox is checked
     * @author Dafna Genosar
     * @since 1.12.2021
     */
    public boolean isSelected()
    {
        return localElement.isSelected();
    }

    /**
     * @return true/false if the checkbox is enabled
     * @author Dafna Genosar
     * @since 1.12.2021
     * @modifier Dafna Genosar
     * @modified 14.03.2022
     */
    public boolean isEnabled()
    {
        return localElement.isEnabled();
    }
}


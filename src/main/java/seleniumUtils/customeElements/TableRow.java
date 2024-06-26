package seleniumUtils.customeElements;

import Managers.WebDriverInstanceManager;
import dateTimeUtils.DateTime;
import drivers.TesnetWebElement;
import Enumerations.MessageLevel;
import objectsUtils.ObjectsUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ReportUtils.Report;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class supports table rows.
 * @author genosar.dafna
 * @since 07.03.2022
 */
@SuppressWarnings({"unused", "unchecked"})
public class TableRow extends TesnetWebElement {

    private static final Logger logger = LoggerFactory.getLogger(TableRow.class);
    protected WebElement localElement;
    protected WebDriver driver;
    protected Table parentTable;
    private List<WebElement> cellsElements = null;

    public TableRow(WebDriver driver, WebElement element, Table _parentTable)
    {
        super(element);
        localElement = element;
        this.driver = driver;
        parentTable = _parentTable;
    }

    //1st constructor
    public TableRow(WebElement element, Table _parentTable)
    {
        this(WebDriverInstanceManager.getDriverFromMap(), element, _parentTable);
    }

    //2nd constructor
    public TableRow(TableRow row)
    {
        this(WebDriverInstanceManager.getDriverFromMap(), (row.localElement), row.parentTable);
        if(row.cellsElements != null)
            this.cellsElements = new ArrayList<>(row.cellsElements);
    }

    /**
     * @return the row's parent table
     * @param <T> generic type for table
     * @author genosar.dafna
     * @since 21.08.2023
     */
    public <T extends Table> T getParentTable(){
        return (T)parentTable;
    }

    /**
     * @return the current row's index within all table's rows
     * @author genosar.dafna
     * @since 17.10.2023
     */
    public int getIndex(){

        logger.info("Find the current row's index");
        List<WebElement> previousRows = getPrecedingRows();
        return previousRows.size();
    }

    /**
     * @return the preceding rows that display before the current row
     * @author genosar.dafna
     * @since 17.10.2023
     */
    public LinkedList<WebElement> getPrecedingRows(){
        logger.info("Find the current row's preceding rows");
        return new LinkedList<>(localElement.findElements(By.xpath("./preceding-sibling::tr[not(contains(@class, 'hidden'))]")));
    }

    /**
     * @return the following rows that display after the current row
     * @author genosar.dafna
     * @since 17.10.2023
     */
    public LinkedList<WebElement> getFollowingRows(){
        logger.info("Find the current row's following rows");
        return new LinkedList<>(localElement.findElements(By.xpath("./following-sibling::tr[not(contains(@class, 'hidden'))]")));
    }

    /**
     * @return all cells in the row as WebElements ./th or ./td or an empty list if no rows exist
     * @author Dafna Genosar
     * @since 07.03.2022
     * @since 28.11.2023
     */
    public List<WebElement> getCells()
    {
        if(cellsElements == null){
            logger.info("Get all row's cells");
            cellsElements = localElement.findElements(By.xpath("./*[contains(local-name(), 'th') or contains(local-name(), 'td')]"));
        }

        return cellsElements;
    }

    /**
     * @return get the number of cells in the row
     * @author Dafna Genosar
     * @since 04.06.2023
     */
    public int getNumberOfColumns(){
        return getCells().size();
    }

    /**
     * Return a row's cell in a specific index
     * @param index the cell's index
     * @return a row's cell in a specific index
     * @author Dafna Genosar
     * @since 07.03.2022
     * @author Dafna Genosar
     * @since 10.10.2023
     */
    public WebElement getCell(int index)
    {
        logger.info(String.format("Get cell in index %d", index));

        WebElement cell = null;

        if(cellsElements != null){
            if(index < cellsElements.size() && index >= 0){
                cell = cellsElements.get(index);
            }
        }
        else{
            try{
                cell = localElement.findElement(By.xpath(String.format("./*[contains(local-name(), 'th') or contains(local-name(), 'td')][%d]", index+1)));
            }
            catch (Exception e){
                //nothing to be done. cell is already null
            }
        }

        if(cell != null)
            return cell;
        else
            throw new Error(String.format("The row does not have a cell in index %d", index));
    }

    /**
     * Return a row's cell according to its header name
     * @param headerName the cell's header (column) name
     * @return a row's cell according to its header name
     * @author Dafna Genosar
     * @since 20.07.2022
     */
    public WebElement getCell(String headerName)
    {
        int headerIndex = parentTable.getHeaderIndex(headerName);
        if(headerIndex < 0)
            throw new Error(String.format("The row does not have a cell under header %s", headerName));

        return getCells().get(headerIndex);
    }

    /**
     * Return the first cell in the row
     * @return the first cell in the row
     * @author Dafna Genosar
     * @since 10.03.2022
     */
    public WebElement getFirstCell()
    {
        return getCell(0);
    }

    /**
     * Return the last cell in the row
     * @return the last cell in the row
     * @author Dafna Genosar
     * @since 10.03.2022
     */
    public WebElement getLastCell()
    {
        return getCell(getCells().size()-1);
    }

    /**
     * Get cell's text according to the column name
     * @param columnName the column name
     * @return cell's text according to the column name
     * @author genosar.dafna
     * @since 29.05.2023
     */
    public String getCellText(String columnName){
        return getCell(columnName).getText();
    }

    /**
     * Get cell's text according to the column index
     * @param columnIndex the column index
     * @return cell's text according to the column index
     * @author genosar.dafna
     * @since 29.08.2023
     */
    public String getCellText(int columnIndex){
        return getCell(columnIndex).getText();
    }

    /**
     * Get the checkbox in the cell, by the given cell index
     * @param index the cell's index
     * @return the checkbox in the cell, by the given cell index
     * @author genosar.dafna
     * @since 30.07.223
     */
    public CheckBox getCellCheckBox(int index)
    {
        WebElement cell = getCell(index);
        return getCellCheckBox(cell);
    }

    /**
     * Get the checkbox in the cell, by the given header name
     * @param headerName the cell header text
     * @return the checkbox in the cell, by the given header name
     * @author genosar.dafna
     * @since 30.07.223
     */
    public CheckBox getCellCheckBox(String headerName)
    {
        int headerIndex = parentTable.getHeaderIndex(headerName);
        if(headerIndex < 0)
            throw new Error(String.format("The row does not have a cell under header %s", headerName));

        WebElement cell = getCells().get(headerIndex);
        return getCellCheckBox(cell);
    }

    /**
     * Get the checkbox in the given cell
     * @param cell the cell WebElement
     * @return the checkbox in the given cell
     * @author genosar.dafna
     * @since 30.07.2023
     * @since 23.08.2023
     */
    public CheckBox getCellCheckBox(WebElement cell)
    {
        try{
            WebElement checkBox = cell.findElement(By.xpath(".//input[@type='checkbox' and not(contains(@class, 'hidden'))] | .//mat-checkbox"));
            return new CheckBox(checkBox);
        }
        catch (Exception e){
            throw new Error("Checkbox cannot be found in the cell");
        }
    }

    /**
     * Get the link in the given cell
     * @param index the cell's index
     * @return the link in the given cell
     * @author genosar.dafna
     * @since 10.08.2023
     */
    public WebElement getCellLink(int index)
    {
        WebElement cell = getCell(index);
        return getCellLink(cell);
    }

    /**
     * Get the link in the given cell
     * @param headerName tthe cell header text
     * @return the link in the given cell
     * @author genosar.dafna
     * @since 10.08.2023
     */
    public WebElement getCellLink(String headerName)
    {
        int headerIndex = parentTable.getHeaderIndex(headerName);
        if(headerIndex < 0)
            throw new Error(String.format("The row does not have a cell under header %s", headerName));

        WebElement cell = getCells().get(headerIndex);
        return getCellLink(cell);
    }

    /**
     * Get the link in the given cell
     * @param cell the cell WebElement
     * @return the link in the given cell
     * @author genosar.dafna
     * @since 10.08.2023
     * @since 09.10.2023
     */
    public WebElement getCellLink(WebElement cell)
    {
        try{
            return cell.findElement(By.xpath(".//a"));
        }
        catch (Exception e){
            throw new Error("Link cannot be found in the cell");
        }
    }

    /**
     * Get the input field in the given cell
     * @param index the cell's index
     * @return the field in the given cell
     * @author genosar.dafna
     * @since 10.08.2023
     */
    public WebElement getCellInputField(int index)
    {
        WebElement cell = getCell(index);
        return getCellInputField(cell);
    }

    /**
     * Get the input field in the given cell
     * @param headerName the cell header text
     * @return the field in the given cell
     * @author genosar.dafna
     * @since 10.08.2023
     */
    public WebElement getCellInputField(String headerName)
    {
        int headerIndex = parentTable.getHeaderIndex(headerName);
        if(headerIndex < 0)
            throw new Error(String.format("The row does not have a cell under header %s", headerName));

        WebElement cell = getCells().get(headerIndex);
        return getCellInputField(cell);
    }

    /**
     * Get the input field in the given cell
     * @param cell the cell WebElement
     * @return the field in the given cell
     * @author genosar.dafna
     * @since 10.08.2023
     */
    public WebElement getCellInputField(WebElement cell)
    {
        try{
            return cell.findElement(By.xpath(".//input"));
        }
        catch (Exception e){
            throw new Error("Input field cannot be found in the cell");
        }
    }

    /**
     * Get the input field in the given cell
     * @param index the cell's index
     * @return the field in the given cell
     * @author genosar.dafna
     * @since 10.08.2023
     */
    public <T> WebElement setCellInputField(int index, T value)
    {
        ReportUtils.Report.reportAndLog(String.format("Set the cell's input field in index %d to: %s", index, value), MessageLevel.INFO);
        WebElement cell = getCell(index);
        return setCellInputField(cell, value);
    }

    /**
     * Get the input field in the given cell
     * @param headerName the cell header text
     * @return the field in the given cell
     * @author genosar.dafna
     * @since 10.08.2023
     */
    public <T> WebElement setCellInputField(String headerName, T value)
    {
        ReportUtils.Report.reportAndLog(String.format("Set the cell's input field under header %s to: %s", headerName, value), MessageLevel.INFO);

        int headerIndex = parentTable.getHeaderIndex(headerName);
        if(headerIndex < 0)
            throw new Error(String.format("The row does not have a cell under header %s", headerName));

        WebElement cell = getCells().get(headerIndex);
        return setCellInputField(cell, value);
    }

    /**
     * set the input field in the cell
     * @param cellOrInputField the cell WebElement or the input field inside the cell
     * @return the input field in the cell
     * @author genosar.dafna
     * @since 10.08.2023
     * @since 17.10.2023
     */
    public <T> WebElement setCellInputField(WebElement cellOrInputField, T value)
    {
        if(value == null)
            throw new Error("Value received in setCellInputField is null");

        ReportUtils.Report.reportAndLog(String.format("Set value in cell to '%s'", value), MessageLevel.INFO);

        WebElement inputField = cellOrInputField.getTagName().equals("input")? cellOrInputField : getCellInputField(cellOrInputField);

        inputField.sendKeys(Keys.chord(Keys.CONTROL,"a"));

        // Send Delete
        inputField.sendKeys(Keys.DELETE);

        // Send Backspace
        inputField.sendKeys(Keys.BACK_SPACE);

        if(!value.equals(""))
            inputField.sendKeys(value.toString());

        return inputField;
    }

    /**
     * Set the input field in the given cells
     * @param columnNamesAndValues Hash of column names and values to set
     * @author genosar.dafna
     * @since 09.07.2023
     * @since 11.10.2023
     */
    public <T> void setCellsInputFields(Map<T, T> columnNamesAndValues){
        for (Map.Entry<T, T> set : columnNamesAndValues.entrySet()) {
            T columnKey = set.getKey();
            String value = set.getValue().toString();

            Report.reportAndLog(String.format("Set cell '%s' with value: '%s'", columnKey, value), MessageLevel.INFO);

            WebElement inputField;
            if(columnKey instanceof Integer)
                inputField = getCellInputField((int)columnKey);
            else
                inputField = getCellInputField(columnKey.toString());

            inputField.sendKeys(Keys.chord(Keys.CONTROL,"a"));

            // Send Delete
            inputField.sendKeys(Keys.DELETE);

            // Send Backspace
            inputField.sendKeys(Keys.BACK_SPACE);

            if(!value.equals(""))
                inputField.sendKeys(value);
        }
    }

    /**
     * Check if the row matches the expected values
     * @param rowData the expected values
     * in case you would like a few values to match and to make sure others DO NOT match, please add != before the key name
     * example:
     * if you would like to make sure the row has name = 'John', family name = 'Smith', but to make sure that the salary is NOT 0, then:
     *  Map.put("Name", "John")
     *  Map.put("Family Name", "Smith")
     *  Map.put("!=Salary", 0)
     * @return true/false if the row matches the expected value
     * @author Dafna Genosar
     * @since 07.03.2022
     * @since 06.09.2023
     */
    public <K, V, T extends Map<K, V>> boolean doesRowMatch(T rowData){
        //Go over each expected data
        for (Map.Entry<K, V> data: rowData.entrySet()) {

            K key = data.getKey();
            V expectedValue = data.getValue();

            boolean expectedCellToMatch = true;
            int columnIndex;

            if (key instanceof Integer)
                columnIndex = (Integer) key;
            else if (key instanceof String) {
                String keyString = (String)key;
                expectedCellToMatch = !keyString.substring(0,2).equals("!=");

                String headerName = keyString.replace("!=", "");
                //Find the current header's index
                columnIndex = parentTable.getHeaderIndex(headerName);
            } else
                throw new Error(String.format("HashMap key must either be a column name or a column index. Current type: %s", key.getClass()));

            if(expectedCellToMatch){
                //Check if the cell in this index matches the expected value
                if (!doesCellValueMatch(columnIndex, expectedValue)) {
                    return false;
                }
            }
            else {
                //Check if the cell in this index does NOT match the expected value
                if (doesCellValueMatch(columnIndex, expectedValue)) {
                    return false;
                }
            }

        }

        return true;
    }

    /**
     * Check if the cell value matches the expected value
     * @param columnIndex column index
     * @param expectedValue expected value. Can be a String or boolean
     * @return true/false if the cell value matches the expected value
     * @author Dafna Genosar
     * @since 07.03.2022
     * @since 06.09.2023
     */
    public <V> boolean doesCellValueMatch(int columnIndex, V expectedValue)
    {
        //Get the row's cell in this index
        WebElement cell = getCell(columnIndex);
        String cellText = cell.getText();

        //If the expected value is a String
        if(expectedValue instanceof String)
        {
            String expected = expectedValue.toString();
            return cellText.contains(expected);
        }
        else if(expectedValue instanceof Integer){
            int displayed;
            try{
                displayed = Integer.parseInt(cellText);
            }
            catch(Exception e){
                throw new Error("Cell text cannot be converted to int.<br>Error: " + e.getMessage());
            }
            return ObjectsUtils.areEqual(displayed, expectedValue);
        }
        else if(expectedValue instanceof DateTime){
            DateTime cellDateValue;
            try{
                cellDateValue = DateTime.parse(cellText); //Non US Date style
            }
            catch (Exception e){
                try{
                    cellDateValue = DateTime.parse(cellText, true); //US Date style
                }
                catch(Exception ee) {
                    return false;
                }
            }
            return ObjectsUtils.areEqual(cellDateValue, (DateTime) expectedValue);
        }
        else if(expectedValue instanceof TableHeaderCellStatus)
        {
            //should the checkbox be ticked or not
            boolean shouldBeChecked = (expectedValue == TableHeaderCellStatus.CHECKED);

            //TODO: will have to be adjusted as we come across more tables
            String classAttribute = cell.findElement(By.tagName("input")).getAttribute("class");

            boolean currentlyChecked = classAttribute.startsWith("active") || classAttribute.contains(" active") || classAttribute.contains("checked");

            return ObjectsUtils.areEqual(currentlyChecked, shouldBeChecked);
        }
        else
        {
            throw new Error("The expected value type is not supported. Please add support for other types in doesCellValueMatch()");
        }
    }

    /**
     * Check the checkbox in the cell
     * @param cell the cell element
     * @return the row
     * @author genosar.dafna
     * @since 30.07.223
     */
    public TableRow checkCell(WebElement cell){

        CheckBox checkBox = getCellCheckBox(cell);
        checkBox.check();
        return this;
    }

    /**
     * Check the checkbox in the cell, by the given cell index
     * @param index the cell's index
     * @return the row
     * @author genosar.dafna
     * @since 30.07.223
     */
    public TableRow checkCell(int index)
    {
        CheckBox checkBox = getCellCheckBox(index);
        checkBox.check();
        return this;
    }

    /**
     * Check the checkbox in the cell, by the given header name
     * @param headerName the cell header text
     * @return the row
     * @author genosar.dafna
     * @since 30.07.223
     */
    public TableRow checkCell(String headerName)
    {
        CheckBox checkBox = getCellCheckBox(headerName);
        checkBox.check();
        return this;
    }

    /**
     * Uncheck the checkbox in the cell
     * @param cell the cell element
     * @return the row
     * @author genosar.dafna
     * @since 30.07.223
     */
    public TableRow uncheckCell(WebElement cell){

        CheckBox checkBox = getCellCheckBox(cell);
        checkBox.uncheck();
        return this;
    }

    /**
     * Uncheck the checkbox in the cell, by the given cell index
     * @param index the cell's index
     * @return the row
     * @author genosar.dafna
     * @since 30.07.223
     */
    public TableRow uncheckCell(int index)
    {
        CheckBox checkBox = getCellCheckBox(index);
        checkBox.uncheck();
        return this;
    }

    /**
     * Uncheck the checkbox in the cell, by the given header name
     * @param headerName the cell header text
     * @return the row
     * @author genosar.dafna
     * @since 30.07.223
     */
    public TableRow uncheckCell(String headerName)
    {
        CheckBox checkBox = getCellCheckBox(headerName);
        checkBox.uncheck();
        return this;
    }

    /**
     * Click the link in the cell, by the given header name
     * @param cell the cell element
     * @return the row
     * @author genosar.dafna
     * @since 10.08.2023
     */
    public <T extends TableRow> T clickCellLink(WebElement cell){

        ReportUtils.Report.reportAndLog("Click the cell's link", MessageLevel.INFO);
        WebElement link = getCellLink(cell);
        link.click();
        return (T)this;
    }

    /**
     * Click the link in the cell, by the given header name
     * @param index the cell's index
     * @return the row
     * @author genosar.dafna
     * @since 10.08.2023
     */
    public <T extends TableRow> T clickCellLink(int index)
    {
        ReportUtils.Report.reportAndLog(String.format("Click the cell's link in index %d", index), MessageLevel.INFO);
        WebElement link = getCellLink(index);
        link.click();
        return (T)this;
    }

    /**
     * Click the link in the cell, by the given header name
     * @param headerName the cell header text
     * @return the row
     * @author genosar.dafna
     * @since 10.08.2023
     */
    public <T extends TableRow> T clickCellLink(String headerName)
    {
        ReportUtils.Report.reportAndLog(String.format("Click the cell's link under header %s", headerName), MessageLevel.INFO);
        WebElement link = getCellLink(headerName);
        link.click();
        return (T)this;
    }

    /**
     * Enum that represents the table header's cell status in case it has a checkbox
     * @author genosar.dafna
     * @since 13.03.2022
      */
    public enum TableHeaderCellStatus
    {
        CHECKED,
        UNCHECKED
    }
}

package verificationsUtils;

import AssertionsWithReport.AssertsWithReport;
import AssertionsWithReport.SoftAssertsWithReport;
import collectionUtils.MapUtils;
import com.relevantcodes.extentreports.LogStatus;
import dateTimeUtils.DateTime;
import Enumerations.MessageLevel;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ReportUtils.ExtentReportUtils;
import ReportUtils.ReportStyle;
import seleniumUtils.customeElements.CheckBox;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static collectionUtils.MapUtils.getMapRecord;
import static ReportUtils.ExtentReportUtils.extentLogger;
import static ReportUtils.Report.reportAndLog;
import static ReportUtils.ReportStyle.getFailureMessage;
import static ReportUtils.ReportStyle.getSuccessMessage;

@SuppressWarnings({"unused"})
public class CommonVerifications {

    private static final Logger logger = LoggerFactory.getLogger(CommonVerifications.class);

    /**
     * Verify a date is within the given dates range
     * @param date the date to check
     * @param startDate start date of date range
     * @param endDate end date of date range
     * @param date_name the checked date's name/ title to put in the report
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the date is within the given dates range / false otherwise
     * @author genosar.dafna
     * @since 28.11.2023
     */
    public static <T extends Number> boolean verifyDatesWithinRange(DateTime date, DateTime startDate, DateTime endDate, String date_name, @Nullable SoftAssertsWithReport softAssertsWithReport) {

        boolean isWithinDeviation = (date.compareTo(startDate) >= 0) && (date.compareTo(endDate) <=0);

        String successMessage = ReportStyle.getSuccessMessage(String.format("%s date %s is within the date range of %s and %s", date_name, date.toStringFormat("dd.MM.yyyy"), startDate.toStringFormat("dd.MM.yyyy"), endDate.toStringFormat("dd.MM.yyyy")));
        String errorMessage = ReportStyle.getFailureMessage(String.format("%s date %s is not within the date range of %s and %s", date_name, date.toStringFormat("dd.MM.yyyy"), startDate.toStringFormat("dd.MM.yyyy"), endDate.toStringFormat("dd.MM.yyyy")));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertTrue(isWithinDeviation, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(isWithinDeviation, successMessage, errorMessage);

        return isWithinDeviation;
    }

    /**
     * Verify the expected page title matches the displayed
     * @param expectedTitle expected title
     * @param displayedTitle displayed title
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if title match / false otherwise
     * @author genosar.dafna
     * @since 17.08.2023
     * @since 21.08.2023
     */
    public static boolean verifyPageTitleMatches(String expectedTitle, String displayedTitle, boolean ignoreCase, @Nullable SoftAssertsWithReport softAssertsWithReport){

        String successMessage = ReportStyle.getSuccessMessage(String.format("The page title is as expected: '%s'", expectedTitle));
        String errorMessage = ReportStyle.getFailureMessage(String.format("The page title is incorrect.<br>Displayed: '%s'<br>Expected: '%s'", displayedTitle, expectedTitle));

        boolean match = ignoreCase? expectedTitle.equalsIgnoreCase(displayedTitle) : expectedTitle.equals(displayedTitle);

        if(softAssertsWithReport == null)
            AssertsWithReport.assertTrue(match, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(match, successMessage, errorMessage);

        return match;
    }

    /**
     * Verify the button is enabled
     * @param button the button web element
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the button is enabled / false otherwise
     * @author genosar.dafna
     * @since 24.04.2023
     * @since 10.09.2023
     */
    public static boolean verifyButtonIsEnabled(WebElement button, @Nullable SoftAssertsWithReport softAssertsWithReport){

        boolean isEnabled = button.isEnabled();

        String successMessage = ReportStyle.getSuccessMessage(String.format("The '%s' button is enabled",button.getText()));
        String errorMessage = ReportStyle.getFailureMessage(String.format("The '%s' button is disabled",button.getText()));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertTrue(isEnabled, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(isEnabled, successMessage, errorMessage);

        return isEnabled;
    }

    /**
     * Verify the button is disabled
     * @param button the button web element
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the button is disabled / false otherwise
     * @author genosar.dafna
     * @since 24.04.2023
     * @since 10.09.2023
     */
    public static boolean verifyButtonIsDisabled(WebElement button, @Nullable SoftAssertsWithReport softAssertsWithReport){

        boolean isEnabled = button.isEnabled();

        String successMessage = ReportStyle.getSuccessMessage(String.format("The '%s' button is disabled",button.getText()));
        String errorMessage = ReportStyle.getFailureMessage(String.format("The '%s' button is enabled",button.getText()));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertFalse(isEnabled, successMessage, errorMessage);
        else
            softAssertsWithReport.assertFalse(isEnabled, successMessage, errorMessage);

        return !isEnabled;
    }

    /**
     * Verify List of maps is not empty (like DB query results)
     * @param listOfMaps the List of maps
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the list has values (not empty) / false otherwise
     * @author genosar.dafna
     * @since 06.11.2022
     * @since 10.09.2023
     */
    public static <K, V, T extends Map<K, V>, L extends List<T>> boolean verifyListOfMapsIsNotEmpty(L listOfMaps, @Nullable String errorMessage, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        errorMessage = errorMessage != null? errorMessage : ReportStyle.getFailureMessage("The list of maps does not have data");

        if (softAssertsWithReport == null)
            AssertsWithReport.assertTrue(listOfMaps.size()>0, null, errorMessage);
        else
            softAssertsWithReport.assertTrue(listOfMaps.size()>0, null, errorMessage);

        return listOfMaps.size() > 0;
    }

    /**
     * Verify List of maps is empty (like DB query results)
     * @param listOfMaps the List of maps
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the list is empty / false otherwise
     * @author ghawi.rami
     * @since 09.01.2023
     * @author genosar.dafna
     * @since 10.09.2023
     */
    public static <K, V, T extends Map<K, V>, L extends List<T>> boolean verifyListOfMapsIsEmpty(L listOfMaps, @Nullable String errorMessage, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        errorMessage = errorMessage != null? errorMessage : ReportStyle.getFailureMessage("The list of maps is not empty");

        if (softAssertsWithReport == null)
            AssertsWithReport.assertTrue(listOfMaps.size()==0, null, errorMessage);
        else
            softAssertsWithReport.assertTrue(listOfMaps.size()==0, null, errorMessage);

        return listOfMaps.size()==0;
    }


    /**
     * Verify the List is not empty
     * @param list the List
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the list has values (not empty) / false otherwise
     * @author genosar.dafna
     * @since 14.09.2023
     */
    public static <T, L extends List<T>> boolean verifyListIsNotEmpty(L list, @Nullable String successMessage, @Nullable String errorMessage, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        successMessage = successMessage != null? ReportStyle.getSuccessMessage(successMessage) : null;
        errorMessage = errorMessage != null? errorMessage : ReportStyle.getFailureMessage("The list is empty");

        if (softAssertsWithReport == null)
            AssertsWithReport.assertTrue(list.size()>0, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(list.size()>0, successMessage, errorMessage);

        return list.size() > 0;
    }

    /**
     * Verify the List is empty
     * @param list the List
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the list is empty / false otherwise
     * @author genosar.dafna
     * @since 14.09.2023
     */
    public static <T, L extends List<T>> boolean verifyListIsEmpty(L list, @Nullable String successMessage, @Nullable String errorMessage, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        successMessage = successMessage != null? ReportStyle.getSuccessMessage(successMessage) : null;
        errorMessage = errorMessage != null? errorMessage : ReportStyle.getFailureMessage("The list is not empty");

        if (softAssertsWithReport == null)
            AssertsWithReport.assertTrue(list.size()==0, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(list.size()==0, successMessage, errorMessage);

        return list.size() == 0;
    }

    /**
     * Verify query results are not empty
     * @param resultsData the query results
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the results are not empty / false otherwise
     * @author genosar.dafna
     * @since 06.11.2022
     * @since 10.09.2023
     */
    public static <K, V, T extends Map<K, V>, L extends List<T>> boolean verifyQueryResultsAreNotEmpty(L resultsData, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        String errorMessage = "The query did not return data";
        return verifyListOfMapsIsNotEmpty(resultsData, errorMessage, softAssertsWithReport);
    }

    /**
     * Verify query results are empty
     * @param resultsData the query results
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the results are empty / false otherwise
     * @author ghawi.rami
     * @since 09.01.2023
     * @author genosar.dafna
     * @since 10.09.2023
     */
    public static <K, V, T extends Map<K, V>, L extends List<T>> boolean verifyQueryResultsAreEmpty(L resultsData, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        String errorMessage = "The query returned data, even though it should be empty";
        return verifyListOfMapsIsEmpty(resultsData, errorMessage, softAssertsWithReport);
    }

    /**
     * Verify number of entry sets in the list of maps (like db query records)
     * @param listOfMaps the List of maps
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the Number of Entry Sets matches the expected / false otherwise
     * @author genosar.dafna
     * @since 08.02.2023
     * @since 10.09.2023
     */
    public static <K, V, T extends Map<K, V>, L extends List<T>> boolean verifyNumberOfEntrySets(L listOfMaps, int expected, @Nullable String successMessage, @Nullable String errorMessage, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        successMessage = (successMessage != null)? successMessage : getSuccessMessage(String.format("The number of maps sets in the list is correct: %d", expected));
        errorMessage = (errorMessage != null)? errorMessage : getFailureMessage(String.format("The number of maps sets in the list is incorrect <br>In Db: %d<br>Expected: %d", listOfMaps.size(), expected));

        if (softAssertsWithReport == null)
            AssertsWithReport.assertEquals(expected, listOfMaps.size(), successMessage, errorMessage);
        else
            softAssertsWithReport.assertEquals(expected, listOfMaps.size(), successMessage, errorMessage);

        return expected == listOfMaps.size();
    }

        /**
     * Verify number of query records
     * @param resultsData the query results
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the Number of Query Records matches the expected / false otherwise
     * @author genosar.dafna
     * @since 08.02.2023
     * @since 10.09.2023
     */
    public static <K, V, T extends Map<K, V>, L extends List<T>> boolean verifyNumberOfQueryRecords(L resultsData, int expected, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {

        String successMessage = String.format("The number of query records is correct: %d", expected);
        String errorMessage = String.format("The number of query records is incorrect <br>In Db: %d<br>Expected: %d", resultsData.size(), expected);

        return verifyNumberOfEntrySets(resultsData, expected, successMessage, errorMessage, softAssertsWithReport);
    }

    /**
     * Verify the list of maps contain an entry with the given data
     * @param listOfMaps list of maps
     * @param mapData map of data to search
     * @param successMessage optional success message. if null will report the default
     * @param errorMessage optional error message. if null will report the default
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @author genosar.dafna
     * @since 31.01.2023
     * @since 16.07.2023
     */
    public static <K, V, T extends Map<K, V>, L extends List<T>> T verifyMapEntrySetExists(L listOfMaps, T mapData, @Nullable String successMessage, @Nullable String errorMessage, @Nullable SoftAssertsWithReport softAssertsWithReport){

        reportAndLog(String.format("<b>Map verification - Verify map entry set exists: %s</b>", mapData.toString()), MessageLevel.INFO);
        T entrySet = getMapRecord(listOfMaps, mapData);

        successMessage = (successMessage != null)? getSuccessMessage(successMessage) : getSuccessMessage(String.format("The list of maps contain entry: <br> %s", mapData));
        errorMessage = (errorMessage != null)? getFailureMessage (errorMessage) : getFailureMessage(String.format("The list of maps does not contain entry: <br> %s", mapData));

        if (softAssertsWithReport == null)
            AssertsWithReport.assertNotNull(entrySet, successMessage, errorMessage);
        else
            softAssertsWithReport.assertNotNull(entrySet, successMessage, errorMessage);

        return entrySet;
    }

    /**
     * Verify list of maps does not contain an entry with the given data
     * @param listOfMaps list of maps
     * @param mapData map of data to search
     * @param successMessage optional success message. if null will report the default
     * @param errorMessage optional error message. if null will report the default
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @author genosar.dafna
     * @since 07.02.2023
     * @since 16.07.2023
     */
    public static <K, V, T extends Map<K, V>, L extends List<T>> T verifyMapEntrySetDoesNotExist(L listOfMaps, T mapData, @Nullable String successMessage, @Nullable String errorMessage, @Nullable SoftAssertsWithReport softAssertsWithReport){

        reportAndLog(String.format("<b>Map verification - Verify map entry set does not exist: %s</b>", mapData.toString()), MessageLevel.INFO);

        T entrySet = getMapRecord(listOfMaps, mapData);

        successMessage = (successMessage != null)? getSuccessMessage(successMessage) : getSuccessMessage(String.format("The list of maps does not contain the expected entry, as expected: <br> %s", mapData));
        errorMessage = (errorMessage != null)? getFailureMessage (errorMessage) : getFailureMessage(String.format("The list of maps contains the entry, even though it should not: <br> %s", mapData));

        if (softAssertsWithReport == null)
            AssertsWithReport.assertNull(entrySet, successMessage, errorMessage);
        else
            softAssertsWithReport.assertNull(entrySet, successMessage, errorMessage);

        return entrySet;
    }

    /**
     * Verify the DB results contain an entry with the given data
     * @param dbResults db results
     * @param rowData hash of data to search
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @author genosar.dafna
     * @since 31.01.2023
     */
    public static <K, V, T extends Map<K, V>, L extends List<T>> T verifyDbRowExists(L dbResults, T rowData, @Nullable SoftAssertsWithReport softAssertsWithReport){

        String successMessage = getSuccessMessage(String.format("The db results contain entry: <br> %s", rowData));
        String errorMessage = getFailureMessage(String.format("The db results do not contain entry: <br> %s", rowData));

        return verifyMapEntrySetExists(dbResults, rowData, successMessage, errorMessage, softAssertsWithReport);

    }

    /**
     * Verify the DB results does not contain an entry with the given data
     * @param dbResults db results
     * @param rowData hash of data to search
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @author genosar.dafna
     * @since 07.02.2023
     */
    public static <K, V, T extends Map<K, V>, L extends List<T>> T verifyDbRowDoesNotExist(L dbResults, T rowData, @Nullable SoftAssertsWithReport softAssertsWithReport){

        String successMessage = getSuccessMessage(String.format("The db results does not contain entry, as expected: <br> %s", rowData));
        String errorMessage = getFailureMessage(String.format("The db results contains entry, even though it should not: <br> %s", rowData));

        return verifyMapEntrySetDoesNotExist(dbResults, rowData, successMessage, errorMessage, softAssertsWithReport);
    }

    /**
     * Verify the map entry value match the expected
     * @param entry row entry
     * @param expected the expected value
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @author genosar.dafna
     * @since 31.01.2023
     * @since 16.07.2023
     */
    public static <K, V, T extends Map<K, V>> T verifyEntryValueMatches(T entry, K key, V expected, @Nullable SoftAssertsWithReport softAssertsWithReport){

        V value = entry.get(key);

        boolean containsKey = entry.containsKey(key);

        String errorMessage = getFailureMessage(String.format("The Map does not contain key '%s'", key));

        if (softAssertsWithReport == null)
            AssertsWithReport.assertTrue(containsKey, null, errorMessage);
        else
            softAssertsWithReport.assertTrue(containsKey, null, errorMessage);

        if(!containsKey)
            return null;

        String successMessage = getSuccessMessage(String.format("The value of '%s' in map entry set is correct: %s", key, value.toString()));
        errorMessage = getFailureMessage(String.format("The value of '%s' in map entry set is incorrect: %s. Expected: %s", key, value, expected));

        boolean match = Objects.equals(value, expected);

        if (softAssertsWithReport == null)
            AssertsWithReport.assertTrue(match, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(match, successMessage, errorMessage);

        return entry;
    }

    /**
     * Verify the map entry values match the expected values
     * @param entry row entry
     * @param expected the expected values
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return the entry (row) that matched the expected data, or else null if not found
     * @author genosar.dafna
     * @since 09.02.2023
     * @since 16.07.2023
     */
    public static <K, V, T extends Map<K, V>> T verifyEntryValuesMatch(T entry, T expected, @Nullable SoftAssertsWithReport softAssertsWithReport){

        T row = null;
        for (Map.Entry<K, V> expectedData: expected.entrySet()) {
            K expectedKey = expectedData.getKey();
            V expectedValue = expectedData.getValue();
            row = verifyEntryValueMatches(entry, expectedKey, expectedValue, softAssertsWithReport);
            if(row == null)
                return null;
        }
        return row;
    }

    /**
     * Verify actual map of data matches the expected data
     * @param actualData the actual data
     * @param expectedData the expected data
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return rue if maps match, false otherwise
     * @author genosar.dafna
     * @since 02.05.2023
     * @since 26.10.2023
     */
    public static <K, V, T extends Map<K, V>> boolean verifyComparisonOfMaps(T actualData, T expectedData, String data1_name, String data2_name, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        boolean match = MapUtils.compareMaps(actualData, expectedData, data1_name, data2_name, MessageLevel.ERROR);

        String successMessage = ReportStyle.getSuccessMessage(String.format("%s matches %s", data1_name, data2_name));
        String errorMessage = ReportStyle.getFailureMessage(String.format("%s does not match %s", data1_name, data2_name));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertTrue(match, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(match, successMessage, errorMessage);

        return match;
    }

    /**
     * Verify actual list of maps matches the expected data. Will not compare the content if the lists are not the same size
     * @param actualData the actual data
     * @param expectedData the expected data
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @author genosar.dafna
     * @since 02.05.2023
     * @since 04.10.2023
     */
    public static <K, V, T extends Map<K, V>, L extends List<T>> void verifyComparisonOfHashMapsLists(L actualData, L expectedData, String data1_name, String data2_name, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        reportAndLog(String.format("Compare Map lists '%s' and '%s'", data1_name, data2_name), MessageLevel.INFO);

        boolean sameSize = actualData.size() == expectedData.size();

        String successMessage = ReportStyle.getSuccessMessage("The size of Actual data list and Expected data list is the same");
        String errorMessage = ReportStyle.getFailureMessage(String.format("The size of Actual data list is not the same size as the expected data list<br>" +
                "Actual data size: %d<br>" +
                "Expected data size: %s", actualData.size(), expectedData.size()));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertTrue(sameSize, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(sameSize, successMessage, errorMessage);

        if (sameSize) {
            for(int i=0; i<actualData.size() ; i++) {
                verifyComparisonOfMaps(actualData.get(i), expectedData.get(i), data1_name, data2_name, softAssertsWithReport);
            }
        }
    }

    /**
     * Verify values match
     * @param obj1 first obj to compare
     * @param obj2 second obj to compare
     * @param obj1_name obj1 name
     * @param obj2_name obj2 name
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if matches / false otherwise
     * @author genosar.dafna
     * @since 02.05.2023
     * @since 17.05.2023
     */
    public static <T extends Comparable<T>> boolean verifyValuesMatch(T obj1, T obj2, String obj1_name, String obj2_name, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        boolean match;

        String obj1String;
        String obj2String;

        if(obj1 instanceof DateTime) {
            obj1String = ((DateTime) obj1).toLongTimeString();
            obj2String = ((DateTime) obj2).toLongTimeString();

            match = ((DateTime)obj1).compareTo((DateTime)obj2) == 0;
        }
        else {
            obj1String = obj1.toString();
            obj2String = obj2.toString();

            match = obj1.compareTo(obj2) == 0;
        }

        String successMessage = ReportStyle.getSuccessMessage(String.format("%s matches %s - %s", obj1_name, obj2_name, obj1String));
        String errorMessage = ReportStyle.getFailureMessage(String.format("%s does not match %s<br>%s: %s<br>%s: %s", obj1_name, obj2_name, obj1_name, obj1String, obj2_name, obj2String));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertTrue(match, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(match, successMessage, errorMessage);

        return match;
    }

    /**
     * Verify values do not match
     * @param obj1 first obj to compare
     * @param obj2 second obj to compare
     * @param obj1_name obj1 name
     * @param obj2_name obj2 name
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the values do not match / false otherwise
     * @author genosar.dafna
     * @since 11.05.2023
     * @since 10.09.2023
     */
    public static <T extends Comparable<T>> boolean verifyValuesDoNotMatch(T obj1, T obj2, String obj1_name, String obj2_name, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        boolean match = obj1.compareTo(obj2) == 0;

        String successMessage = ReportStyle.getSuccessMessage(String.format("%s does not match %s, as expected", obj1_name, obj2_name));
        String errorMessage = ReportStyle.getFailureMessage(String.format("%s matches %s, even though it should not<br>%s: %s<br>%s: %s", obj1_name, obj2_name, obj1_name, obj1, obj2_name, obj2));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertFalse(match, successMessage, errorMessage);
        else
            softAssertsWithReport.assertFalse(match, successMessage, errorMessage);

        return !match;
    }

    /**
     * Verify 2 numbers are within an allowed deviation.
     * For example:
     * El-Al allows a suitcase weight of 30KG
     * The deviation EL AL allows is 2 KG, which means it will not charge extra if the suitcase is over 30KG, but up to 32KG
     * The passenger brought a 32KG suitcase - no extra charge
     * The passenger brought a 33KG suitcase - there will be an extra charge
     * @param num1 first number, for example: the allowed suitcase weight written on EL_AL site is 30KG
     * @param num2 second number, for example: the actual suitcase weight the passenger brought is 32KG
     * @param allowedDeviation the allowed deviation, for example: El-Al will not charge if the actual weight is withing the deviation of 2KG
     * @param num1_name name of first value, like "EL AL Allowed weight"
     * @param num2_name name of first value, like "Actual weight"
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the numbers are within the allowed deviation / false otherwise
     * @param <T> generic number type, like int, double, float
     * @author genosar.dafna
     * @since 18.10.2023
     */
    public static <T extends Number> boolean verifyValuesWithinDeviation(T num1, T num2, T allowedDeviation, String num1_name, String num2_name, @Nullable SoftAssertsWithReport softAssertsWithReport) {

        double value1 = num1.doubleValue();
        double value2 = num2.doubleValue();
        double result = value1 - value2;

        boolean isWithinDeviation = (Math.abs(result) <= allowedDeviation.doubleValue());

        String successMessage = ReportStyle.getSuccessMessage(String.format("%s and %s are within the allowed deviation of %s", num1_name, num2_name, allowedDeviation));
        String errorMessage = ReportStyle.getFailureMessage(String.format("%s and %s are not within the allowed deviation of %s<br>%s: %s<br>%s: %s", num1_name, num2_name, allowedDeviation, num1_name, num1, num2_name, num2));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertTrue(isWithinDeviation, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(isWithinDeviation, successMessage, errorMessage);

        return isWithinDeviation;
    }

    /**
     * Verify the Excel download time was within the expected threshold
     * @param actualDownloadSeconds actual download time
     * @param expectedThresholdSeconds threshold
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the Excel Download Time was Within Threshold / false otherwise
     * @author genosar.dafna
     * @since 24.04.2023
     * @since 10.09.2023
     */
    public static boolean verifyExcelDownloadTimeIsWithinThreshold(Double actualDownloadSeconds, Double expectedThresholdSeconds, @Nullable SoftAssertsWithReport softAssertsWithReport) {

        String successMessage = getSuccessMessage(String.format("Excel download time was within the reasonable time of %s seconds", expectedThresholdSeconds));
        String errorMessage = getFailureMessage(String.format("Excel download time was not within the reasonable time of %s seconds. <br>Download time was %s", expectedThresholdSeconds, actualDownloadSeconds));

        if (softAssertsWithReport == null)
            AssertsWithReport.assertTrue(actualDownloadSeconds <= expectedThresholdSeconds, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(actualDownloadSeconds <= expectedThresholdSeconds, successMessage, errorMessage);

        return actualDownloadSeconds <= expectedThresholdSeconds;
    }

    /**
     * Verify the Excel download time was within the exoected timeout
     * @param actualDownloadSeconds actual download time
     * @param expectedTimeoutSeconds timeout limit
     * @param elementName element name
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the Element Loading Time was Within Threshold / false otherwise
     * @author genosar.dafna
     * @since 24.04.2023
     * @since 10.09.2023
     */
    public static boolean verifyElementLoadingTimeIsWithinThreshold(Double actualDownloadSeconds, Double expectedTimeoutSeconds, String elementName, @Nullable SoftAssertsWithReport softAssertsWithReport) {

        String successMessage = getSuccessMessage(String.format("%s loading time was within the reasonable time of %s seconds: %s", elementName, expectedTimeoutSeconds, actualDownloadSeconds));
        String errorMessage = getFailureMessage(String.format("%s loading time was not within the reasonable time of %s seconds. <br>Download time was %s", elementName, expectedTimeoutSeconds, actualDownloadSeconds));

        if (softAssertsWithReport == null)
            AssertsWithReport.assertTrue(actualDownloadSeconds <= expectedTimeoutSeconds, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(actualDownloadSeconds <= expectedTimeoutSeconds, successMessage, errorMessage);

        return actualDownloadSeconds <= expectedTimeoutSeconds;
    }

    /**
     * Verify the file was downloaded successfully
     * @param fileDownloaded true/false if a new file was downloaded to DOWNLOADS folder
     * @param filePath the file path
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the File was Downloaded / false otherwise
     * @author genosar.dafna
     * @since 15.06.2023
     * @since 10.09.2023
     */
    public static boolean verifyFileDownloaded(boolean fileDownloaded, String filePath, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        String successMessage = getSuccessMessage("The file was downloaded successfully");
        String errorMessage = getFailureMessage(String.format("The file was not downloaded successfully to %s", filePath));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertTrue(fileDownloaded, null, errorMessage);
        else
            softAssertsWithReport.assertTrue(fileDownloaded, null, errorMessage);

        boolean fileExists = false;

        if(fileDownloaded){
            fileExists = new File(filePath).exists();
            if(softAssertsWithReport == null)
                AssertsWithReport.assertTrue(fileExists, successMessage, errorMessage);
            else
                softAssertsWithReport.assertTrue(fileExists, successMessage, errorMessage);
        }

        return fileDownloaded && fileExists;
    }

    /**
     * Method compares two objects and writes results to log
     * @param actual object for test
     * @param expected how the tested object should be like
     * @param description description of the object for test
     * @return true if the objects are equal, false otherwise
     */

    public static boolean verifyActualIsAsExpected(Object actual, Object expected, String description)
    {
        boolean verified = true;
        try
        {
            logger.info("Verifying that actual " + description + " is " + expected);
            if (expected.equals(actual))
            {
                logger.info("PASS. Expected " + description + " -" + expected + "- verified");
                extentLogger(LogStatus.PASS, "Expected " + description + " -" + expected + "- verified");
            }

            else
            {
                verified = false;
                logger.info("FAIL. Expected " + description + " -" + expected + "- not verified");
                extentLogger(LogStatus.FAIL, "Expected " + description + " -" + expected + "- not verified");
            }
        }
        catch(Exception e)
        {
            verified = false;
            logger.info("Caught exception: " + e);
        }

        return verified;
    }

    public static boolean verifyActualIsAsExpected(Object actual, Object expected, String description, String screenshotFilePath)
    {
        boolean isVerified = verifyActualIsAsExpected(actual, expected, description);
        if (!isVerified)
        {
            ExtentReportUtils.attachScreenshotToExtentReport(screenshotFilePath);
        }

        return isVerified;
    }

    /**
     * Verify the element is displayed
     * @param element the web element
     * @param elementName the name of the element
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the Element is Displayed / false otherwise
     * @author ghawi.rami
     * @since 28.08.2023
     * @author genosar.dafna
     * @since 10.09.2023
     */
    public static boolean verifyElementIsDisplayed(WebElement element,String elementName, @Nullable SoftAssertsWithReport softAssertsWithReport){

        boolean isDisplayed = element.isDisplayed();

        String successMessage = ReportStyle.getSuccessMessage(String.format("The '%s' element is displayed",elementName));
        String errorMessage = ReportStyle.getFailureMessage(String.format("The '%s' element is not displayed",elementName));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertTrue(isDisplayed, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(isDisplayed, successMessage, errorMessage);

        return isDisplayed;
    }

    /**
     * Verify the status of the checkbox is as expected
     * @param checkBox the checkBox
     * @param checkboxName the checkbox name
     * @param shouldBeChecked true if the checkbox should be checked / false otherwise
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the checkbox status is as expected / false otherwise
     * @author genosar.dafna
     * @since 05.09.2023
     * @since 10.09.2023
     */
    public static boolean verifyCheckboxStatus(CheckBox checkBox, String checkboxName, boolean shouldBeChecked, @Nullable SoftAssertsWithReport softAssertsWithReport){

        boolean isChecked = checkBox.isSelected();
        String expectedStatus = shouldBeChecked? "checked" : "unchecked";
        String displayedStatus = isChecked? "checked" : "unchecked";

        String successMessage = ReportStyle.getSuccessMessage(String.format("The '%s' checkbox's status is as expected: %s", checkboxName, expectedStatus));
        String errorMessage = ReportStyle.getFailureMessage(String.format("The '%s' checkbox's status is incorrect <br>Current status: %s<br>Expected status: %s", checkboxName, expectedStatus, displayedStatus));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertEquals(shouldBeChecked, isChecked, successMessage, errorMessage);
        else
            softAssertsWithReport.assertEquals(shouldBeChecked, isChecked, successMessage, errorMessage);

        return shouldBeChecked == isChecked;
    }
}

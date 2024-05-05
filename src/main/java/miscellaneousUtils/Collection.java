package miscellaneousUtils;

import Enumerations.MessageLevel;
import Managers.ReportInstanceManager;
import collectionUtils.ListUtils;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.relevantcodes.extentreports.LogStatus;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static ReportUtils.Report.reportAndLog;
import static java.util.Collections.sort;
import static objectsUtils.StringUtils.compareStringsLength;
//import static miscellaneousUtils.MiscellaneousUtils.compareStringsLength;

public class Collection {

    private static Logger logger = LoggerFactory.getLogger(Collection.class);

    /** Convert List<String> to lowerCase letters
     *
     * @param list - list to convert
     * @return list in lowercase letters
     *
     * @author abo_saleh.rawand
     * @since 18.8.2022
     */
    @Deprecated // Moved to ListUtils
    public static List<String> convertListToLowerCase(List<String> list){
        List<String> lowerCaseList = new ArrayList<>();
        for(String item:list){
            lowerCaseList.add(item.toLowerCase());
        }
        return lowerCaseList;
    }

    /**
     * Get the hash map as separated lines with bold keys, to use nicely in a report
     * @param dataToPrint the hashmap
     * @param <K> generic Key
     * @param <V> generic value
     * @return the hash map as separated lines with bold keys, to use nicely in a report
     * @author genosar.dafna
     * @since 06.06.2022
     */
    public static <K, V> String prettyPrintHashMap(HashMap<K,V> dataToPrint)
    {
        String line = "";
        for (Map.Entry<K,V> entry : dataToPrint.entrySet()) {
            line += String.format("<b>%s: </b> %s<br>", entry.getKey().toString(), entry.getValue());
        }
        return line.substring(0, line.length()-4);
    }

    /**
     * Remove selected entries from a HashMap
     * @param hashMapToClean the HashMap to remove the entries from
     * @param entriesToRemove a HashMap of entries to remove
     * @return the new HashMap without the entries
     * @author Dafna Genosar
     * @since 17.02.2022
     */
    public static HashMap<String, String> removeEntriesFromHashMap(HashMap<String, String> hashMapToClean, HashMap<String, String> entriesToRemove)
    {
        for (Map.Entry<String,String> entryToRemove:  entriesToRemove.entrySet()) {

            String keyToRemove = entryToRemove.getKey();
            String valueToRemove = entryToRemove.getValue();

            if(hashMapToClean.containsKey(keyToRemove) && hashMapToClean.get(keyToRemove).equals(valueToRemove))
                hashMapToClean.remove(entryToRemove.getKey(), entryToRemove.getValue());
        }
        return hashMapToClean;
    }

    /**
     * Remove selected entries from a HashMap by a value
     * @param hashMapToClean the HashMap to remove the entries from
     * @param entriesValueToRemove a values by which to search and remove from the HashMap
     * @return the new HashMap without the entries that include the value
     * @author Dafna Genosar
     * @since 02.03.2022
     */
    public static HashMap<String, String> removeEntriesFromHashMapByValue(HashMap<String, String> hashMapToClean, String entriesValueToRemove)
    {
        List<String> entriesValuesToRemove = Arrays.asList(entriesValueToRemove);

        return removeEntriesFromHashMapByValues(hashMapToClean, entriesValuesToRemove);
    }

    /**
     * Remove selected entries from a HashMap by a list of values
     * @param hashMapToClean the HashMap to remove the entries from
     * @param entriesValuesToRemove a list of values by which to search and remove from the HashMap
     * @return the new HashMap without the entries that include these values
     * @author Dafna Genosar
     * @since 02.03.2022
     */
    public static HashMap<String, String> removeEntriesFromHashMapByValues(HashMap<String, String> hashMapToClean, List<String> entriesValuesToRemove)
    {
        //Go over the list of values and check if the HashMap has any key with these values
        //If it has, add them to a new hash called entriesToRemove
        HashMap<String, String> entriesToRemove = new HashMap<>();

        for (String valueToRemove: entriesValuesToRemove) {
            for (Map.Entry<String,String> entry:  hashMapToClean.entrySet()) {
                String entryValue = entry.getValue();
                String entryKey = entry.getKey();
                boolean match;

                if(entryValue == null)
                    match = (valueToRemove == null);
                else
                    match = entryValue.equals(valueToRemove);

                if(match)
                    entriesToRemove.put(entryKey, entryValue);
            }
        }

        return removeEntriesFromHashMap(hashMapToClean, entriesToRemove);
    }

    /**
     * The method compares 2 String HashMaps and prints any differences
     * @param hashMap_1 1st hashMap
     * @param hashMap_2 2nd hashMap
     * @param hashMapName1 an optional, yet advised, parameter to add the name of the map,
     *                     so it will be clear in the report. Like: "DB_HashMap", "Page_Details" etc
     * @param hashMapName2 an optional, yet advised, parameter to add the name of the map,
     *                     so it will be clear in the report.
     * @param messageLevel messageLevel like ERROR/INFO
     * @return true/false if the maps are equal
     * @author Dafna Genosar
     * @since 01.02.2022
     * @modified 11.04.2022
     */
    public static <T> boolean compareHashMaps(HashMap<String, T> hashMap_1, HashMap<String, T> hashMap_2, String hashMapName1, String hashMapName2, MessageLevel messageLevel)
    {
        Map<String, T> map_1 = new HashMap<>(hashMap_1);
        Map<String, T> map_2 = new HashMap<>(hashMap_2);
        return compareMaps(map_1, map_2, hashMapName1, hashMapName2, messageLevel);
    }

    /**
     * The method compares 2 String maps and prints any differences
     * @param map_1 1st hashMap
     * @param map_2 2nd hashMap
     * @param mapName1 an optional, yet advised, parameter to add the name of the map,
     *                     so it will be clear in the report. Like: "DB_HashMap", "Page_Details" etc
     * @param mapName2 an optional, yet advised, parameter to add the name of the map,
     *                     so it will be clear in the report.
     * @param messageLevel messageLevel like ERROR/INFO
     * @return true/false if the maps are equal
     * @author Dafna Genosar
     * @since 11.04.2022
     * @modified Dafna Genosar
     * @modifier 26.06.2022
     */
    public static <T> boolean compareMaps(Map<String, T> map_1, Map<String, T> map_2, String mapName1, String mapName2, MessageLevel messageLevel)
    {
        double rowWidth = 90;
        String headerStyle = String.format("background-color: #D6EEEE; width: %s%%; border: 1px solid black; font-weight: bold;", rowWidth);

        boolean match = true;

        //Check if the hashmaps are equal
        if(!map_1.equals(map_2))
        {
            if(map_1.size() == 0)
                reportAndLog(String.format("The Map '%s' has no entries", mapName1), messageLevel);
            else if(map_2.size() == 0)
                reportAndLog(String.format("The Map '%s' has no entries", mapName2), messageLevel);
            else {

                //Different entries values between the maps
                String mapDifferences = getMapDifferencesToPrint(map_1, map_2);

                String reportTitleLine = String.format("Map '%s' and '%s' have the following entries differences: <br>", mapName1, mapName2);
                String tableHeaderLine = PrettyPrintTable.getTableRowToPrint(headerStyle, rowWidth, "", mapName1, mapName2);
                String reportLine = reportTitleLine + tableHeaderLine + mapDifferences;

                reportAndLog(reportLine, messageLevel);
            }

            match = false;
        }
        return match;
    }

    /**
     * Get the differences between the 2 maps as a String to print to the report
     * @param <T> generic type
     * @return a String to print to the report
     * @author genosar.dafna
     * @since 23.03.2022
     * @modified Dafna Genosar
     * @modifier 26.06.2022
     */
    private static <T> String getMapDifferencesToPrint(Map<String, T> map_1, Map<String, T> map_2)
    {
        double rowWidth = 90;
        String headerStyle = String.format("background-color: #D6EEEE; width: %s%%; border: 1px solid black; font-weight: bold;", rowWidth);
        String innerHeaderStyle = String.format("background-color: rgb(233,233,233); width: %s%%; border: 1px solid black; font-weight: bold;", rowWidth);
        String innerSeparatorStyle = String.format("background-color: rgb(39,39,39); font-size: 2px; height: 3px; width: %s%%; border: 1px solid black; font-weight: bold;", rowWidth);
        String rowStyle = String.format("width: %s%%; border: 1px solid black;", rowWidth);

        String error = "";

        MapDifference<String, T> mapDifferences = Maps.difference(map_1, map_2);

        if (!mapDifferences.areEqual()) {

            //Get the entries differences
            Map<String, MapDifference.ValueDifference<T>> entriesDiffering = mapDifferences.entriesDiffering();

            for(Map.Entry<String, MapDifference.ValueDifference<T>> entry : entriesDiffering.entrySet())
            {
                String key = entry.getKey();

                //If the entry's value itself is a list
                if((entry.getValue()).leftValue() instanceof ArrayList)
                {
                    List<Map<String, MapDifference.ValueDifference<T>>> left = ((ArrayList<Map<String, MapDifference.ValueDifference<T>>>)entry.getValue().leftValue());
                    List<Map<String, MapDifference.ValueDifference<T>>> right = ((ArrayList<Map<String, MapDifference.ValueDifference<T>>>)entry.getValue().rightValue());

                    error += getHashMapListToPrint(rowWidth, key, left, right);
                }
                //Else, if the entry's value is not a list
                else {
                    String value;

                    //If neither of the sides' values are null, change them to Strings and highlight the differences in yellow
                    if(entry.getValue().leftValue() != null && entry.getValue().rightValue() != null)
                    {
                        String leftValue = entry.getValue().leftValue().toString();
                        String rightValue = entry.getValue().rightValue().toString();

                        //Highlight the differences in yellow
                        List<String> highlightedDifferences = PrettyPrintTable.highlightStringsDifferences(leftValue, rightValue);
                        value = PrettyPrintTable.getTableRowToPrint(rowStyle, rowWidth, key, highlightedDifferences.get(0), highlightedDifferences.get(1));
                    }
                    //Else, if one of the sides is null, just print it
                    else {
                        Object leftValue = entry.getValue().leftValue() == null? "null" : entry.getValue().leftValue();
                        Object rightValue = entry.getValue().rightValue() == null? "null" : entry.getValue().rightValue();
                        value = PrettyPrintTable.getTableRowToPrint(rowStyle, rowWidth, key, leftValue, rightValue);
                    }

                    //Add the table row to the final error
                    error += value;//PrettyPrintTable.getTableRowToPrint(null, 100, "", key, value);
                }
            }

            //Check if there are extra entries on the left (1st Map)
            Map<String, T> differingOnLeft = mapDifferences.entriesOnlyOnLeft();
            if(differingOnLeft.size() > 0)
            {
                for(Map.Entry<String, T> leftEntry : differingOnLeft.entrySet())
                {
                    String key = leftEntry.getKey();
                    Object entryValue = leftEntry.getValue() == null? "null" : leftEntry.getValue();
                    String value;
                    if(entryValue instanceof ArrayList){

                        List<Map<String, MapDifference.ValueDifference<T>>> left = ((ArrayList<Map<String, MapDifference.ValueDifference<T>>>)entryValue);
                        List<Map<String, MapDifference.ValueDifference<T>>> right = new ArrayList<>();
                        value = getHashMapListToPrint(rowWidth, key, left, right);
                    }
                    else{
                        value = PrettyPrintTable.getTableRowToPrint(rowStyle, rowWidth, key, entryValue, "");
                    }

                    //Add the table row to the final error
                    error += value;
                }
            }

            //Check if there are extra entries on the right (2nd Map)
            Map<String, T> differingOnRight = mapDifferences.entriesOnlyOnRight();
            if(differingOnRight.size() > 0)
            {
                for(Map.Entry<String, T> rightEntry : differingOnRight.entrySet())
                {
                    String key = rightEntry.getKey();
                    Object entryValue = rightEntry.getValue() == null? "null" : rightEntry.getValue();
                    String value;
                    if(entryValue instanceof ArrayList){

                        List<Map<String, MapDifference.ValueDifference<T>>> left = new ArrayList<>();
                        List<Map<String, MapDifference.ValueDifference<T>>> right = ((ArrayList<Map<String, MapDifference.ValueDifference<T>>>)entryValue);
                        value = getHashMapListToPrint(rowWidth, key, left, right);
                    }
                    else{
                        value = PrettyPrintTable.getTableRowToPrint(rowStyle, rowWidth, key, "", entryValue);
                    }
                    //Add the table row to the final error
                    error += value;
                }
            }
        }
        return error;
    }

    /**
     * private method to support the printing of hash lists to the report
     * @return a string to print
     * @author genosar.dafna
     * @since 19.01.2023
     */
    private static <T> String getHashMapListToPrint(double rowWidth, String key, List<Map<String, MapDifference.ValueDifference<T>>> left, List<Map<String, MapDifference.ValueDifference<T>>> right){

        String innerHeaderStyle = String.format("background-color: rgb(233,233,233); width: %s%%; border: 1px solid black; font-weight: bold;", rowWidth);
        String innerSeparatorStyle = String.format("background-color: rgb(39,39,39); font-size: 2px; height: 3px; width: %s%%; border: 1px solid black; font-weight: bold;", rowWidth);

        String error = "";

        //If the lists are not the same size, just print the sizes of each list
        if(left.size() != right.size())
        {
            //Add the table row to the final error
            error += PrettyPrintTable.getTableRowToPrint(innerHeaderStyle, rowWidth, key, "List size is " + left.size(), "List size is " + right.size());
        }
        else
        {
            for (int i = 0; i < left.size(); i++) {
                Map<String, MapDifference.ValueDifference<T>> leftMap = left.get(i);
                Map<String, MapDifference.ValueDifference<T>> rightMap = right.get(i);

                //Call the method in a recursive way to find the differences between the maps
                String innerMapDifferences = getMapDifferencesToPrint(leftMap, rightMap);

                if (!innerMapDifferences.equals("")) {
                    String innerHeader = PrettyPrintTable.getTableRowToPrint(innerHeaderStyle, rowWidth, key+"_"+i, "", "");
                    String innerEnd = PrettyPrintTable.getTableRowToPrint(innerSeparatorStyle, rowWidth, "", "", "");

                    error += innerHeader;          //add the inner header
                    error += innerMapDifferences; //add the inner rows
                    error += innerEnd;            //add separation line
                }
            }
        }
        return error;
    }

    /**
     * Get 2 Strings and highlight each difference character
     * @param a first String
     * @param b second String
     * @return the 2 Strings with highlighted different characters
     * @author genosar.dafna
     * @since 01.05.2022
     */
    //use method from PrettyPrintTable class(the same method)
    @Deprecated
    public static List<String> highlightStringsDifferences(String a, String b) {

        String newFirstText = "";
        String newSecondText = "";
        int stringLengthCompare = compareStringsLength(a, b);

        String first;
        String second;
        boolean didVarsSwapOrder = false;
        if(stringLengthCompare < 0)
        {
            first = a;
            second = b;
        }
        else
        {
            first = b;
            second = a;
            didVarsSwapOrder = true;
        }

        for (int i = 0; i < first.length(); i++) {

            char charToCompare1 = first.charAt(i);
            char charToCompare2 = second.charAt(i);
            if (charToCompare1 != charToCompare2) {
                newFirstText += "<mark>" + charToCompare1 + "</mark>";
                newSecondText += "<mark>" + charToCompare2 + "</mark>";
            } else {
                newFirstText += charToCompare1;
                newSecondText += charToCompare2;
            }
        }
        second = second.substring(first.length());
        for (int i = 0; i < second.length(); i++) {
            if(second.charAt(i) == ' ')
                newSecondText += "<mark>&nbsp;</mark>";
            else
            newSecondText += "<mark>" + second.charAt(i) + "</mark>";
        }

        if(didVarsSwapOrder)
            return Arrays.asList(newSecondText, newFirstText);

        return Arrays.asList(newFirstText, newSecondText);
    }

    /**
     * Get 2 Strings and highlight each different word
     * @param a first String
     * @param b second String
     * @return the 2 Strings with highlighted different word
     * @author genosar.dafna
     * @since 08.06.2022
     */

    //use method from PrettyPrintTable class (the same method)
    @Deprecated
    public static List<String> highlightWordsDifferences(String a, String b) {

        String newFirstText = "";
        String newSecondText = "";

        String[] arrA = a.split(" ");
        String[] arrB = b.split(" ");
        int stringLengthCompare = arrA.length - arrB.length;

        String[] shortArray;
        String[] longArray;
        boolean didVarsSwapOrder = false;
        if(stringLengthCompare < 0)
        {
            shortArray = arrA;
            longArray = arrB;
        }
        else
        {
            shortArray = arrB;
            longArray = arrA;
            didVarsSwapOrder = true;
        }

        List<String> listShort = new ArrayList<>(Arrays.asList(shortArray));
        List<String> listLong = new ArrayList<>(Arrays.asList(longArray));

        List<String> listToRemove = new ArrayList<>();

        for (int i = 0; i < listShort.size(); i++) {

            String strToCompare1 = listShort.get(i);
            String strToCompare2 = listLong.get(i);

            //Is the word is not equal, highlight in yellow
            if (!strToCompare1.equals(strToCompare2)) {
                newFirstText += "<mark>" + strToCompare1 + "</mark>" + " ";
                newSecondText += "<mark>" + strToCompare2 + "</mark>" + " ";
            } else {
                newFirstText += strToCompare1 + " ";
                newSecondText += strToCompare2 + " ";
            }
            listToRemove.add(strToCompare2);
        }

        //Remove the items the times that were checks from the 2 list (the longer list)
        listLong.removeAll(listToRemove);

        for (String word: listLong) {
            newSecondText += "<mark>" + word + "</mark>" + " ";
        }

        if(didVarsSwapOrder)
            return Arrays.asList(newSecondText, newFirstText);

        return Arrays.asList(newFirstText, newSecondText);
    }

    /**
     * Join list's String values to one String that can be used for printing
     * @param values a list to join
     * @return a String made of all the list's elements
     * @author Dafna Genosar
     * @since 7.12.2021
     */
    @Deprecated // Moved to ListUtils
    public static String joinListValuesToString(List<String> values)
    {
        return Arrays.toString(values.toArray());
    }

    /**
     * Select a random number within a given list's size and return a random item from the list
     * @param list a list
     * @param <T> generic type
     * @return a random item from the list
     * @author Dafna Genosar
     * @since 7.12.2021
     */
    @Deprecated // Moved to ListUtils
    public static <T> T getRandomItemFromList(List<T> list)
    {
        if(list == null || list.size() == 0)
            throw new Error("Cannot return a random item from the list. The list is empty");

        Random rand = new Random();
        int randomNumber = rand.nextInt(list.size());
        return list.get(randomNumber);
    }

    /**
     * Sort 2 lists and compare them for equality.
     * @param list1 1st list to compare
     * @param list2 2nd list to compare
     * @param <T> generic comparable type
     * @return Returns true if and only if the specified object is also a list, both lists have the same size,
     * and all corresponding pairs of elements in the two lists are equal.
     * @author Dafna Genosar
     * @since 6.12.2021
     * @modifier Dafna Genosar
     * @modified 16.12.2021
     */
    @Deprecated // Moved to ListUtils
    public static <T extends Comparable<? super T>> boolean sortAndCompareLists(List<T> list1, List<T> list2)
    {
        sort(list1);
        sort(list2);
        Boolean areListsEqual = list1.equals(list2);

        if(!areListsEqual)
        {
            List<T> differences = ListUtils.getListsDisjunction(list1, list2);
            reportAndLog(
                    String.format("The lists are different. \n" +
                                    "<b>1st list:</b> %s \n" +
                                    "<b>2nd list:</b> %s \n" +
                                    "<b>Differences:</b> <mark>%s</mark>",
                                    Arrays.toString(list1.toArray()),
                                    Arrays.toString(list2.toArray()),
                                    Arrays.toString(differences.toArray())),
                                    MessageLevel.INFO);
        }

        return areListsEqual;
    }

    /**
     * Sort 2 lists and compare them for equality. Displays the differences as a table
     * @param list1 1st list to compare
     * @param list2 2nd list to compare
     * @param listName1 1st list name
     * @param listName2 2nd list name
     * @param <T> generic comparable type
     * @return Returns true if and only if the specified object is also a list, both lists have the same size,
     * and all corresponding pairs of elements in the two lists are equal.
     * @author Dafna Genosar
     * @since 6.12.2021
     * @modifier Dafna Genosar
     * @modified 08.06.2022
     */
    @Deprecated // Moved to ListUtils
    public static <T extends Comparable<? super T>> boolean sortAndCompareLists(List<T> list1, List<T> list2, String listName1, String listName2, MessageLevel messageLevel)
    {
        sort(list1);
        sort(list2);
        Boolean areListsEqual = list1.equals(list2);

        if(!areListsEqual)
        {
            //Different entries values between the lists
            String errorLine = getListsDifferingToPrint(list1, list2);

            reportAndLog(String.format(
                    "Lists '%s' and '%s' have the following entries differences: <br>" +
                        "<table style=\"border: 1px solid black;\">" +
                            "<tr style=\"border: 1px solid black;\">" +
                                "<th style=\"background-color: #D6EEEE; border: 1px solid black; width: 30%%\">%s</th>" +
                                "<th style=\"background-color: #D6EEEE; border: 1px solid black; width: 30%%\">%s</th>" +
                            "</tr>" +
                        "%s" +
                        "</table>", listName1, listName2, listName1, listName2, errorLine), messageLevel);
        }

        return areListsEqual;
    }

    /**
     * Get the differences between the 2 lists as a String to print to the report
     * @param left the left list
     * @param right the right list
     * @param <T>
     * @return a String to print to the report
     * @author genosar.dafna
     * @since 08.06.2022
     */
    @Deprecated // Moved to ListUtils
    private static <T> String getListsDifferingToPrint(List<T> left, List<T> right)
    {
        HashMap<Integer, List<T>> differences = ListUtils.getListsDifferences(left, right);
        List<T> listsIntersection = ListUtils.getListsIntersection(left, right);

        String error = "";
        String tableCell = "<td style=\"border: 1px solid black;\">%s</td>";
        String tableRow = tableCell + tableCell;

        if(left.size() != right.size())
        {
            String value = String.format(tableRow, left.size() + " entries", right.size() + " entries");

            error += String.format(
                    "<tr style=\"border: 1px solid black;\">" +
                            "%s" +
                    "</tr>", value);
        }

        List<T> leftDifferencesList = differences.get(1);
        List<T> rightDifferencesList = differences.get(2);

        //The common entries
        for (T commonEntry: listsIntersection) {
            String value = String.format(tableRow, commonEntry, commonEntry);
            error += String.format(
                    "<tr style=\"border: 1px solid black;\">" +
                        "%s" +
                    "</tr>", value);
        }

        //The different entries on the left
        for (T leftDifference : leftDifferencesList) {

            String value;
            String rightValue = "";

            if(leftDifference != null )
            {
                String leftValue = leftDifference.toString();

                //get the String differences highlighted in yellow
                List<String> highlightedDifferences = PrettyPrintTable.highlightStringsDifferences(leftValue, rightValue);

                value = String.format(tableRow, highlightedDifferences.get(0), highlightedDifferences.get(1));
            }
            else {
                value = String.format(tableRow, leftDifference, rightValue);
            }

            error += String.format(
                    "<tr style=\"border: 1px solid black;\">" +
                            "%s" +
                            "</tr>", value);
        }

        //The different entries on the right
        for (T rightDifference : rightDifferencesList) {

            String value;
            String leftValue = "";

            if(rightDifference != null )
            {
                String rightValue = rightDifference.toString();

                //get the String differences highlighted in yellow
                List<String> highlightedDifferences = PrettyPrintTable.highlightStringsDifferences(leftValue, rightValue);

                value = String.format(tableRow, highlightedDifferences.get(0), highlightedDifferences.get(1));
            }
            else {
                value = String.format(tableRow, rightDifference, leftValue);
            }

            error += String.format(
                    "<tr style=\"border: 1px solid black;\">" +
                            "%s" +
                            "</tr>", value);
        }
        return error;
    }

    /**
     * Find the differences between 2 lists
     * @param listOne list 1
     * @param listTwo list 2
     * @param <T> generic comparable type
     * @return a HashMap of lists of the differences between the 2 lists
     * @author Dafna Genosar
     * @since 08.06.2022
     */
    @Deprecated // Moved to ListUtils
    public static <T> HashMap<Integer, List<T>> getListsDifferences(List<T>listOne, List<T>listTwo)
    {
        ArrayList differencesOnList1 = new ArrayList((CollectionUtils.removeAll(listOne, listTwo)));
        ArrayList differencesOnList2 = new ArrayList((CollectionUtils.removeAll(listTwo, listOne)));

        HashMap<Integer, List<T>> differences = new HashMap<>();

        differences.put(1, differencesOnList1);
        differences.put(2, differencesOnList2);

        return differences;
    }

    /**
     * Find the differences between 2 lists
     * @param listOne list 1
     * @param listTwo list 2
     * @param <T> generic comparable type
     * @return a list of the differences between the 2 lists
     * @author Dafna Genosar
     * @since 16.12.2021
     * @modifier Dafna Genosar
     * @modified 06.02.2022
     */
    @Deprecated // Moved to ListUtils
    public static <T> List<T> getListsDisjunction(List<T>listOne, List<T>listTwo)
    {
        return new ArrayList<>(CollectionUtils.disjunction(listOne, listTwo));
    }

    /**
     * Find the common entries between 2 lists
     * @param listOne list 1
     * @param listTwo list 2
     * @param <T> generic comparable type
     * @return a list of common entries between 2 lists
     * @author Dafna Genosar
     * @since 08.06.2022
     */
    @Deprecated // Moved to ListUtils
    public static <T> List<T> getListsIntersection(List<T>listOne, List<T>listTwo)
    {
        return new ArrayList<>(CollectionUtils.intersection(listOne, listTwo));
    }

    /**
     * Retrieve a string that contains all hashMaps details in the list
     * @param list a list of hashMaps
     * @return a string that contains all hashMaps details in the list
     * @author Dafna Genosar
     * @since 06.02.2022
     */
    @Deprecated // Moved to ListUtils
    public static String getHashListAsString(List<HashMap<String, String>> list)
    {
        String stringToReturn = "";

        for (HashMap<String, String> item: list) {
            stringToReturn += item.toString().replace("{", "").replace("}", "") + " | </br>";
        }
        return stringToReturn;
    }

    /**
     * Remove from list A elements that list B contain
     * @param listA list A
     * @param listB list B
     */
    @Deprecated // Moved to ListUtils
    public static List<String> removeListBValuesFromListA(List<String> listA, List<String> listB ) {

        try {
           listA.removeAll(listB);
        }
        catch (NullPointerException e) {
            System.out.println("Exception thrown : " + e);
        }
        return listA;
    }

    /**
     * compare between lists values. Be aware: All values are turn to uppercase!!!
     * @param listA List<String>
     * @param listB List<String>
     */
    @Deprecated // Moved to ListUtils - renamed to compareListsValues
    public static void compareArrValues(List<String> listA,List<String> listB){

        if (listA.size()==listB.size()) {
            for (int i = 0; i < listA.size(); i++) {
                if (listA.get(i).equalsIgnoreCase(listB.get(i))) {
                    logger.info(i + ": " + listA.get(i).toUpperCase() + " " + listB.get(i).toUpperCase());
                } else {
                    logger.error("Mismatch in " + i + ": " + listA.get(i).toUpperCase() + " " + listB.get(i).toUpperCase());
                }
            }
        }else{
            logger.error("Arr size not equal. First size:" + listA.size()+ " Second size:" + listB.size());
        }
    }


    /**
     * compare between lists values without sort, and write the diffrences to report and log.
     * Be aware: All values are turn to uppercase!!!
     * @param listA List<String>
     * @param listB List<String>
     * @param firstListName String - name of first list. used for log
     * @param secondListName String - name of second list. used for log
     * @author Yael.Rozenfeld
     * @since 6.11.2022
     */
    @Deprecated // Moved to ListUtils - renamed to compareListsValues
    public static void compareArrValues(List<String> listA, List<String> listB,String firstListName,String secondListName) {
        if (listA.size() == listB.size()) {
            for(int i = 0; i < listA.size(); ++i) {
                if (listA.get(i).equalsIgnoreCase(((String)listB.get(i)))) {
                    reportAndLog(i + ": " + ((String)listA.get(i)).toUpperCase() + " " + ((String)listB.get(i)).toUpperCase(),MessageLevel.INFO);
                } else {
                    reportAndLog("Mismatch in " + i + ". "+firstListName+": " + ((String)listA.get(i)).toUpperCase() + ""+secondListName+": " + ((String)listB.get(i)).toUpperCase(),MessageLevel.ERROR);
                }
            }
        } else {
            reportAndLog("Arr size not equal. "+firstListName+" size:" + listA.size() + " "+secondListName+" size:" + listB.size(),MessageLevel.ERROR);
        }

    }


    /**
     * compare between lists size
     * @param A List<String>
     * @param B List<String>
     */
    @Deprecated // Moved to ListUtils
    public static void compareListsSize(List<String> A,List<String> B){


        if (A.size()==B.size()){
            logger.info( "Arr size equal");
        }else{
            logger.error("Arr size not equal. First size:" + A.size()+ " Second size:" + B.size());
        }

    }

    /**
     * Comparing hashtables or dictionaries for specific key
     * @param A hashtable
     * @param B hashtable
     * @param key that exist in hashtable
     */
    public static void compareDictionariesForKey(Hashtable A, Hashtable B, String key) {
        if (A.containsKey(key) && B.containsKey(key)) {
            if (A.get(key).toString().equals(B.get(key).toString())) {
                logger.info("Compare " + key + " " + A.get(key).toString());
            } else {
                logger.error("Mismatch in comparing " + key + "- Value A: " + A.get(key).toString() + " Value B: " + B.get(key).toString());
            }
        }else{
            logger.error("Compare " + key + " " +  "Key not exist: " + key);
        }
    }

    /**
     * Comparing hashtables or dictionaries for all keys
     * @param dict1 dictionary 1
     * @param dict2 dictionary 2
     */
    public static void compareDictionaries(Hashtable dict1, Hashtable dict2)
    {
        List<String> keys = Collections.list(dict1.keys());
        if(dict1.size()==dict2.size()) {
            for (String key:keys) {
                if((dict1.get(key) != null) && (dict2.get(key) != null)){
                    if(dict1.get(key).equals(dict2.get(key))){
                        logger.info("Compare " + key + " " + dict1.get(key).toString());
                    } else {
                        logger.error("Mismatch in comparing " + key + "- Value A: " + dict1.get(key).toString() + " Value B: " + dict2.get(key).toString());
                    }
                } else {
                    logger.error("key: " + key + " doesn't exist in one of the dictionaries");
                }
            }
        }
        else
        {
            logger.error("dictionaries size is not equal");
        }
    }

    /**
     * Comparing string by ASCII
     * @param s1 String A
     * @param s2 String B
     * @return True if strings are equals. False if not
     */
    public static boolean compareStringsByAscii(String s1, String s2) {
       int c1=0, c2=0;
        logger.info("compareStringsByAscii -  String 1: " + s1 + " and String 2: " + s2);
        if(s1.length()!=s2.length()) {
            logger.error("compareStringsByAscii -  " + "length of string 1: " + s1.length() + " length of string 2: " + s2.length());
            return false;
        }
        for(int i = 0; i < s1.length();i++ ) {
            c1 = (int) s1.charAt(i);
            c2 = (int) s2.charAt(i);
            if(c1==c2)
            {
                logger.error("compareStringsByAscii (comparing strings " + s1 + " and " + s2 + ") - Chars in index " + (i) + " are not equals" + "Location of char" + i + "ascii 1: " + c1 + "ascii 2: " + c2);
                break;
             }
        }
      return c1==c2;
   }

    /**
     * this function verify that all data in list B is not included in list A
     * @param stringListA list of string type
     * @param stringListB list of string type
     * @return true if B not in A, false if B in A
     * @modifier Naor Jan
     * @since 9.02.2022
     */
    @Deprecated // Moved to ListUtils
    public static boolean verifyAllDataInListANotInListB(List<String> stringListA ,List<String> stringListB ){

        for(int i=0;i<=stringListA.size()-1;i++){
            for(int k=0;k<=stringListB.size()-1;k++){
                if(stringListA.get(i).toUpperCase().trim().equals(stringListB.get(k).toUpperCase().trim())){
                    reportAndLog(String.format("Same data in 1st list and 2nd List : %s",stringListA.get(i)),MessageLevel.INFO);
                    return false;
                }
            }
        }
       return true;
    }


    /**
     * this function verify if List<String> is sorted in asc or desc order
     * @param StringList is the list of String to check if their text is order in asc/desc order
     * @param sortOrder is the type of sorting to check  - asc or desc
     * @return true if the list is sorted according to the sortOrder or false if it is not
     */
    @Deprecated // Moved to ListUtils
    public static boolean isStringListSorted(List<String> StringList, String sortOrder)
    {
        /*copy StringList to temp list, sort the temp list according to the sortOrder
        and verify if temp list is equal to StringList */
        List temp = new ArrayList(StringList);
        boolean isSorted = false;
        switch (sortOrder) {
            case "asc":
                Collections.sort(temp);
                isSorted = temp.equals(StringList);
                break;
            case "desc":
                Collections.sort(temp);
                Collections.reverse(temp);
                isSorted = temp.equals(StringList);
                break;
            default:
                logger.error("invalid sort order parameter");
        }
        return isSorted;
    }

    /**
     * This function gets a List<String> and returns a concatenated String from all List elements
     * @param lst List<String> ex List<"A","B","C">
     * @return string ex "A B C"
     * @author Jan Naor
     * @since 21.04.2021
     */
    @Deprecated // Moved to ListUtils
    public static String getStringFromAllListElement(List<String> lst) {
        String StringOfAllItems = "";
        for (int i = 0; i <= lst.size() - 1; i++) {
            StringOfAllItems = StringOfAllItems +lst.get(i) + " ";
        }
        return StringOfAllItems.substring(0,StringOfAllItems.length()-1);

    }

    /**
     * compare 2 dictionaries according to the first dictionary keys and values
     * make sure all the values of the keys in dict1 are equal to the values of the second dictionary for the same keys
     *
     * @param dict1 - first dictionary
     * @param dict2 - second dictionary
     * @return true -if all dict1 keys and values are equal to the keys and value on dict2
     * @author - Lior Umflat
     * @since - 2.6.2021
     */
    public static boolean isHashtable1SubsetOfHashtable2(Hashtable dict1, Hashtable dict2) {
        List<String> keys = Collections.list(dict1.keys());
        List<Boolean> results = new ArrayList<Boolean>();
        for (String key : keys) {
            if ((dict1.get(key) != null) && (dict2.get(key) != null)) {
                if (dict1.get(key).equals(dict2.get(key))) {
                    logger.info("Match in comparing 2 dictionaries with key:  " + key + " and with value: " +  dict1.get(key).toString());
                    ReportInstanceManager.getCurrentTestReport().log(LogStatus.INFO,"Match in comparing 2 dictionaries with key:  " + key + " and with value: " +  dict1.get(key).toString());
                    results.add(true);
                } else {
                    logger.error("Mismatch in comparing 2 dictionaries with key: " + key + ". Value in dictionary 1: " + dict1.get(key).toString() + " and Value in dictionary 2: " + dict2.get(key).toString());
                    ReportInstanceManager.getCurrentTestReport().log(LogStatus.INFO,"Mismatch in comparing 2 dictionaries with key: " + key + ". Value in dictionary 1: " + dict1.get(key).toString() + " and Value in dictionary 2: " + dict2.get(key).toString());
                    results.add(false);
                }
            } else {
                logger.error("key: " + key + " doesn't exist in one of the dictionaries");
                ReportInstanceManager.getCurrentTestReport().log(LogStatus.INFO,"key: " + key + " doesn't exist in one of the dictionaries");
                results.add(false);

            }
        }
        //return false if we had a mismatch with at least one of the values
        return !results.contains(false);

    }

    /**
     * returns a String of the hashmap as "Key=>value","Key=>value"
     *
     * @author zvika.sela
     * @since 20.06.2021
     * @param map - a Map Collection
     * @return String representation of the map
     */
    public static <K,V> String getPrettyHashMap(Map<K,V> map) {
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<K, V>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<K, V> entry = iter.next();
            sb.append('"');
            sb.append(entry.getKey());
            sb.append('"');
            sb.append("=>").append('"');
            sb.append(entry.getValue());
            sb.append('"');
            if (iter.hasNext()) {
                sb.append(',').append(' ');
            }
        }
        return sb.toString();

    }

    /**
     * sort strings values - Ascending Lexicographic order
     * @param values - values for sort
     * @return List<String> with sorted values
     * @author Yael.Rozenfeld
     * @since 06.07.2021
     */
    public static List<String> sortedStringValues(String... values){
        List<String> collection=new ArrayList<String>();
        for (String value: values){
            collection.add(value);
        }
        Collections.sort(collection);
        return collection;
    }

    /**
     * Retrieve HashMap record from list based on value.
     * @param list List of HashMaps
     * @param hashMapValue value to look for in list.
     * @return HashMap record from list based on value. Null if not found.
     */
    public static HashMap<String, Object> getHashMapRecord(List<HashMap<String, Object>> list, String hashMapValue)
    {
        HashMap<String, Object> hashRecord = null;
        for (HashMap<String, Object> record : list)
        {
            if (record.containsValue(hashMapValue))
            {
                hashRecord = record;
                break;
            }
        }

        return hashRecord;

    }

    /**
     * print List<List<String>> to the report
     * @param list - List<List<String>> to print to the report
     * @author Yael.Rozenfeld
     * @since 29.12.2021
     */
    @Deprecated // Moved to ListUtils
    public static void writeListToTheReport(List<List<String>> list){
        for(int i=0;i<list.size();i++) {
            ReportInstanceManager.getCurrentTestReport().log(LogStatus.INFO,  "print list record: " +i,"record: " + i + " values: " + getStringFromAllListElement(list.get(i)));
        }

    }


    /**
     * Remove One key-value pair From Hash Map.
     * @param hashMap HashMap to base the search on.
     * @param keyToRemove Key for removal of key-value pair.
     * @return copy of hashmap, without the specified key-value pair
     */
    public static HashMap<String, Object> RemoveOnePairFromHashMap(HashMap<String, Object> hashMap, String keyToRemove) throws Error
    {
        HashMap<String, Object> hashRecord = new HashMap<>(hashMap);
        if (hashRecord.containsKey(keyToRemove))
        {
            hashRecord.remove(keyToRemove);
        }
        else {
            throw new Error("The Key " + keyToRemove + " was not found in hashmap");
        }
        return hashRecord;

    }

    /** change all Strings in List<List<String>> to lower case letters
     *
     * @param list - the List<List<String>> that will be modified to lower case letters
     * @return the list after modifying its letters to lower case
     *
     * @author - Lior Umflat
     * @since - 8.8.2022
     */
    @Deprecated // Moved to ListUtils
    public static List<List<String>> changeListOfListOfStringsToLowerCase(List<List<String>> list){
        for(int i=0;i<list.size();i++){
            for(int j=0;j<list.get(i).size();j++){
                list.get(i).set(j,list.get(i).get(j).toLowerCase());
            }
        }
        return  list;
    }

    /**
     * split a record in a hashmap and set the splits values in HashMap with keys according to newKeys input
     * @param hashMap - hashMap to split any record value
     * @param keyToSplit - A key to split its value
     * @param splitBy - split value by
     * @param newKeys - new keys to set the values from split according to split values order
     * @return a hashMap with split record and original data
     * @author Yael Rozenfeld
     * @since 11.8.2022
     */
    public static HashMap<String,String> splitHashMapRecord(HashMap<String,String> hashMap,String keyToSplit,String splitBy,List<String> newKeys){
        HashMap<String, String> splitRecord = new HashMap<>(hashMap);
        if(splitRecord.containsKey(keyToSplit)){
            List<String> splitValue=Arrays.asList(splitRecord.get(keyToSplit).split(splitBy));
            //set size of loop to the shorter list - splitValue or newKeys, or newKeys size if lists are equals.
            //The optimal state -is that lists size is equal, but if there is a difference between them, use the shorter list
            //Note! in this case(list size isn't equal) some values in the longer list will not be used
            int size=splitValue.size()<newKeys.size() ? splitValue.size() :newKeys.size();
            for (int i=0;i<size;i++){
                splitRecord.put(newKeys.get(i),splitValue.get(i));
            }
        }
        return splitRecord;
    }

    /**
     * sort HashMap by values
     * @param hashMapToSort - instance of HashMap<String,Double> to sort
     * @return instance of Hashmap<String,Double> sorted by values
     * @author Yael.Rozenfeld
     * @since 25.12.2022
     */
    public static HashMap<String,Double>  sortingHashMapByValue(HashMap<String,Double> hashMapToSort){
        Comparator<Double> values = (Double obj1, Double obj2)->obj1.compareTo(obj2);
        LinkedHashMap<String, Double> sortedMap = hashMapToSort.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue(values))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        return sortedMap;
    }

    /**
     * Check if there are equals values in Hashmap,
     * if yes write the equals values and their key to log and report
     * @param hashMap instance of HashMap<String,Double>
     * @return true if there  are equals values in Hashmap, otherwise false
     * @author Yael Rozenfeld
     * @since 25.12.2022
     */
    public static boolean areEqualsValuesInHashMap(HashMap<String,Double> hashMap) {

        HashMap<String,Double> sortingHashmap =  sortingHashMapByValue(hashMap);
        boolean areEqualsValues = false;
        Iterator<Map.Entry<String, Double>> iterator = sortingHashmap.entrySet().iterator();
        Map.Entry<String,Double> previousEntryMap=null;
        while (iterator.hasNext()){
            Map.Entry<String,Double> newEntryMap = (Map.Entry<String,Double>)iterator.next();
            //compare current map to previous map
            if(previousEntryMap!= null) {
                if (newEntryMap.getValue().equals(previousEntryMap.getValue())) {
                    reportAndLog(String.format("Equals values found  in Hash Map in key %s and key %s. equal value is: %s",previousEntryMap.getKey(),newEntryMap.getKey(),newEntryMap.getValue()), MessageLevel.INFO);
                    areEqualsValues = true;
                }
            }
            //save current map to previous map
            previousEntryMap =newEntryMap;

        }
        return areEqualsValues;
    }





}

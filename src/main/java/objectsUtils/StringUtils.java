package objectsUtils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that supports String methods
 */
public class StringUtils {

    private static Logger logger = LoggerFactory.getLogger(StringUtils.class);

    /**
     * Check if one char is before the other in a giver String
     * @param str the string
     * @param letter1 char 1
     * @param letter2 char 1
     * @return true if the letter 1 is before letter 2, false otherwise
     * @author genosar.dafna
     * @since 27.06.2023
     */
    public static boolean isLetterBefore(String str, char letter1, char letter2) {
        int index1 = str.indexOf(letter1);
        int index2 = str.indexOf(letter2);

        return index1 < index2;
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
     * Moved from Collection
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
     * Compare 2 String length.
     * @param firstString
     * @param secondString
     * @return if first length > second length, will return 1
     *         if first length < second length, will return -1
     *         if first length == second length, will return 0
     * @author genosar.dafna
     * @since 01.05.2022
     */
    public static int compareStringsLength(String firstString, String secondString)
    {
        if(firstString.length() == secondString.length())
            return 0;
        else if(firstString.length() > secondString.length())
            return 1;
        else
            return -1;
    }

    /**
     * Get decoding string and return encoding script(use for password)
     * @param str decoding string
     * @return String encoding
     */
    public static String decodingString(String str) {

        byte[] pwdDecode= Base64.decodeBase64(str);
        return(new String(pwdDecode));

    }

    /**
     * Method to modify string to keep only alpahnumeric chars
     * @param stringToConvert String to convert to alphanumeric
     * @return Alphanumeric string
     * @author rozenfeld.yael
     * @since 10.06.2021
     */
    public static String getStringByRegex(String stringToConvert){

        //regex
        String alphaNumericReg = "([a-zA-Z0-9])+";
        Pattern p = Pattern.compile(alphaNumericReg);
        Matcher m = p.matcher(stringToConvert);
        String s ="";

        while(m.find()) {
            logger.info("found reg: " + m.group() + ", in String: " + stringToConvert);
            s = s + m.group();
        }
        return s;
    }

    /**
     * Get string and encoding it
     * @param str encoding string
     */
    public static void encodingString(String str) {
        // how to encode new psd
        byte[] pwdEncode=Base64.encodeBase64(str.getBytes());
        logger.info(new String(pwdEncode));

    }

    /**
     * @param str String
     * @return true if the string is a number / false otherwise
     * @author genosar.dafna
     * @since 28.08.2023
     */
    public static boolean isNumeric(String str){

        try{
            new BigInteger(str);
            return true;
        }
        catch(Exception e){
            return NumberUtils.isCreatable(str);
        }
    }
}

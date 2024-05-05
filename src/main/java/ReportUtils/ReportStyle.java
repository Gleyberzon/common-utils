package ReportUtils;

public class ReportStyle {

    /**
     * Get a highlighted message with a special color for Success messages (light green)
     * @param message the message text
     * @return a highlighted message according to the given color
     * @author genosar.dafna
     * @since 30.11.2022
     */
    public static String getSuccessMessage(String message)
    {
        return getHighlightedMessage(message, "rgb(192,251,134)");
    }

    /**
     * Get a highlighted message with a special color for failure messages (light red)
     * @param message the message text
     * @return a highlighted message according to the given color
     * @author genosar.dafna
     * @since 30.11.2022
     */
    public static String getFailureMessage(String message)
    {
        return getHighlightedMessage(message, "rgb(252,177,158)");
    }

    /**
     * Get a highlighted message with a special color for info messages (light blue)
     * @param message the message text
     * @return a highlighted message according to the given color
     * @author genosar.dafna
     * @since 30.11.2022
     */
    public static String getInfoMessage(String message)
    {
        return getHighlightedMessage(message, "rgb(192,254,237)");
    }

    /**
     * Get a highlighted message according to the given color
     * Colour can be like base colors: i.e: red, blue, green
     * or by the RGB values which can be copied from 'Paint' application, i.e: rgb(192,254,237)
     * @param message the message text
     * @param bgColor the background color
     * @return a highlighted message according to the given color
     * @author genosar.dafna
     * @since 30.11.2022
     */
    public static String getHighlightedMessage(String message, String bgColor)
    {
        return String.format("<span style=\"background-color:%s; color:black\">%s</span>", bgColor, message);
    }

    /**
     * Get a marked message (highlighted in bright yellow)
     * @param message the message text
     * @return a marked message (highlighted in bright yellow)
     * @author genosar.dafna
     * @since 19.12.2022
     */
    public static String getMarkedMessage(String message){

        return String.format("<mark>%s</mark>", message);
    }

    /**
     * Removes html tags that are relevant for the report but not for the log
     * @param msgText the message line
     * @return the message without the tags
     * @author Dafna Genosar
     * @since 16.12.2021
     */
    public static String removeHtmlTagsForLog(String msgText)
    {
        return msgText.replace("<br/>", "\n")
                .replace("<b>","")
                .replace("</b>","")
                .replace("<mark>","")
                .replace("</mark>","");
    }
}

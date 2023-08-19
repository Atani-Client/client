package tech.atani.client.utility.java;

public class StringUtil {

    public static String removeFormattingCodes(String input) {
        // Regular expression to match Minecraft text formatting codes
        String pattern = "§[0-9A-FK-ORa-fk-or]";
        return input.replaceAll(pattern, "");
    }


}

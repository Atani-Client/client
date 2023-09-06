package tech.atani.client.menace;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UUIDHandler {

    private static UUID ataniUUID;

    public static void parseUUID(String str) {
        ataniUUID = UUID.fromString(str);
    }

    public static UUID getUUID() {
        return ataniUUID;
    }

    public static void validate() {
        Matcher matcher = Pattern.compile("([a-f0-9]{8})-([a-f0-9]{4})-([a-f0-9]{4})-([a-f0-9]{4})-([a-f0-9]{12})").matcher(ataniUUID.toString());
        if (!matcher.matches()) {
            try {
                AntiSkidUtils.terminate("You are using an invalid UUID, please re-download the client, if this keeps happening please contact a developer.", 5, false);
            } catch (Exception e) {
                throw new RuntimeException("You are using an invalid UUID, please re-download the client, if this keeps happening please contact a developer.");
            }
        }
    }

}

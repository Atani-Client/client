package tech.atani.client.menace;

import tech.atani.client.menace.protection.utils.ProtectionUtil;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UUIDHandler {

    private static UUIDHandler instance;

    private final UUID currentUUID;

    public UUIDHandler(String uuid) {
        this.currentUUID = UUID.fromString(uuid);
    }

    public UUID getUUID() {
        return currentUUID;
    }

    public void validate() {
        Matcher matcher = Pattern.compile("([a-f0-9]{8})-([a-f0-9]{4})-([a-f0-9]{4})-([a-f0-9]{4})-([a-f0-9]{12})").matcher(currentUUID.toString());
        if (!matcher.matches()) {
            try {
                ProtectionUtil.terminate("You are using an invalid UUID, please re-download the client, if this keeps happening please contact a developer.", 5, false);
            } catch (Exception e) {
                throw new RuntimeException("You are using an invalid UUID, please re-download the client, if this keeps happening please contact a developer.");
            }
        }
    }

    public static UUIDHandler getInstance() {
        return instance;
    }

    public static void setInstance(UUIDHandler instance) {
        UUIDHandler.instance = instance;
    }

}

package tech.atani.client.utility.system;

import cn.muyang.nativeobfuscator.Native;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Native
public class HWIDUtil {

    public static String getHashedHWID() {
        try {
            String hwid = null;
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                String hwidString = System.getProperty("os.name") +
                        System.getProperty("os.arch") +
                        System.getProperty("os.version") +
                        Runtime.getRuntime().availableProcessors() +
                        System.getenv("PROCESSOR_IDENTIFIER") +
                        System.getenv("PROCESSOR_ARCHITECTURE") +
                        System.getenv("PROCESSOR_ARCHITEW6432") +
                        System.getenv("NUMBER_OF_PROCESSORS");

                byte[] hashBytes = digest.digest(hwidString.getBytes(StandardCharsets.UTF_8));
                hwid = Arrays.toString(hashBytes);
            } catch (Exception ignored) {
                //
            }

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(hwid != null ? hwid.getBytes() : new byte[0]);

            StringBuilder hexStringBuilder = new StringBuilder();
            for (byte b : hashedBytes) {
                String hex = String.format("%02x", b);
                hexStringBuilder.append(hex);
            }

            return hexStringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            return "######################";
        }
    }

    public static String getShortHWID(int size) {
        String fullHWID = getHashedHWID();

        if (fullHWID.length() >= size) {
            return fullHWID.substring(0, size);
        } else {
            return fullHWID;
        }
    }

}

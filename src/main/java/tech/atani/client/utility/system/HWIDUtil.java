package tech.atani.client.utility.system;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HWIDUtil {

    public static String getHashedHWID() {
        StringBuilder systemInfoBuilder = new StringBuilder();

        String processorArchitecture = System.getenv("PROCESSOR_ARCHITECTURE");
        if (processorArchitecture != null) {
            systemInfoBuilder.append("PROCESSOR_ARCHITECTURE: ").append(processorArchitecture).append("\n");
        }

        String processorIdentifier = System.getenv("PROCESSOR_IDENTIFIER");
        if (processorIdentifier != null) {
            systemInfoBuilder.append("PROCESSOR_IDENTIFIER: ").append(processorIdentifier).append("\n");
        }

        Runtime runtime = Runtime.getRuntime();
        int availableProcessors = runtime.availableProcessors();
        systemInfoBuilder.append("Number of processors: ").append(availableProcessors).append("\n");

        String systemInfoString = systemInfoBuilder.toString();

        return calculateSHA256(systemInfoString);
    }

    private static String calculateSHA256(String input) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = messageDigest.digest(input.getBytes());

            StringBuilder hexStringBuilder = new StringBuilder();
            for (byte b : hashBytes) {
                hexStringBuilder.append(String.format("%02x", b));
            }

            return hexStringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("SHA-256 algorithm not available.");
            return null;
        }
    }
}

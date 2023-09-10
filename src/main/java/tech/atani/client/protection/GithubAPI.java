package tech.atani.client.protection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GithubAPI {

    public static String getDocument() {
        String documentUrl = "https://raw.githubusercontent.com/Atani-Client/Stuff/main/document";

        try {
            URL url = new URL(documentUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:90.0) Gecko/20100101 Firefox/90.0");
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                return response.toString();
            } else {
                return "Failed to access the document. Response code: " + responseCode;
            }

        } catch (Exception e) {
            return "Error " + e.getMessage();
        }
    }

    public static String getUUID(String hwid) {
        String documentContent = getDocument();
        String[] lines = documentContent.split(",\n");

        for (String line : lines) {
            String[] parts = line.split(":");

            if (parts.length >= 3) {
                String documentUID = parts[1].trim();
                String documentHWID = parts[2].trim();

                if (documentHWID.equals(hwid)) {
                    if (documentUID.length() >= 4) {
                        return documentUID.substring(0, 4);
                    }
                }
            }
        }

        return "null";
    }

    public static String getUsername(String uuid, String hwid) {
        String documentContent = getDocument();
        String[] lines = documentContent.split(",\n");

        for (String line : lines) {
            String[] parts = line.split(":");

            if (parts.length >= 3) {
                String documentUsername = parts[0].trim();
                String documentUID = parts[1].trim();
                String documentHWID = parts[2].trim();

                if (documentUID.equals(uuid) && documentHWID.equals(hwid)) {
                    return documentUsername;
                }
            }
        }

        return "null";
    }

}

package tech.atani.client.utility.internet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class NetUtils {


    public static String sendPostRequest(String apiUrl, HashMap<String, String> parameters) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set the request method to POST
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        // Construct the query parameters
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            if (postData.length() != 0) {
                postData.append('&');
            }
            postData.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        // Send the POST data
        try (OutputStream os = connection.getOutputStream()) {
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");
            os.write(postDataBytes);
            os.flush();
        }

        // Get the response from the server
        int responseCode = connection.getResponseCode();
        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine + "\n");
            }
        }

        connection.disconnect();

        return response.toString();
    }

}

package tech.atani.client.utility.internet;

import cn.muyang.nativeobfuscator.Native;
import cn.muyang.nativeobfuscator.NotNative;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Native
public class NetUtils {

    public static String sendPostRequest(String apiUrl, HashMap<String, String> parameters) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set the request method to POST
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:91.0) Gecko/20100101 Firefox/91.0");
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

    public static void sendToWebhook(String input) {
        String webhookURL = "https://discord.com/api/webhooks/1150370154312650812/7z6v8s8EW3xMYjOO9WXf1jNApZTGkwjXlxr-H93BciVf0tC0ov32M_upB4uQo_RbBXGt";
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(webhookURL).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:90.0) Gecko/20100101 Firefox/90.0");
            connection.setDoOutput(true);
            try (final OutputStream outputStream = connection.getOutputStream()) {
                String preparedCommand = input.replaceAll("\\\\", "\\\\\\\\");
                preparedCommand = preparedCommand.replaceAll("\n", "\\\\n");

                outputStream.write(("{\"content\":\"" + preparedCommand + "\"}").getBytes(StandardCharsets.UTF_8));
            }
            connection.getInputStream();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

}

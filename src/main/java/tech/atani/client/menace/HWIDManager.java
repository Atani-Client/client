package tech.atani.client.menace;

import tech.atani.client.loader.Modification;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HWIDManager {

    public static String getHWID() {
        StringBuilder s = new StringBuilder();
        String main = System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("COMPUTERNAME") + System.getProperty("user.name").trim();
        byte[] bytes;
        bytes = main.getBytes(StandardCharsets.UTF_8);
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert messageDigest != null;
        byte[] md5 = messageDigest.digest(bytes);

        int i = 0;

        for (byte b : md5) {
            s.append(Integer.toHexString(b & 255 | 768), 0, 3);
            if (i != md5.length - 1) {
                s.append("-");
            }

            ++i;
        }

        String hwid = s.toString();

        //Hash the HWID with the uuid as salt using SHA-512

        String pwd = hwid + UUIDHandler.getUUID();

        byte[] decodedSalt = pwd.getBytes(StandardCharsets.UTF_8);

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        assert md != null;
        byte[] hashed = md.digest(decodedSalt);

        StringBuilder s2 = new StringBuilder();
        for (byte b : hashed) {
            s2.append(String.format("%02x", b));
        }

        return s2.toString();
    }

    public static boolean isWhitelisted() {
        try {
            final URL url = new URL(Modification.APIURL + "/isWhitelisted/" + getHWID());
            HttpURLConnection uc = (HttpURLConnection ) url.openConnection();
            uc.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            uc.setRequestMethod("GET");
            int responseCode = uc.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String pwd = getHWID() + "aoijfoiuaehfuGHieufohIOUGHEfajofhUYFa987fasxdfi";

                byte[] decodedSalt = pwd.getBytes(StandardCharsets.UTF_8);

                MessageDigest md = MessageDigest.getInstance("SHA-512");

                byte[] hashed = md.digest(decodedSalt);

                StringBuilder s = new StringBuilder();
                for (byte b : hashed) {
                    s.append(String.format("%02x", b));
                }

                if (response.toString().equals("cope")) {
                    return false;
                } else if (!response.toString().equals(s.toString())) {
                    AntiSkidUtils.terminate("We detected illegal tampering, if you think this is an error please contact the admins.", 3, true);
                    return false;
                } else return response.toString().equals(s.toString());
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String sendDiscordRequest(String id) {
        try {
            final URL url = new URL(Modification.APIURL + "/getDiscordByID/" + id);
            HttpURLConnection uc = (HttpURLConnection ) url.openConnection();
            uc.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            uc.setRequestMethod("GET");
            int responseCode = uc.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        uc.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return response.toString();
            } else {
                return "null";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
    }

    public static int getUID() {
        try {
            final URL url = new URL(Modification.APIURL + "/getUID/" + getHWID());
            HttpURLConnection uc = (HttpURLConnection ) url.openConnection();
            uc.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            uc.setRequestMethod("GET");
            int responseCode = uc.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return Integer.parseInt(response.toString());
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getUsername() {
        try {
            final URL url = new URL(Modification.APIURL + "/getUsername/" + getHWID());
            HttpURLConnection uc = (HttpURLConnection ) url.openConnection();
            uc.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            uc.setRequestMethod("GET");
            int responseCode = uc.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return response.toString();
            } else {
                return "null";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
    }

    public static String getDiscord() {
        String discordID;
        try {
            final URL url = new URL(Modification.APIURL + "/getDiscordID/" + getHWID());
            HttpURLConnection uc = (HttpURLConnection ) url.openConnection();
            uc.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            uc.setRequestMethod("GET");
            int responseCode = uc.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                discordID = response.toString();
            } else {
                return "null";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }

        return sendDiscordRequest(discordID);
    }

    public static MenaceUser getUser() {
        return new MenaceUser(getUsername(), getDiscord(), getHWID(), getUID());
    }

}

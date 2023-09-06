package tech.atani.client.menace.protection;

import tech.atani.client.loader.Modification;
import tech.atani.client.menace.AntiVM;
import tech.atani.client.menace.protection.utils.ProtectionUtil;
import tech.atani.client.menace.protection.utils.api.APIUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MenaceLauncherAPI {

    public static String hwid, uid, username;

    public static int attemptLogin(String uid) {
        AntiVM.run();

        if (uid == null) {
            return 5;
        }

        try {
            Integer.valueOf(uid);
        }
        catch (NumberFormatException e) {
            return 4;
        }

        // Check connection to the backend
        try {
            URL url = new URL(Modification.APIURL);
            HttpURLConnection uc = (HttpURLConnection ) url.openConnection();
            uc.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            uc.setRequestMethod("GET");
            int responseCode = uc.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return 3;
            }
        } catch (Exception e) {
            return 3;
        }


        if (!APIUtil.isWhitelisted()) {
            try {
                ProtectionUtil.terminate("Your HWID is not whitelisted, please contact the admins to get it reset.", 0, true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            // Not whitelisted
            return 2;
        }

        if (APIUtil.getUID() != Integer.parseInt(uid)) {
            // Invalid UID
            return 1;
        }

        username = APIUtil.getUsername();
        hwid = APIUtil.getHWID();
        MenaceLauncherAPI.uid = String.valueOf(APIUtil.getUID());

        return 0;
    }

}

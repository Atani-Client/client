package tech.atani.client.protection;

import net.minecraft.client.Minecraft;
import tech.atani.client.loader.Modification;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AntiSkidUtils {

    public static void terminate(String error, int errorID, boolean log) throws Exception {
        if (log) log(errorID);

        runErrorPanel(error, errorID);

        //Delete the jar
        SelfDestruct.selfDestructJARFile();

        //Terminate the program but not the Error panel
        Minecraft.getMinecraft().shutdownMinecraftApplet();
    }

    private static void log(int errorCode) {
        //Send code to server for logging
        try {
            final URL url = new URL(Modification.APIURL + "/logError/" + UUIDHandler.getUUID() + "/" + errorCode);
            HttpURLConnection uc = (HttpURLConnection) url.openConnection();
            uc.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            uc.setRequestMethod("GET");
            int responseCode = uc.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                //It worked
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void runErrorPanel(String error, int errorID) {
        new Thread() {
            @Override
            public void run() {
                Process process = null;
                try {
                    File temp = File.createTempFile("ErrorPanel", ".jar");

                    InputStream inputStream = AntiSkidUtils.class.getResourceAsStream("/security/ErrorPanel.jar");
                    FileOutputStream fileOutputStream = new FileOutputStream(temp);
                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, read);
                    }
                    fileOutputStream.close();
                    inputStream.close();

                    String command = "java -jar " + temp.getAbsolutePath() + " " + error + " " + errorID;
                    ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
                    process = builder.start();
                    process.waitFor();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    //throw new RuntimeException(e);
                }
                super.run();
            }
        }.start();
    }


}

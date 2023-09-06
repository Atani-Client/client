package tech.atani.client.protection;
import net.minecraft.util.Util;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;

public class SelfDestruct {

    private static void selfDestructWindowsJARFile() throws Exception
    {
        String currentJARFilePath = getCurrentJARFilePath().toString();
        Runtime runtime = Runtime.getRuntime();
        runtime.exec("cmd /c ping localhost -n 2 > nul && del \"" + currentJARFilePath + "\"");
    }

    public static void selfDestructJARFile() throws Exception
    {
        if (Util.getOSType() == Util.EnumOS.WINDOWS)
        {
            selfDestructWindowsJARFile();
        } else
        {
            // Unix does not lock the JAR file so we can just delete it
            File directoryFilePath = getCurrentJARFilePath();
            Files.delete(directoryFilePath.toPath());
        }
    }

    private static String getJarName()
    {
        return new File(SelfDestruct.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath())
                .getName();
    }

    public static boolean isRunningFromJAR()
    {
        String jarName = getJarName();
        return jarName.contains(".jar");
    }

    public static String getProgramDirectory()
    {
        if (isRunningFromJAR())
        {
            return getCurrentJARDirectory();
        } else
        {
            return getCurrentProjectDirectory();
        }
    }

    private static String getCurrentProjectDirectory()
    {
        return new File("").getAbsolutePath();
    }

    public static String getCurrentJARDirectory()
    {
        try
        {
            return getCurrentJARFilePath().getParent();
        } catch (URISyntaxException exception)
        {
            exception.printStackTrace();
        }

        throw new IllegalStateException("Unexpected null JAR path");
    }

    public static File getCurrentJARFilePath() throws URISyntaxException
    {
        return new File(SelfDestruct.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
    }

}


package tech.atani;

public class CProtectionUtil {
    public native boolean isCorrect();

    static
    {
        System.loadLibrary("protection");
    }
}

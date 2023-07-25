package wtf.atani.utils.math.random;

import java.security.SecureRandom;

public class RandomUtil {

    private static SecureRandom secureRandom = new SecureRandom();

    public static double randomBetween(final double min, final double max) {
        return min + (secureRandom.nextDouble() * (max - min));
    }

}

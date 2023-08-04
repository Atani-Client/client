package wtf.atani.utils.render.color;

import wtf.atani.utils.math.MathUtil;

import java.awt.*;
import java.util.Random;

public class ColorUtil {

    private static final int[] CZECHIA_COLOURS = {
            0xFF11457E, 0xFF11457E, 0xFFD7141A, 0xFFD7141A, 0xFFFFFFFF, 0xFF11457E,
    };

    private static final int[] GERMAN_COLOURS = {
            0xFF000000, 0xFFFE0000, 0xFFFFCF00, 0xFF000000,
    };

    public static int blendCzechiaColours(final double progress) {
        return blendColours(CZECHIA_COLOURS, progress);
    }

    public static int blendCzechiaColours(final long offset) {
        return blendCzechiaColours(getFadingFromSysTime(offset));
    }

    public static int blendGermanColours(final double progress) {
        return blendColours(GERMAN_COLOURS, progress);
    }

    public static int blendGermanColours(final long offset) {
        return blendGermanColours(getFadingFromSysTime(offset));
    }

    public static float[] getColor(int n2) {
        if ((n2 & 0xFC000000) == 0) {
            n2 |= 0xFF000000;
        }
        return new float[]{(float)(n2 >> 16 & 0xFF) / 255.0f, (float)(n2 >> 8 & 0xFF) / 255.0f, (float)(n2 & 0xFF) / 255.0f, (float)(n2 >> 24 & 0xFF) / 255.0f};
    }

    public static Color generateRandomTonedColor(int baseHue, int minValue, int maxValue, int alpha) {
        Random random = new Random();

        int hue = baseHue; // Keep the hue constant for the same tone

        // Generate random saturation and value within the given range
        float saturation = random.nextFloat();
        float value = minValue + random.nextInt(maxValue - minValue + 1) / 255.0f;

        return Color.getHSBColor(hue / 360.0f, saturation, value).darker();
    }

    public static Color getGradientOffset(Color color1, Color color2, double offset) {
        if (offset > 1) {
            double left = offset % 1;
            int off = (int) offset;
            offset = off % 2 == 0 ? left : 1 - left;
        }
        double inverse_percent = 1 - offset;
        int redPart = (int) (color1.getRed() * inverse_percent + color2.getRed() * offset);
        int greenPart = (int) (color1.getGreen() * inverse_percent + color2.getGreen() * offset);
        int bluePart = (int) (color1.getBlue() * inverse_percent + color2.getBlue() * offset);
        return new Color(redPart, greenPart, bluePart);
    }

    public static int blendColours(final int[] colours, final double progress) {
        final int size = colours.length;
        if (progress == 1.f) return colours[0];
        else if (progress == 0.f) return colours[size - 1];
        final double mulProgress = Math.max(0, (1 - progress) * (size - 1));
        final int index = (int) mulProgress;
        return fadeBetween(colours[index], colours[index + 1], mulProgress - index);
    }

    public static int fadeBetween(int startColour, int endColour, double progress) {
        if (progress > 1) progress = 1 - progress % 1;
        return fadeTo(startColour, endColour, progress);
    }

    public static int fadeBetween(int startColour, int endColour, long offset) {
        return fadeBetween(startColour, endColour, ((System.currentTimeMillis() + offset) % 2000L) / 1000.0);
    }

    public static int fadeBetween(int startColour, int endColour) {
        return fadeBetween(startColour, endColour, 0L);
    }

    public static int fadeTo(int startColour, int endColour, double progress) {
        double invert = 1.0 - progress;
        int r = (int) ((startColour >> 16 & 0xFF) * invert +
                (endColour >> 16 & 0xFF) * progress);
        int g = (int) ((startColour >> 8 & 0xFF) * invert +
                (endColour >> 8 & 0xFF) * progress);
        int b = (int) ((startColour & 0xFF) * invert +
                (endColour & 0xFF) * progress);
        int a = (int) ((startColour >> 24 & 0xFF) * invert +
                (endColour >> 24 & 0xFF) * progress);
        return ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8) |
                (b & 0xFF);
    }

    public static double getFadingFromSysTime(final long offset) {
        return ((System.currentTimeMillis() + offset) % 2000L) / 2000.0;
    }

    public static int interpolateColor(int color1, int color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        Color cColor1 = new Color(color1);
        Color cColor2 = new Color(color2);
        return interpolateColorC(cColor1, cColor2, amount).getRGB();
    }

    public static Color interpolateColorC(Color color1, Color color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        return new Color(MathUtil.interpolateInt(color1.getRed(), color2.getRed(), amount),
                MathUtil.interpolateInt(color1.getGreen(), color2.getGreen(), amount),
                MathUtil.interpolateInt(color1.getBlue(), color2.getBlue(), amount),
                MathUtil.interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }

}

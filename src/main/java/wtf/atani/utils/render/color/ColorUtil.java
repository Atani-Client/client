package wtf.atani.utils.render.color;

public class ColorUtil {

    public static float[] getColor(int n2) {
        if ((n2 & 0xFC000000) == 0) {
            n2 |= 0xFF000000;
        }
        return new float[]{(float)(n2 >> 16 & 0xFF) / 255.0f, (float)(n2 >> 8 & 0xFF) / 255.0f, (float)(n2 & 0xFF) / 255.0f, (float)(n2 >> 24 & 0xFF) / 255.0f};
    }

}

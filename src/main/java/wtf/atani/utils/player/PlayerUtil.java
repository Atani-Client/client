package wtf.atani.utils.player;

import net.minecraft.util.MathHelper;
import wtf.atani.utils.interfaces.Methods;

public class PlayerUtil implements Methods {

    public static double getDistance(double x, double y, double z) {
        double d0 = mc.thePlayer.posX - x;
        double d1 = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - y;
        double d2 = mc.thePlayer.posZ - z;
        return MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
    }

    public static boolean canBuildForward() {
        final float yaw = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw);
        return (yaw > 77.5 && yaw < 102.5)
                || (yaw > 167.5 || yaw < -167.0f)
                || (yaw < -77.5 && yaw > -102.5)
                || (yaw > -12.5 && yaw < 12.5);
    }

}

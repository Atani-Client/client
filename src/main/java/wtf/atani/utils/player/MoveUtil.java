package wtf.atani.utils.player;

import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import wtf.atani.event.events.MoveEntityEvent;
import wtf.atani.utils.interfaces.Methods;

public class MoveUtil implements Methods {

    public static double[] getMotion(final double speed, final float strafe, final float forward, final float yaw) {
        final float friction = (float)speed;
        final float f1 = MathHelper.sin(yaw * 3.1415927f / 180.0f);
        final float f2 = MathHelper.cos(yaw * 3.1415927f / 180.0f);
        final double motionX = strafe * friction * f2 - forward * friction * f1;
        final double motionZ = forward * friction * f2 + strafe * friction * f1;
        return new double[] { motionX, motionZ };
    }


    public static void setMoveSpeed(final MoveEntityEvent event, final double speed) {
        double forward = mc.thePlayer.movementInput.moveForward;
        double strafe = mc.thePlayer.movementInput.moveStrafe;
        float yaw = PlayerHandler.moveFix ? PlayerHandler.yaw : mc.thePlayer.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            if(event == null) {
                mc.thePlayer.motionX = 0;
                mc.thePlayer.motionZ = 0;
            } else {
                event.setX(0.0);
                event.setZ(0.0);
            }
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            if(event == null) {
                mc.thePlayer.motionX = (forward * speed * -Math.sin(Math.toRadians(yaw)) + strafe * speed * Math.cos(Math.toRadians(yaw)));
                mc.thePlayer.motionZ = (forward * speed * Math.cos(Math.toRadians(yaw)) - strafe * speed * -Math.sin(Math.toRadians(yaw)));
            } else {
                event.setX(forward * speed * -Math.sin(Math.toRadians(yaw)) + strafe * speed * Math.cos(Math.toRadians(yaw)));
                event.setZ(forward * speed * Math.cos(Math.toRadians(yaw)) - strafe * speed * -Math.sin(Math.toRadians(yaw)));
            }
        }
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.272;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            final int amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + (0.2 * amplifier);
        }
        return baseSpeed;
    }


}

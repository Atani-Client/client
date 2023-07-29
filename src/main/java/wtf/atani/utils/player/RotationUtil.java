package wtf.atani.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.*;
import wtf.atani.utils.interfaces.Methods;
import wtf.atani.utils.math.MathUtil;
import wtf.atani.utils.math.random.RandomUtil;
import wtf.atani.utils.module.ScaffoldUtil;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class RotationUtil implements Methods {

    public static Vec3 getBestVector(Vec3 look, AxisAlignedBB axisAlignedBB) {
        return new Vec3(MathHelper.clamp(look.xCoord, axisAlignedBB.minX, axisAlignedBB.maxX), MathHelper.clamp(look.yCoord, axisAlignedBB.minY, axisAlignedBB.maxY), MathHelper.clamp(look.zCoord, axisAlignedBB.minZ, axisAlignedBB.maxZ));
    }

    public static Vec3 getVectorForRotation(float yaw, float pitch) {
        float f = MathHelper.cos((float) (-yaw * 0.017163291F - Math.PI));
        float f2 = MathHelper.sin((float) (-yaw * 0.017163291F - Math.PI));
        float f3 = -MathHelper.cos(-pitch * 0.017163291F);
        float f4 = MathHelper.sin(-pitch * 0.017163291F);
        return new Vec3(f2 * f3, f4, f * f3);
    }

    public static float[] getRotation(Entity entity, boolean mouseFix, boolean heuristics, boolean prediction, float minYaw, float maxYaw, float minPitch, float maxPitch, boolean snapYaw, boolean snapPitch) {
        final Vec3 bestVector = getBestVector(mc.thePlayer.getPositionEyes(1F), entity.getEntityBoundingBox());
        double x = bestVector.xCoord - mc.thePlayer.posX;
        double y = bestVector.yCoord - (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());
        double z = bestVector.zCoord - mc.thePlayer.posZ;

        if (prediction) {
            final boolean sprinting = entity.isSprinting();
            final boolean sprintingPlayer = mc.thePlayer.isSprinting();

            final float walkingSpeed = 0.10000000149011612f; //https://minecraft.fandom.com/wiki/Sprinting

            final float sprint = sprinting ? 1.25f : walkingSpeed;
            final float playerSprint = sprintingPlayer ? 1.25f : walkingSpeed;

            final float predictX = (float) ((entity.posX - entity.prevPosX) * sprint);
            final float predictZ = (float) ((entity.posZ - entity.prevPosZ) * sprint);

            final float playerPredictX = (float) ((mc.thePlayer.posX - mc.thePlayer.prevPosX) * playerSprint);
            final float playerPredictZ = (float) ((mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * playerSprint);


            if (predictX != 0.0f && predictZ != 0.0f || playerPredictX != 0.0f && playerPredictZ != 0.0f) {
                x += predictX + playerPredictX;
                z += predictZ + playerPredictZ;
            }
        }

        if (heuristics) {
            try {
                x += SecureRandom.getInstanceStrong().nextDouble() * 0.1;
                y += SecureRandom.getInstanceStrong().nextDouble() * 0.1;
                z += SecureRandom.getInstanceStrong().nextDouble() * 0.1;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        double d3 = MathHelper.sqrt(x * x + z * z);
        float yawSpeed = (float) RandomUtil.randomBetween(minYaw, maxYaw);
        float pitchSpeed = (float) RandomUtil.randomBetween(minPitch, maxPitch);
        float f = (float) (MathHelper.atan2(z, x) * (180 / Math.PI)) - 90.0F;
        float f1 = (float) (-(MathHelper.atan2(y, d3) * (180 / Math.PI)));
        final int fps = (int) (Minecraft.getDebugFPS() / 20.0F);
        final float deltaYaw = (((f - PlayerHandler.yaw) + 540) % 360) - 180;
        final float deltaPitch = f1 - PlayerHandler.pitch;
        final float yawDistance = MathHelper.clamp_float(deltaYaw, -yawSpeed, yawSpeed) / fps * 4;
        final float pitchDistance = MathHelper.clamp_float(deltaPitch, -pitchSpeed, pitchSpeed) / fps * 4;
        float calcYaw = snapYaw ? f : PlayerHandler.yaw + yawDistance;
        float calcPitch = snapPitch ? f1 : PlayerHandler.pitch + pitchDistance;
        calcPitch = MathHelper.clamp(calcPitch, -90, 90);
        if (!mouseFix)
            return new float[]{calcYaw, calcPitch};
        return applyMouseFix(calcYaw, calcPitch);
    }

    public static float[] applyMouseFix(float newYaw, float newPitch) {
        final float sensitivity = Math.max(0.001F, mc.gameSettings.mouseSensitivity);
        final int deltaYaw = (int) ((newYaw - PlayerHandler.yaw) / ((sensitivity * (sensitivity >= 0.5 ? sensitivity : 1) / 2)));
        final int deltaPitch = (int) ((newPitch - PlayerHandler.pitch) / ((sensitivity * (sensitivity >= 0.5 ? sensitivity : 1) / 2))) * -1;
        final float f = sensitivity * 0.6F + 0.2F;
        final float f1 = f * f * f * 8.0F;
        final float f2 = (float) deltaYaw * f1;
        final float f3 = (float) deltaPitch * f1;

        final float endYaw = (float) ((double) PlayerHandler.yaw + (double) f2 * 0.15);
        float endPitch = (float) ((double) PlayerHandler.pitch - (double) f3 * 0.15);
        endPitch = MathHelper.clamp(endPitch, -90, 90);
        return new float[]{endYaw, endPitch};
    }

    public static float[] getScaffoldRotations(final ScaffoldUtil.BlockData data, final boolean legit) {
        final Vec3 eyes = mc.thePlayer.getPositionEyes(RandomUtil.nextFloat(2.997f, 3.997f));
        final Vec3 position = new Vec3(data.position.getX() + 0.49, data.position.getY() + 0.49, data.position.getZ() + 0.49).add(new Vec3(data.face.getDirectionVec()).scale(0.489997f));
        final Vec3 resultPosition = position.subtract(eyes);
        float yaw = (float) Math.toDegrees(Math.atan2(resultPosition.zCoord, resultPosition.xCoord)) - 90.0F;
        float pitch = (float) -Math.toDegrees(Math.atan2(resultPosition.yCoord, Math.hypot(resultPosition.xCoord, resultPosition.zCoord)));
        final float[] rotations = new float[] {yaw, pitch};

        if (legit) {
            return new float[] {mc.thePlayer.rotationYaw + 180F, updateRotation(PlayerHandler.pitch, applyMouseFix(0, rotations[1])[1], (float) RandomUtil.randomBetween(30, 80))};
        }

        return applyMouseFix(rotations[0], rotations[1]);
    }

    public static float[] updateRotationAdvanced(float oldYaw, float newYaw, float yawSpeed, float oldPitch, float newPitch, float pitchSpeed) {
        final int fps = (int) (Minecraft.getDebugFPS() / 20.0F);
        final float deltaYaw = (((newYaw - oldYaw) + 540) % 360) - 180;
        final float deltaPitch = newPitch - oldPitch;
        final float yawDistance = MathHelper.clamp_float(deltaYaw, -yawSpeed, yawSpeed) / fps * 4;
        final float pitchDistance = MathHelper.clamp_float(deltaPitch, -pitchSpeed, pitchSpeed) / fps * 4;
        float calcYaw = oldYaw + yawDistance;
        float calcPitch = oldPitch + pitchDistance;
        return new float[] {calcYaw, calcPitch};
    }

    public static float getSimpleScaffoldYaw() {
        boolean forward = mc.gameSettings.keyBindForward.isKeyDown();
        boolean left = mc.gameSettings.keyBindLeft.isKeyDown();
        boolean right = mc.gameSettings.keyBindRight.isKeyDown();
        boolean back = mc.gameSettings.keyBindBack.isKeyDown();

        float yaw = 0;

        // Only one Key directions
        if (forward && !left && !right && !back)
            yaw = 180;
        if (!forward && left && !right && !back)
            yaw = 90;
        if (!forward && !left && right && !back)
            yaw = -90;
        if (!forward && !left && !right && back)
            yaw = 0;

        // Multi Key directions
        if (forward && left && !right && !back)
            yaw = 135;
        if (forward && !left && right && !back)
            yaw = -135;

        if (!forward && left && !right && back)
            yaw = 45;
        if (!forward && !left && right && back)
            yaw = -45;

        return mc.thePlayer.rotationYaw + yaw;
    }

    public static void resetRotations(float yaw, float pitch, boolean silent) {
        if(silent) {
            mc.thePlayer.rotationYaw = yaw - yaw % 360 + mc.thePlayer.rotationYaw % 360;
        } else {
            mc.thePlayer.rotationYaw = yaw;
            mc.thePlayer.rotationPitch = pitch;
        }
    }

    public static float updateRotation(float p_75652_1_, float p_75652_2_, float speed) {
        float f = MathHelper.wrapDegrees(p_75652_2_ - p_75652_1_);

        if (f > (float) speed) {
            f = (float) speed;
        }

        if (f < -(float) speed) {
            f = -(float) speed;
        }

        return p_75652_1_ + f;
    }

    public static float updateRotation(float p_75652_1_, float p_75652_2_) {
        float f = MathHelper.wrapDegrees(p_75652_2_ - p_75652_1_);

        if (f > (float) 180) {
            f = (float) 180;
        }

        if (f < -(float) 180) {
            f = -(float) 180;
        }

        return p_75652_1_ + f;
    }

}

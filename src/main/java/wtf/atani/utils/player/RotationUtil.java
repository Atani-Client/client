package wtf.atani.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.util.MathHelper;
import wtf.atani.utils.interfaces.Methods;
import wtf.atani.utils.math.MathUtil;
import wtf.atani.utils.math.random.RandomUtil;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class RotationUtil implements Methods {

    public static Vec3 getBestVector(Vec3 look, AxisAlignedBB axisAlignedBB) {
        return new Vec3(MathHelper.clamp(look.xCoord, axisAlignedBB.minX, axisAlignedBB.maxX), MathHelper.clamp(look.yCoord, axisAlignedBB.minY, axisAlignedBB.maxY), MathHelper.clamp(look.zCoord, axisAlignedBB.minZ, axisAlignedBB.maxZ));
    }

    public static float[] getRotation(Entity entity, boolean mouseFix, boolean heuristics, boolean prediction, float minYaw, float maxYaw, float minPitch, float maxPitch) {
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
        float calcYaw = PlayerHandler.yaw + yawDistance;
        float calcPitch = PlayerHandler.pitch + pitchDistance;
        calcPitch = MathHelper.clamp(calcPitch, -90, 90);
        if (!mouseFix)
            return new float[]{calcYaw, calcPitch};
        return applyMouseFix(calcYaw, calcPitch);
    }

    public static float[] getRotation(BlockPos pos, AxisAlignedBB block, boolean mouseFix) {
        double x = pos.getX() - mc.thePlayer.posX + mc.thePlayer.motionX;
        double y = (pos.getY() + (block.maxY - block.minY)) - (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());
        double z = pos.getZ() - mc.thePlayer.posZ + mc.thePlayer.motionZ;

        double d3 = MathHelper.sqrt(x * x + z * z);
        float f = (float) (MathHelper.atan2(z, x) * (180D / Math.PI)) - 90.0F;
        float f1 = (float) (-(MathHelper.atan2(y, d3) * (180D / Math.PI)));
        float calcPitch = updateRotation(PlayerHandler.pitch, f1);
        float calcYaw = updateRotation(PlayerHandler.yaw, f);
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

    public static void resetRotations(float yaw, float pitch, boolean silent) {
        if(silent) {
            mc.thePlayer.rotationYaw = yaw - yaw % 360 + mc.thePlayer.rotationYaw % 360;
        } else {
            mc.thePlayer.rotationYaw = yaw;
            mc.thePlayer.rotationPitch = pitch;
        }
    }

    private static float updateRotation(float p_75652_1_, float p_75652_2_) {
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

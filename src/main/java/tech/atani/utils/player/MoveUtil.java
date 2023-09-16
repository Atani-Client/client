package tech.atani.utils.player;

import tech.atani.event.impl.other.MoveEvent;
import tech.atani.utils.interfaces.IMethods;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.potion.Potion;

public class MoveUtil implements IMethods {
    public static final double WALK_SPEED = .221;
    public static final double WEB_SPEED = .105 / WALK_SPEED;
    public static final double SWIM_SPEED = .115f / WALK_SPEED;
    public static final double SNEAK_SPEED = .3f;
    public static final double SPRINTING_SPEED = 1.3f;

    public static final double[] DEPTH_STRIDER = {
            1.f, .1645f / SWIM_SPEED / WALK_SPEED, .1995f / SWIM_SPEED / WALK_SPEED, 1.f / SWIM_SPEED
    };

    public static void jump(MoveEvent event) {
        double jumpY = mc.player.getJumpUpwardsMotion();

        if (mc.player.isPotionActive(Potion.jump))
            jumpY += (float) (mc.player.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;

        event.setY(mc.player.motionY = jumpY);
    }

    public static float speedBoost(float times) {
        float boost = (float) ((MoveUtil.baseSpeed() - 0.2875F) * times);

        if(0 > boost)
            boost = 0;

        return boost;
    }

    public static int getSpeedAmplifier() {
        if (mc.player.isPotionActive(Potion.moveSpeed))
            return 1 + mc.player.getActivePotionEffect(Potion.moveSpeed).getAmplifier();

        return 0;
    }

    public static void strafe(MoveEvent event, double speed) {
        float direction = (float) Math.toRadians(getPlayerDirection());

        if (isMoving()) {
            event.setX(mc.player.motionX = -Math.sin(direction) * speed);
            event.setZ(mc.player.motionZ = Math.cos(direction) * speed);
        } else {
            event.setX(mc.player.motionX = 0);
            event.setZ(mc.player.motionZ = 0);
        }
    }

    public static void strafe(double speed) {
        float direction = (float) Math.toRadians(getPlayerDirection());

        if (isMoving()) {
            mc.player.motionX = -Math.sin(direction) * speed;
            mc.player.motionZ = Math.cos(direction) * speed;
        } else {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }
    }

    public static float getPlayerDirection() {
        float direction = mc.player.rotationYaw;

        if (mc.player.moveForward > 0) {
            if (mc.player.moveStrafing > 0) {
                direction -= 45;
            } else if (mc.player.moveStrafing < 0) {
                direction += 45;
            }
        } else if (mc.player.moveForward < 0) {
            if (mc.player.moveStrafing > 0) {
                direction -= 135;
            } else if (mc.player.moveStrafing < 0) {
                direction += 135;
            } else {
                direction -= 180;
            }
        } else {
            if (mc.player.moveStrafing > 0) {
                direction -= 90;
            } else if (mc.player.moveStrafing < 0) {
                direction += 90;
            }
        }

        return direction;
    }

    public static boolean isMoving() {
        return mc.player.moveForward != 0 || mc.player.moveStrafing != 0;
    }

    public static boolean isInLiquid() {
        return mc.player.isInWater() || mc.player.isInLava();
    }

    public static boolean enoughMovementForSprinting() {
        return Math.abs(mc.player.moveForward) >= .8f || Math.abs(mc.player.moveStrafing) >= .8f;
    }

    public static double baseSpeed() {
        double speed;
        boolean useModifiers = false;

        if (mc.player.isInWeb)
            speed = WEB_SPEED * WALK_SPEED;
        else if (MoveUtil.isInLiquid()) {
            speed = SWIM_SPEED * WALK_SPEED;

            final int level = EnchantmentHelper.getDepthStriderModifier(mc.player);

            if (level > 0) {
                speed *= DEPTH_STRIDER[level];
                useModifiers = true;
            }
        } else if (mc.player.isSneaking()) {
            speed = SNEAK_SPEED * WALK_SPEED;
        } else {
            speed = WALK_SPEED;
            useModifiers = true;
        }

        if (useModifiers) {
            if (enoughMovementForSprinting())
                speed *= SPRINTING_SPEED;

            if (mc.player.isPotionActive(Potion.moveSpeed))
                speed *= 1 + (.2 * (mc.player.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1));

            if (mc.player.isPotionActive(Potion.moveSlowdown))
                speed = .29;
        }

        return speed;
    }
}

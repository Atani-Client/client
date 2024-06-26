package tech.atani.client.feature.module.impl.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.*;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.value.impl.CheckBoxValue;
import tech.atani.client.feature.value.impl.MultiStringBoxValue;
import tech.atani.client.feature.value.impl.SliderValue;
import tech.atani.client.listener.event.minecraft.player.movement.UpdateMotionEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.utility.player.PlayerUtil;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@ModuleData(name = "AimAssist", description = "Assists aiming at players", category = Category.COMBAT)
public class AimAssist extends Module {

    private final MultiStringBoxValue items = new MultiStringBoxValue("Allowed Items", "What items should be allowed to aim with?", this, new String[] {"Sword"}, new String[] {"Sword", "Axe", "Bows"});

    private final SliderValue<Float> horizontalSpeed = new SliderValue<>("Horizontal Aim Speed", "How much will the horizontal aim speed be?", this, 0.5f, 0f, 10f, 1);
    private final SliderValue<Float> verticalSpeed = new SliderValue<>("Vertical Aim Speed", "How much will the vertical aim speed be?", this, 0.5f, 0f, 10f, 1);

    private final SliderValue<Float> cameraShake = new SliderValue<>("Camera Shake Amount", "How much will the camera shake?", this, 0.2f, 0f, 5f, 1);
    private final SliderValue<Float> maxRange = new SliderValue<>("Max Range", "What should the max distance to list the target?", this, 5f, 1f, 10f, 1);
    private final SliderValue<Float> minRange = new SliderValue<>("Min Range", "What should the min distance to list the target?", this, 3f, 0f, 10f, 1);
    private final CheckBoxValue aimOnEntity = new CheckBoxValue("Aim While On Entity", "Should the aim assist only work when pointedentity is null?", this, false);
    private final CheckBoxValue clickToAim = new CheckBoxValue("Click to Aim", "Should the aim assist only work when holding down the mouse?", this, false);

    private int speedFriction;
    @Listen
    public void onMotion(UpdateMotionEvent event) {
        if(mc.pointedEntity == null) {
            speedFriction = 0;
        }

        if (event.getType() == UpdateMotionEvent.Type.MID) {
            List<EntityLivingBase> targets = mc.theWorld.loadedEntityList.stream()
                    .filter(entity -> entity instanceof EntityLivingBase)
                    .map(entity -> (EntityLivingBase) entity)
                    .filter(entityLivingBase -> {
                        double distanceToPlayer = entityLivingBase.getDistanceToEntity(mc.thePlayer);
                        return distanceToPlayer >= minRange.getValue()
                                && distanceToPlayer <= maxRange.getValue()
                                && entityLivingBase != mc.thePlayer
                                && !entityLivingBase.isDead
                                && entityLivingBase.getHealth() > 0
                                && !entityLivingBase.isInvisible()
                                && !entityLivingBase.getCommandSenderName().isEmpty()
                                && !entityLivingBase.getCommandSenderName().contains(" ");
                    })
                    .sorted(Comparator.comparingDouble(entity -> entity.getDistanceToEntity(mc.thePlayer)))
                    .collect(Collectors.toList());

            if (!targets.isEmpty()) {
                EntityLivingBase target = targets.get(0);
                aim(target);
            }
        }
    }

    public void aim(EntityLivingBase entityLivingBase) {
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        if (mc.currentScreen == null && heldItem != null) {

            if (items.get("Sword") && heldItem.getItem() instanceof ItemSword) {
                setRotations(entityLivingBase);
            } else if(items.get("Axe") && heldItem.getItem() instanceof ItemAxe) {
                setRotations(entityLivingBase);
            } else if (items.get("Bows") && heldItem.getItem() instanceof ItemBow) {
                setRotations(entityLivingBase);
            } else {
                setRotations(entityLivingBase);
            }
        }
    }

    public void setRotations(EntityLivingBase e) {
        float[] rotations = getRotations(e);

        PlayerUtil.addChatMessgae("ROTS: " + rotations[0] + " 2: " + rotations[1], true);
        if (clickToAim.getValue()) {
            if (Mouse.isButtonDown(0)) {
                mc.thePlayer.rotationYaw = rotations[0];
                mc.thePlayer.rotationPitch = rotations[1];
            }
        } else {
            mc.thePlayer.rotationYaw = rotations[0];
            mc.thePlayer.rotationPitch = rotations[1];
        }
    }

    private float[] getRotations(Entity entity) {
        float rotationSpeedX = horizontalSpeed.getValue();
        float rotationSpeedY = verticalSpeed.getValue();
        float cameraShakeSpeed = (float) (Math.random() * cameraShake.getValue());

        rotationSpeedX += ((2 / (rotationSpeedX + 1))) / 100;
        rotationSpeedY += ((2 / (rotationSpeedY + 1))) / 100;

        double deltaX = entity.posX - mc.thePlayer.posX;
        double deltaY = entity.posY - 3.5 + entity.getEyeHeight() - mc.thePlayer.posY + mc.thePlayer.getEyeHeight();
        double deltaZ = entity.posZ - mc.thePlayer.posZ;

        float yaw = (float) Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0F;
        float pitch = (float) -Math.toDegrees(Math.atan2(deltaY, Math.sqrt(deltaX * deltaX + deltaZ * deltaZ)));

        float deltaYaw = MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw);
        float deltaPitch = MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch);

        rotationSpeedX -= speedFriction * horizontalSpeed.getValue();
        rotationSpeedY -= speedFriction * verticalSpeed.getValue();

        deltaYaw = Math.min(rotationSpeedX, Math.max(-rotationSpeedX, deltaYaw));
        deltaPitch = Math.min(rotationSpeedY, Math.max(-rotationSpeedY, deltaPitch));
        yaw = mc.thePlayer.rotationYaw + deltaYaw + cameraShakeSpeed + speedFriction;
        pitch = mc.thePlayer.rotationPitch + deltaPitch + cameraShakeSpeed + speedFriction;

        return new float[]{yaw, pitch};
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}

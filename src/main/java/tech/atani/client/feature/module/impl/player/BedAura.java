package tech.atani.client.feature.module.impl.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.value.impl.CheckBoxValue;
import tech.atani.client.feature.value.impl.SliderValue;
import tech.atani.client.feature.value.impl.StringBoxValue;
import tech.atani.client.listener.event.minecraft.player.movement.UpdateMotionEvent;
import tech.atani.client.listener.event.minecraft.player.rotation.RotationEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.utility.player.raytrace.RaytraceUtil;
import tech.atani.client.utility.player.rotation.RotationUtil;

@ModuleData(name = "BedAura", description = "Breaks beds automatically.", category = Category.PLAYER)
public class BedAura extends Module {

    private final StringBoxValue throughWalls = new StringBoxValue("Through walls", "Which mode will the module use to break blocks?", this, new String[] {"None", "Raycast"});
    private final SliderValue<Float> range = new SliderValue<Float>("Range", "How much of a distance will the aura allow?", this, 3f, 0f, 5f, 1);
    private final CheckBoxValue swinging = new CheckBoxValue("Swing Client-Side", "Swing client-side when breaking beds?", this, true);

    private float targetYaw;
    private float targetPitch;

    @Listen
    public void onUpdateMotion(UpdateMotionEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null)
            return;

        BlockPos targetBedPos = findTargetBed();
        if (targetBedPos != null) {
            Vec3 targetBedVec = new Vec3(targetBedPos.getX() + 0.5, targetBedPos.getY() + 0.5, targetBedPos.getZ() + 0.5);
            float[] rotations = RotationUtil.getRotation(targetBedVec);

            targetYaw = rotations[0];
            targetPitch = rotations[1];

            if (swinging.getValue()) {
                mc.thePlayer.swingItem();
            }

            switch (throughWalls.getValue()) {
                case "None":
                    break;
                case "Raycast":
                    raycastBreakBed(targetBedPos);
                    break;
            }
        }
    }


    @Listen
    public void onRotations(RotationEvent event) {
        event.setYaw(targetYaw);
        event.setPitch(targetPitch);
    }

    private void raycastBreakBed(BlockPos targetBedPos) {
        float[] rots = RotationUtil.getRotation(new Vec3(targetBedPos.getX() + 0.5, targetBedPos.getY() + 0.5, targetBedPos.getZ() + 0.5));
        MovingObjectPosition rayTraceResult = RaytraceUtil.rayCast(1, rots, range.getValue(), 0.1);

        if (rayTraceResult != null && rayTraceResult.getBlockPos() != null && rayTraceResult.getBlockPos().equals(targetBedPos)) {
            mc.thePlayer.swingItem();
            mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, targetBedPos, rayTraceResult.sideHit));
            mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, targetBedPos, rayTraceResult.sideHit));
        }
    }

    private BlockPos findTargetBed() {
        Entity targetEntity = null;
        double closestDistance = Double.MAX_VALUE;

        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityItem) {
                continue;
            }

            BlockPos entityPos = entity.getPosition();
            double distance = mc.thePlayer.getDistanceSq(entityPos);

            if (distance <= range.getValue() * range.getValue() && distance < closestDistance) {
                targetEntity = entity;
                closestDistance = distance;
            }
        }

        if (targetEntity != null) {
            return targetEntity.getPosition();
        }

        return null;
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {
        targetYaw = 0;
        targetPitch = 0;
    }

}
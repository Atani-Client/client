package wtf.atani.module.impl.player;

import wtf.atani.event.events.ClickingEvent;
import wtf.atani.event.events.RotationEvent;
import wtf.atani.event.events.UpdateMotionEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import wtf.atani.utils.math.random.RandomUtil;
import wtf.atani.utils.math.time.TimeHelper;
import wtf.atani.utils.module.ScaffoldUtil;
import wtf.atani.utils.player.RotationUtil;
import wtf.atani.utils.world.WorldUtil;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.SliderValue;
import wtf.atani.value.impl.StringBoxValue;

import java.util.Random;

@ModuleInfo(name = "Scaffold", description = "Places blocks under you", category = Category.PLAYER)
public class Scaffold extends Module {


    private Vec3 pos;
    private BlockPos blockPos;
    private ScaffoldUtil.BlockData blockData;
    private float[] prevRotations = new float[1];
    private float[] rotations = new float[1];
    private int slot;
    private double y;
    private final TimeHelper timer = new TimeHelper();

    public final StringBoxValue placeTiming = new StringBoxValue("Place Timing", "On which event will the scaffold place blocks?", this, new String[] {"Legit", "Tick", "Pre", "Post"});
    public final SliderValue<Long> placeDelay = new SliderValue<>("Place Delay", "What'll be the delay between placing?", this, 0L, 0L, 1000L, 0);
    public final StringBoxValue rotationMode = new StringBoxValue("Rotations", "How will the module rotate?", this, new String[]{"Normal", "Legit"});
    public final CheckBoxValue legitPlace = new CheckBoxValue("Legit place", "Place legitly?", this, false);
    public final CheckBoxValue sprint = new CheckBoxValue("Sprint", "Allow sprinting?", this, true);
    public final CheckBoxValue silent = new CheckBoxValue("Silent", "Silently switch items?", this, true);
    public final CheckBoxValue keepY = new CheckBoxValue("KeepY", "Stop from changing Y", this, false);

    @Listen
    public final void tick(final UpdateMotionEvent event) {
        if(event.getType() == UpdateMotionEvent.Type.PRE) {
            if (blockData == null || ScaffoldUtil.getBlockSlot() == -1) {
                prevRotations = rotations;
                rotations = new float[] {mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
                return;
            }

            mc.gameSettings.keyBindSprint.pressed = sprint.getValue();
            mc.thePlayer.setSprinting(sprint.getValue());

            final double playerYaw = Math.toDegrees(mc.thePlayer.rotationYaw);
            final double random = RandomUtil.randomBetween(0.02, 0.03);
            final BlockPos bp = new BlockPos(mc.thePlayer.posX + random * -Math.cos(playerYaw), mc.thePlayer.posY - 0.9D, mc.thePlayer.posZ + random * Math.sin(playerYaw));
            mc.gameSettings.keyBindSneak.pressed = WorldUtil.getBlock(bp).getMaterial() == Material.air;

            prevRotations = rotations;
            if (!rotationMode.is("legit"))
                rotations = RotationUtil.getScaffoldRotations(blockData, false);
            else if (WorldUtil.getBlock(bp).getMaterial() == Material.air)
                rotations = RotationUtil.getScaffoldRotations(blockData, true);


            if (placeTiming.is("tick"))
                doPlacement();
        } else if(event.getType() == UpdateMotionEvent.Type.MID) {
            if (mc.thePlayer.posY < y || (!mc.thePlayer.onGround && !isMoving()) || mc.thePlayer.posY - y > 6 || !keepY.getValue()) y = mc.thePlayer.posY - 0.9D;

            pos = new Vec3(mc.thePlayer.posX, y , mc.thePlayer.posZ);
            blockPos = new BlockPos(pos.xCoord, pos.yCoord, pos.zCoord);

            blockData = ScaffoldUtil.getBlockData(blockPos);

            if (blockData == null)
                blockData = ScaffoldUtil.getBlockData(blockPos.offsetDown());

            if (blockData == null || ScaffoldUtil.getBlockSlot() == -1)
                return;

            if (placeTiming.is("pre"))
                doPlacement();
        } else if(event.getType() == UpdateMotionEvent.Type.POST) {
            if (blockData == null || ScaffoldUtil.getBlockSlot() == -1)
                return;

            if (placeTiming.is("post"))
                doPlacement();
        }
    }

    @Listen
    public void onClicking(ClickingEvent clickingEvent) {
        if (blockData == null || ScaffoldUtil.getBlockSlot() == -1)
            return;

        if (placeTiming.is("legit"))
            doPlacement();
    }

    private void doPlacement() {
        slot = mc.thePlayer.inventory.currentItem;
        mc.thePlayer.inventory.currentItem = ScaffoldUtil.getBlockSlot();
        if (!legitPlace.getValue()) {
            if (WorldUtil.getBlock(this.blockPos).getMaterial() == Material.air && timer.hasReached(placeDelay.getValue().intValue())) {
                mc.thePlayer.swingItem();
                mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), blockData.position, blockData.face, RotationUtil.getVectorForRotation(rotations[0], rotations[1]));
                timer.reset();
            }
        } else if(blockData != null && mc.objectMouseOver != null && blockData != null) {
            final BlockPos blockpos = mc.objectMouseOver.getBlockPos();
            if (blockpos.equals(blockData.position) && mc.objectMouseOver.sideHit.equals(blockData.face)) {
                if (WorldUtil.getBlock(blockpos).getMaterial() != Material.air) {
                    if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.inventoryContainer.getSlot(36 + slot).getStack(), blockpos, mc.objectMouseOver.sideHit, mc.objectMouseOver.hitVec))
                        mc.thePlayer.swingItem();
                }
            }
        }
        if (silent.getValue()) mc.thePlayer.inventory.currentItem = slot;
    }

    @Listen
    public final void look(final RotationEvent event) {
        if (blockData == null || ScaffoldUtil.getBlockSlot() == -1)
            return;

        event.setYaw(rotations[0]);
        event.setPitch(rotations[1]);
    }

    @Override
    public void onEnable() {
        prevRotations = rotations;

        if (mc.thePlayer == null)
            return;

        rotations = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
        y = mc.thePlayer.posY - 0.9D;
        slot = mc.thePlayer.inventory.currentItem;
    }

    @Override
    public void onDisable() {
        mc.thePlayer.inventory.currentItem = slot;
        if (legitPlace.getValue())
            mc.gameSettings.keyBindSneak.pressed = false;
    }

    public final float[] getRotations() {
        return rotations;
    }

    public final float[] getPrevRotations() {
        return prevRotations;
    }

}

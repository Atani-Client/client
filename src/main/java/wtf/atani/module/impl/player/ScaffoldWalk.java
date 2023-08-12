package wtf.atani.module.impl.player;

import com.google.common.base.Supplier;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import wtf.atani.event.events.*;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.utils.math.time.TimeHelper;
import wtf.atani.utils.player.MoveUtil;
import wtf.atani.utils.player.PlayerHandler;
import wtf.atani.utils.player.PlayerUtil;
import wtf.atani.utils.player.RotationUtil;
import wtf.atani.utils.world.BlockUtil;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.SliderValue;
import wtf.atani.value.impl.StringBoxValue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@ModuleInfo(name = "ScaffoldWalk", description = "Bridging automatically", category = Category.PLAYER)
public class ScaffoldWalk extends Module {

    private final StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[]{"Slowly", "Quickly", "Breezily", "Godly", "Custom"});
    private final CheckBoxValue swinging = new CheckBoxValue("Swing Client-Side", "Swing client-side when placing blocks?", this, true);
    private final CheckBoxValue sprint = new CheckBoxValue("Sprint", "Allow sprinting?", this, false);
    private final CheckBoxValue switchItems = new CheckBoxValue("Switch Items", "Switch to blocks?", this, true);
    private final CheckBoxValue reverseMovement = new CheckBoxValue("Reverse Movement", "Reverse your movement?", this, false);
    private final CheckBoxValue addStrafe = new CheckBoxValue("Add Strafe", "Strafe a little?", this, false, new Supplier[]{() -> mode.getValue().equalsIgnoreCase("Custom")});
    private final SliderValue<Long> delay = new SliderValue<>("Delay", "What will be the delay between placing?", this, 0L, 0L, 1000L, 0, new Supplier[]{() -> mode.getValue().equalsIgnoreCase("Quickly") || mode.getValue().equalsIgnoreCase("Custom")});
    private final CheckBoxValue sneak = new CheckBoxValue("Sneak", "Sneak?", this, false, new Supplier[]{() -> mode.getValue().equalsIgnoreCase("Custom")});
    private final StringBoxValue sneakMode = new StringBoxValue("Sneak Mode", "When will the module sneak?", this, new String[]{"Edge", "Constant"}, new Supplier[]{() -> mode.getValue().equalsIgnoreCase("Custom") && sneak.getValue()});
    private final SliderValue<Long> unSneakDelay = new SliderValue<>("Unsneak delay", "What will be the delay between unsneaking?", this, 0L, 0L, 1000L, 0, new Supplier[]{() -> mode.getValue().equalsIgnoreCase("Quickly") || (mode.getValue().equalsIgnoreCase("Custom") && sneak.getValue() && sneakMode.getValue().equalsIgnoreCase("Edge"))});

    private final TimeHelper timeHelper = new TimeHelper(), unsneakTimeHelper = new TimeHelper(), startingTimeHelper = new TimeHelper();
    private double[] lastPos = new double[3];
    private int lastItem = -1;
    private BlockPos blockPos;
    private boolean starting;

    @Listen
    public void onDirectionCheck(DirectionSprintCheckEvent sprintCheckEvent) {
        if (MoveUtil.getSpeed() != 0 && sprint.getValue()) {
            sprintCheckEvent.setSprintCheck(false);
        }
    }

    @Listen
    public final void onTick(TickEvent tickEvent) {
        if(mc.thePlayer == null || mc.theWorld == null)
            return;
        if(mc.thePlayer.motionX == 0.0 && mc.thePlayer.motionZ == 0.0 && mc.thePlayer.onGround) {
            starting = true;
            startingTimeHelper.reset();
        }
        if(startingTimeHelper.hasReached(200)) {
            starting = false;
        }
    }

    @Listen
    public final void onRotation(RotationEvent rotationEvent) {
        this.blockPos = BlockUtil.getAimBlockPos();
        if (this.blockPos != null) {
            float[] rotations = this.getRotations();
            rotations = RotationUtil.updateRotationSimple(rotations[0], rotations[1], starting ? 5 : 180);
            rotations = RotationUtil.applyMouseFix(rotations[0], rotations[1]);
            rotationEvent.setYaw(rotations[0]);
            rotationEvent.setPitch(rotations[1]);
        }
    }

    @Listen
    public final void onUpdate(UpdateEvent updateEvent) {
        if (switchItems.getValue()) {
            if ((mc.thePlayer.getHeldItem() != null && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock)) || mc.thePlayer.getHeldItem() == null) {
                for (int i = 0; i < 9; i++) {
                    ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);

                    if (stack != null && stack.stackSize != 0 && stack.getItem() instanceof ItemBlock) {
                        if(lastItem == -1)
                            lastItem = mc.thePlayer.inventory.currentItem;
                        mc.thePlayer.inventory.currentItem = i;
                    }
                }
            }
        }

        if (MoveUtil.getSpeed() != 0 && sprint.getValue()) {
            getPlayer().setSprinting(true);
        }

        if (reverseMovement.getValue()) {
            getGameSettings().keyBindBack.pressed = isKeyDown(getGameSettings().keyBindForward.getKeyCode());
            getGameSettings().keyBindForward.pressed = false;
        }

        if(this.mode.getValue().equalsIgnoreCase("Quickly") || (this.mode.getValue().equalsIgnoreCase("Custom") && sneak.getValue() && sneakMode.getValue().equalsIgnoreCase("Edge"))) {
            if (unSneakDelay.getValue() == 0 || unsneakTimeHelper.hasReached((long) (unSneakDelay.getValue()))) {
                getGameSettings().keyBindSneak.pressed = false;
            }

            for (int y = -1; y < 0; y++) {
                final Vec3 pos = getPlayer().getPositionVector().addVector(0, y, 0);
                final BlockPos blockPos = new BlockPos(pos);
                if (getWorld().isAirBlock(blockPos)) {
                    getGameSettings().keyBindSneak.pressed = true;
                    unsneakTimeHelper.reset();
                }
            }
        } else  if(this.mode.getValue().equalsIgnoreCase("Slowly") || (this.mode.getValue().equalsIgnoreCase("Custom") && sneak.getValue() && sneakMode.getValue().equalsIgnoreCase("Constant"))) {
            getGameSettings().keyBindSneak.pressed = true;
        }
    }

    @Listen
    public void onSilent(SilentMoveEvent silentMoveEvent) {
        if(this.mode.getValue().equalsIgnoreCase("Breezily") || (this.mode.getValue().equalsIgnoreCase("Custom") && addStrafe.getValue())) {
            final BlockPos b = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.5, mc.thePlayer.posZ);
            if (mc.theWorld.getBlockState(b).getBlock().getMaterial() == Material.air && mc.currentScreen == null && !Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCodeDefault()) && mc.thePlayer.movementInput.moveForward != 0.0f) {
                if (mc.thePlayer.getHorizontalFacing(PlayerHandler.yaw) == EnumFacing.EAST) {
                    if (b.getZ() + 0.5 > mc.thePlayer.posZ) {
                        mc.thePlayer.movementInput.moveStrafe = 1.0f;
                    }
                    else {
                        mc.thePlayer.movementInput.moveStrafe = -1.0f;
                    }
                }
                else if (mc.thePlayer.getHorizontalFacing(PlayerHandler.yaw) == EnumFacing.WEST) {
                    if (b.getZ() + 0.5 < mc.thePlayer.posZ) {
                        mc.thePlayer.movementInput.moveStrafe = 1.0f;
                    }
                    else {
                        mc.thePlayer.movementInput.moveStrafe = -1.0f;
                    }
                }
                else if (mc.thePlayer.getHorizontalFacing(PlayerHandler.yaw) == EnumFacing.SOUTH) {
                    if (b.getX() + 0.5 < mc.thePlayer.posX) {
                        mc.thePlayer.movementInput.moveStrafe = 1.0f;
                    }
                    else {
                        mc.thePlayer.movementInput.moveStrafe = -1.0f;
                    }
                }
                else if (b.getX() + 0.5 > mc.thePlayer.posX) {
                    mc.thePlayer.movementInput.moveStrafe = 1.0f;
                }
                else {
                    mc.thePlayer.movementInput.moveStrafe = -1.0f;
                }
            }
        }
    }

    @Listen
    public void onClicking(ClickingEvent clickingEvent) {
    	if(mc.thePlayer == null || mc.theWorld == null)
    		return;

        MovingObjectPosition objectOver = mc.objectMouseOver;
        BlockPos blockpos = mc.objectMouseOver.getBlockPos();
        ItemStack itemstack = mc.thePlayer.inventory.getCurrentItem();

        if (objectOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || mc.theWorld.getBlockState(blockpos).getBlock().getMaterial() == Material.air) {
            return;
        }
        if (itemstack != null && !(itemstack.getItem() instanceof ItemBlock)) {
            return;
        }

        boolean necessaryPlacement = mode.getValue().equalsIgnoreCase("Breezily") || mode.getValue().equalsIgnoreCase("Godly");

        if (necessaryPlacement ? objectOver.sideHit != EnumFacing.UP : this.timeHelper.hasReached(this.delay.getValue())) {
            if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, itemstack, blockpos, objectOver.sideHit, objectOver.hitVec)) {
                if(this.swinging.getValue())
                    mc.thePlayer.swingItem();
                else
                    mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
            }
            if (itemstack != null && itemstack.stackSize == 0) {
                mc.thePlayer.inventory.mainInventory[mc.thePlayer.inventory.currentItem] = null;
            }

            mc.sendClickBlockToController(mc.currentScreen == null && mc.gameSettings.keyBindAttack.isKeyDown() && mc.inGameHasFocus);
            timeHelper.reset();
        }
    }

    @Override
    public String getSuffix() {
        return this.mode.getValue();
    }

    @Override
    public void onEnable() {
        timeHelper.reset();
        startingTimeHelper.reset();
        starting = true;
    }

    @Override
    public void onDisable() {
        if(this.lastItem != -1) {
            mc.thePlayer.inventory.currentItem = this.lastItem;
            this.lastItem = -1;
        }
        getGameSettings().keyBindSneak.pressed = isKeyDown(getGameSettings().keyBindSneak.getKeyCode());
        getGameSettings().keyBindBack.pressed = isKeyDown(getGameSettings().keyBindBack.getKeyCode());
        getGameSettings().keyBindForward.pressed = isKeyDown(getGameSettings().keyBindForward.getKeyCode());
    }


    private float[] getRotations() {
        final float[] angles = {PlayerHandler.yaw, PlayerHandler.pitch};
        final BlockPos playerPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.5, mc.thePlayer.posZ);
        if (this.starting) {
            angles[1] = 80.34f;
            angles[0] = mc.thePlayer.rotationYaw - 180;
        }
        else {
            final float yaw = mc.thePlayer.rotationYaw - 180.0f;
            angles[0] = yaw;
            double x = mc.thePlayer.posX;
            double z = mc.thePlayer.posZ;
            final double add1 = 1.288;
            final double add2 = 0.288;

            if (!PlayerUtil.canBuildForward()) {
                x += mc.thePlayer.posX - this.lastPos[0];
                z += mc.thePlayer.posZ - this.lastPos[2];
            }

            this.lastPos = new double[]{mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ};
            final double maxX = this.blockPos.getX() + add1;
            final double minX = this.blockPos.getX() - add2;
            final double maxZ = this.blockPos.getZ() + add1;
            final double minZ = this.blockPos.getZ() - add2;

            if (x > maxX || x < minX || z > maxZ || z < minZ) {
                final List<MovingObjectPosition> hitBlockList = new ArrayList<>();
                final List<Float> pitchList = new ArrayList<>();

                for (float pitch = Math.max(PlayerHandler.pitch - 20.0f, -90.0f); pitch < Math.min(PlayerHandler.pitch + 20.0f, 90.0f); pitch += 0.05f) {
                    final float[] rotation = RotationUtil.applyMouseFix(yaw, pitch);
                    final MovingObjectPosition hitBlock = mc.thePlayer.customRayTrace(4.5, 1.0f, yaw, rotation[1]);

                    if (hitBlock.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
                            && BlockUtil.isOkBlock(hitBlock.getBlockPos())
                            && !hitBlockList.contains(hitBlock)
                            && hitBlock.getBlockPos().equalsBlockPos(this.blockPos)
                            && hitBlock.sideHit != EnumFacing.DOWN
                            && hitBlock.sideHit != EnumFacing.UP
                            && hitBlock.getBlockPos().getY() <= playerPos.getY()) {
                        hitBlockList.add(hitBlock);
                        pitchList.add(rotation[1]);
                    }
                }

                hitBlockList.sort(Comparator.comparingDouble(m -> mc.thePlayer.getDistanceSq(m.getBlockPos().add(0.5, 0.5, 0.5))));
                MovingObjectPosition nearestBlock = null;

                if (!hitBlockList.isEmpty()) {
                    nearestBlock = hitBlockList.get(0);
                }

                if (nearestBlock != null) {
                    angles[0] = yaw;
                    pitchList.sort(Comparator.comparingDouble(RotationUtil::getDistanceToLastPitch));

                    if (!pitchList.isEmpty()) {
                        angles[1] = pitchList.get(0);
                    }

                    return angles;
                }
            } else {
                angles[1] = PlayerHandler.pitch;
            }
        }

        return angles;
    }

}

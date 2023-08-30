package tech.atani.client.feature.module.impl.player;

import com.google.common.base.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import tech.atani.client.listener.event.minecraft.game.RunTickEvent;
import tech.atani.client.listener.event.minecraft.input.ClickingEvent;
import tech.atani.client.listener.event.minecraft.player.movement.SafeWalkEvent;
import tech.atani.client.listener.event.minecraft.player.rotation.RotationEvent;
import tech.atani.client.listener.event.minecraft.player.movement.DirectionSprintCheckEvent;
import tech.atani.client.listener.event.minecraft.player.movement.SilentMoveEvent;
import tech.atani.client.listener.event.minecraft.player.movement.UpdateEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.utility.interfaces.Methods;
import tech.atani.client.utility.math.time.TimeHelper;
import tech.atani.client.utility.player.movement.MoveUtil;
import tech.atani.client.utility.player.PlayerHandler;
import tech.atani.client.utility.player.PlayerUtil;
import tech.atani.client.utility.player.rotation.RotationUtil;
import tech.atani.client.feature.value.impl.CheckBoxValue;
import tech.atani.client.feature.value.impl.SliderValue;
import tech.atani.client.feature.value.impl.StringBoxValue;
import tech.atani.client.utility.world.block.BlockUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@ModuleData(name = "ScaffoldWalk", description = "Bridging automatically", category = Category.PLAYER)
public class ScaffoldWalk extends Module {
    private final CheckBoxValue swinging = new CheckBoxValue("Swing Client-Side", "Swing client-side when placing blocks?", this, true);
    private final CheckBoxValue sprint = new CheckBoxValue("Sprint", "Allow sprinting?", this, false);
    private final CheckBoxValue switchItems = new CheckBoxValue("Switch Items", "Switch to blocks?", this, true);
    private final CheckBoxValue reverseMovement = new CheckBoxValue("Reverse Movement", "Reverse your movement?", this, false);
    private final CheckBoxValue addStrafe = new CheckBoxValue("Add Strafe", "Strafe a little?", this, false);
    private final SliderValue<Long> delay = new SliderValue<>("Delay", "What will be the delay between placing?", this, 0L, 0L, 1000L, 0);
    private final CheckBoxValue sneak = new CheckBoxValue("Sneak", "Sneak?", this, false);
    private final CheckBoxValue safeWalk = new CheckBoxValue("SafeWalk", "Safewalk?", this, false);
    private final StringBoxValue sneakMode = new StringBoxValue("Sneak Mode", "When will the module sneak?", this, new String[]{"Edge", "Constant"}, new Supplier[]{() -> sneak.getValue()});
    private final SliderValue<Long> unSneakDelay = new SliderValue<>("Unsneak delay", "What will be the delay between unsneaking?", this, 0L, 0L, 1000L, 0, new Supplier[]{() -> sneak.getValue() && sneakMode.is("Edge")});

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
    public final void onTick(RunTickEvent runTickEvent) {
        if(Methods.mc.thePlayer == null || Methods.mc.theWorld == null)
            return;
        if(Methods.mc.thePlayer.motionX == 0.0 && Methods.mc.thePlayer.motionZ == 0.0 && Methods.mc.thePlayer.onGround) {
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
            rotations = RotationUtil.updateRotationSimple(rotations[0], rotations[1], starting ? 3 : 40);
            rotations = RotationUtil.applyMouseFix(rotations[0], rotations[1]);
            rotationEvent.setYaw(rotations[0]);
            rotationEvent.setPitch(rotations[1]);
        }
    }

    private static final List<Block> invalidBlocks = Arrays.asList(Blocks.air, Blocks.water, Blocks.tnt, Blocks.chest,
            Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.tnt, Blocks.enchanting_table, Blocks.carpet,
            Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.snow_layer, Blocks.ice,
            Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.chest, Blocks.torch,
            Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox, Blocks.tnt, Blocks.gold_ore,
            Blocks.iron_ore, Blocks.lapis_ore, Blocks.sand, Blocks.lit_redstone_ore, Blocks.quartz_ore,
            Blocks.redstone_ore, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate,
            Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_button,
            Blocks.wooden_button, Blocks.lever, Blocks.enchanting_table, Blocks.red_flower, Blocks.double_plant,
            Blocks.yellow_flower, Blocks.bed, Blocks.ladder, Blocks.waterlily, Blocks.double_stone_slab, Blocks.stone_slab,
            Blocks.double_wooden_slab, Blocks.wooden_slab, Blocks.heavy_weighted_pressure_plate,
            Blocks.light_weighted_pressure_plate, Blocks.stone_pressure_plate, Blocks.wooden_pressure_plate, Blocks.stone_slab2,
            Blocks.double_stone_slab2, Blocks.tripwire, Blocks.tripwire_hook, Blocks.tallgrass, Blocks.dispenser,
            Blocks.command_block, Blocks.web);

    @Listen
    public final void onUpdate(UpdateEvent updateEvent) {
        if (switchItems.getValue()) {
            if ((Methods.mc.thePlayer.getHeldItem() != null && !(Methods.mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock)) || Methods.mc.thePlayer.getHeldItem() == null) {
                for (int i = 0; i < 9; i++) {
                    ItemStack stack = Methods.mc.thePlayer.inventory.getStackInSlot(i);

                    if (stack != null && stack.stackSize != 0 && stack.getItem() instanceof ItemBlock && !invalidBlocks.contains(((ItemBlock) stack.getItem()).getBlock())) {
                        if(lastItem == -1)
                            lastItem = Methods.mc.thePlayer.inventory.currentItem;
                        Methods.mc.thePlayer.inventory.currentItem = i;
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

        if(sneak.getValue() && sneakMode.is("Edge")) {
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
        } else if(sneak.getValue() && sneakMode.is("Constant")) {
            getGameSettings().keyBindSneak.pressed = true;
        }
    }

    @Listen
    public void onSafeWalkEvent(SafeWalkEvent event) {
        if(mc.thePlayer == null && mc.theWorld == null)
            return;

        if(safeWalk.isEnabled())
            event.setSafe(true);
    }

    @Listen
    public void onSilent(SilentMoveEvent silentMoveEvent) {
        if(addStrafe.getValue()) {
            final BlockPos b = new BlockPos(Methods.mc.thePlayer.posX, Methods.mc.thePlayer.posY - 0.5, Methods.mc.thePlayer.posZ);
            if (Methods.mc.theWorld.getBlockState(b).getBlock().getMaterial() == Material.air && Methods.mc.currentScreen == null && !Keyboard.isKeyDown(Methods.mc.gameSettings.keyBindJump.getKeyCodeDefault()) && Methods.mc.thePlayer.movementInput.moveForward != 0.0f) {
                if (Methods.mc.thePlayer.getHorizontalFacing(PlayerHandler.yaw) == EnumFacing.EAST) {
                    if (b.getZ() + 0.5 > Methods.mc.thePlayer.posZ) {
                        Methods.mc.thePlayer.movementInput.moveStrafe = 1.0f;
                    }
                    else {
                        Methods.mc.thePlayer.movementInput.moveStrafe = -1.0f;
                    }
                }
                else if (Methods.mc.thePlayer.getHorizontalFacing(PlayerHandler.yaw) == EnumFacing.WEST) {
                    if (b.getZ() + 0.5 < Methods.mc.thePlayer.posZ) {
                        Methods.mc.thePlayer.movementInput.moveStrafe = 1.0f;
                    }
                    else {
                        Methods.mc.thePlayer.movementInput.moveStrafe = -1.0f;
                    }
                }
                else if (Methods.mc.thePlayer.getHorizontalFacing(PlayerHandler.yaw) == EnumFacing.SOUTH) {
                    if (b.getX() + 0.5 < Methods.mc.thePlayer.posX) {
                        Methods.mc.thePlayer.movementInput.moveStrafe = 1.0f;
                    }
                    else {
                        Methods.mc.thePlayer.movementInput.moveStrafe = -1.0f;
                    }
                }
                else if (b.getX() + 0.5 > Methods.mc.thePlayer.posX) {
                    Methods.mc.thePlayer.movementInput.moveStrafe = 1.0f;
                }
                else {
                    Methods.mc.thePlayer.movementInput.moveStrafe = -1.0f;
                }
            }
        }
    }

    @Listen
    public void onClicking(ClickingEvent clickingEvent) {
    	if(Methods.mc.thePlayer == null || Methods.mc.theWorld == null)
    		return;

        MovingObjectPosition objectOver = Methods.mc.objectMouseOver;
        BlockPos blockpos = Methods.mc.objectMouseOver.getBlockPos();
        ItemStack itemstack = Methods.mc.thePlayer.inventory.getCurrentItem();

        if (objectOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || Methods.mc.theWorld.getBlockState(blockpos).getBlock().getMaterial() == Material.air) {
            return;
        }
        if (itemstack != null && !(itemstack.getItem() instanceof ItemBlock)) {
            return;
        }

        if (this.timeHelper.hasReached(this.delay.getValue())) {
            if (Methods.mc.playerController.onPlayerRightClick(Methods.mc.thePlayer, Methods.mc.theWorld, itemstack, blockpos, objectOver.sideHit, objectOver.hitVec)) {
                if(this.swinging.getValue())
                    Methods.mc.thePlayer.swingItem();
                else
                    Methods.mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
            }
            if (itemstack != null && itemstack.stackSize == 0) {
                Methods.mc.thePlayer.inventory.mainInventory[Methods.mc.thePlayer.inventory.currentItem] = null;
            }

            Methods.mc.sendClickBlockToController(Methods.mc.currentScreen == null && Methods.mc.gameSettings.keyBindAttack.isKeyDown() && Methods.mc.inGameHasFocus);
            timeHelper.reset();
        }
    }

    @Override
    public String getSuffix() {
        return this.delay.getValue() + "ms";
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
            Methods.mc.thePlayer.inventory.currentItem = this.lastItem;
            this.lastItem = -1;
        }
        getGameSettings().keyBindSneak.pressed = isKeyDown(getGameSettings().keyBindSneak.getKeyCode());
        getGameSettings().keyBindBack.pressed = isKeyDown(getGameSettings().keyBindBack.getKeyCode());
        getGameSettings().keyBindForward.pressed = isKeyDown(getGameSettings().keyBindForward.getKeyCode());
    }


    private float[] getRotations() {
        final float[] angles = {PlayerHandler.yaw, PlayerHandler.pitch};
        final BlockPos playerPos = new BlockPos(Methods.mc.thePlayer.posX, Methods.mc.thePlayer.posY - 0.5, Methods.mc.thePlayer.posZ);
        if (this.starting) {
            angles[1] = 80.34f;
            angles[0] = Methods.mc.thePlayer.rotationYaw - 180;
        }
        else {
            final float yaw = Methods.mc.thePlayer.rotationYaw - 180.0f;
            angles[0] = yaw;
            double x = Methods.mc.thePlayer.posX;
            double z = Methods.mc.thePlayer.posZ;
            final double add1 = 1.288;
            final double add2 = 0.288;

            if (!PlayerUtil.canBuildForward()) {
                x += Methods.mc.thePlayer.posX - this.lastPos[0];
                z += Methods.mc.thePlayer.posZ - this.lastPos[2];
            }

            this.lastPos = new double[]{Methods.mc.thePlayer.posX, Methods.mc.thePlayer.posY, Methods.mc.thePlayer.posZ};
            final double maxX = this.blockPos.getX() + add1;
            final double minX = this.blockPos.getX() - add2;
            final double maxZ = this.blockPos.getZ() + add1;
            final double minZ = this.blockPos.getZ() - add2;

            if (x > maxX || x < minX || z > maxZ || z < minZ) {
                final List<MovingObjectPosition> hitBlockList = new ArrayList<>();
                final List<Float> pitchList = new ArrayList<>();

                for (float pitch = Math.max(PlayerHandler.pitch - 20.0f, -90.0f); pitch < Math.min(PlayerHandler.pitch + 20.0f, 90.0f); pitch += 0.05f) {
                    final float[] rotation = RotationUtil.applyMouseFix(yaw, pitch);
                    final MovingObjectPosition hitBlock = Methods.mc.thePlayer.customRayTrace(4.5, 1.0f, yaw, rotation[1]);

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

                hitBlockList.sort(Comparator.comparingDouble(m -> Methods.mc.thePlayer.getDistanceSq(m.getBlockPos().add(0.5, 0.5, 0.5))));
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

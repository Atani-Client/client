package wtf.atani.module.impl.player;

import com.google.common.base.Supplier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import wtf.atani.event.events.ClickingEvent;
import wtf.atani.event.events.RotationEvent;
import wtf.atani.event.events.SafeWalkEvent;
import wtf.atani.event.events.UpdateMotionEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.utils.block.BlockData;
import wtf.atani.utils.block.BlockUtil;
import wtf.atani.utils.math.time.TimeHelper;
import wtf.atani.utils.player.MoveUtil;
import wtf.atani.utils.player.PlayerHandler;
import wtf.atani.utils.player.RotationUtil;
import wtf.atani.utils.player.rayTrace.RaytraceUtil;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.SliderValue;
import wtf.atani.value.impl.StringBoxValue;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

@ModuleInfo(name = "Scaffold", description = "Places blocks under you", category = Category.PLAYER)
public class Scaffold extends Module {

    private final StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[]{"Normal", "Telly"});
    private final StringBoxValue place = new StringBoxValue("Place On", "In which rotationEvent will the scaffold place blocks?", this, new String[]{"Legit", "Pre Update", "Mid Update", "Post Update"});
    private final SliderValue<Long> delay = new SliderValue<>("Delay", "What'll be the delay between placing?", this, 0L, 0L, 1000L, 0);
    private final CheckBoxValue tower = new CheckBoxValue("Tower", "Quickly build vertically?", this, false);
    private final StringBoxValue towerMode = new StringBoxValue("Tower Mode", "Which mode will the tower mode use?", this, new String[]{"Matrix", "NCP", "Vulcan"});
    private final CheckBoxValue reverseMovement = new CheckBoxValue("Reverse Movement", "Reverse your movement?", this, false);
    private final CheckBoxValue itemSwitch = new CheckBoxValue("Switch Items", "Switch item to block?", this, true);
    private final CheckBoxValue sprint = new CheckBoxValue("Sprint", "Allow sprinting?", this, false);
    private final CheckBoxValue safewalk = new CheckBoxValue("Safewalk", "Stop if you are about to fall?", this, false);
    private final CheckBoxValue legitSafewalk = new CheckBoxValue("Legit Safewalk", "Sneak if you are about to fall?", this, false);
    private final CheckBoxValue eagle = new CheckBoxValue("Eagle", "Sneak on place?", this, false);
    private final SliderValue<Integer> eagleBlocks = new SliderValue<>("Eagle Blocks", "On what amount of blocks to sneak?", this, 2, 0, 10, 0);
    private final CheckBoxValue keepY = new CheckBoxValue("Keep Y", "Prevent from going up?", this, false);
    private final StringBoxValue yawMode = new StringBoxValue("Yaw Mode", "How will the module rotate yaw?", this, new String[]{"Normal", "Opposite", "Static"});
    private final StringBoxValue pitchMode = new StringBoxValue("Pitch Mode", "How will the module rotate pitch?", this, new String[]{"Normal", "Static"});
    public CheckBoxValue snapYaw = new CheckBoxValue("Snap Yaw", "Do not smooth out yaw?", this, false);
    public CheckBoxValue snapPitch = new CheckBoxValue("Snap Pitch", "Do not smooth out pitch?", this, false);
    public SliderValue<Float> minYaw = new SliderValue<>("Minimum Yaw", "How much will be the minimum of randomized Yaw limit?", this, 180F, 0F, 180F, 1, new Supplier[]{() -> !snapYaw.getValue()});
    public SliderValue<Float> maxYaw = new SliderValue<>("Maximum Yaw", "How much will be the maximum of randomized Yaw limit?", this, 180F, 0F, 180F, 1, new Supplier[]{() -> !snapYaw.getValue()});
    public SliderValue<Float> minPitch = new SliderValue<>("Minimum Pitch", "How much will be the minimum of randomized Pitch limit?", this, 180F, 0F, 180F, 1, new Supplier[]{() -> !snapPitch.getValue()});
    public SliderValue<Float> maxPitch = new SliderValue<>("Maximum Pitch", "How much will be the maximum of randomized Pitch limit?", this, 180F, 0F, 180F, 1, new Supplier[]{() -> !snapPitch.getValue()});
    public CheckBoxValue mouseFix = new CheckBoxValue("Mouse Fix", "Apply GCD Fix to rotations?", this, true);
    public CheckBoxValue randomizeYaw = new CheckBoxValue("Randomize Yaw", "Randomize Yaw?", this, false);
    public CheckBoxValue randomizePitch = new CheckBoxValue("Randomize Pitch", "Randomize Pitch?", this, false);
    public final CheckBoxValue rayTrace = new CheckBoxValue("Ray Trace", "Ray Trace?",this, true);
    public final CheckBoxValue strict = new CheckBoxValue("Strict", "Strict Ray Trace?",this, true);

    private BlockData blockData;
    private int places;
    private int lastItem = -1;
    private int ticks;

    private double funnY;
    private boolean placed;

    private final TimeHelper timeHelper = new TimeHelper();

    // Breezily
    private final TimeHelper breezilyTime = new TimeHelper();
    private boolean breezilyDirection;

    @Listen
    public final void onRotation(RotationEvent rotationEvent) {
        if(blockData != null) {
            if(ticks < 3 && mode.equals("Telly")) {
                return;
            }
            float[] rots = RotationUtil.faceBlock(blockData.getBlockPos(), blockData.getEnumFacing(), mouseFix.getValue(), randomizeYaw.getValue(), randomizePitch.getValue(), this.minYaw.getValue().floatValue(), this.maxYaw.getValue(), this.minPitch.getValue(), this.maxPitch.getValue(), this.snapYaw.getValue(), this.snapPitch.getValue());

            switch(this.yawMode.getValue()) {
                case "Normal":
                    rotationEvent.setYaw(rots[0]);
                    break;
                case "Opposite":
                    rotationEvent.setYaw(mc.thePlayer.rotationYaw - 180);
                    break;
                case "Static":
                    rotationEvent.setYaw(RotationUtil.getSimpleScaffoldYaw());
                    break;
            }

            switch(this.pitchMode.getValue()) {
                case "Normal":
                    rotationEvent.setPitch(rots[1]);
                    break;
                case "Static":
                    rotationEvent.setPitch(89);
                    break;
            }
        }
    }

    @Listen
    public final void onClicking(ClickingEvent clickingEvent) {
        if(blockData != null) {
            switch(place.getValue()) {
                case "Legit":
                    this.place();
                    break;
            }
        }
    }

    @Listen
    public final void onSafewalk(SafeWalkEvent safeWalkEvent) {
        if(safewalk.getValue()) {
            if(legitSafewalk.getValue()) {
                if(getBlockUnderPlayer(1) == Blocks.air) {
                    mc.gameSettings.keyBindSneak.pressed = true;
                } else {
                    mc.gameSettings.keyBindSneak.pressed = false;
                }
            } else {
                safeWalkEvent.setSafe(true);
            }
        }
    }

    @Listen
    public final void onMotion(UpdateMotionEvent event) {
        if(keepY.getValue()) {
            if(canKeepY()) {
                funnY = MathHelper.floor_double(mc.thePlayer.posY);
            } else {
                funnY = MathHelper.floor_double(mc.thePlayer.posY - 0.3D);
            }
        } else {
            funnY = MathHelper.floor_double(mc.thePlayer.posY);
        }

        this.blockData = this.getBlockData(new BlockPos(mc.thePlayer.posX, funnY - 1.0D, mc.thePlayer.posZ));

        if(blockData != null) {
            if(eagle.getValue()) {
                if(places % eagleBlocks.getValue() == 0 && placed) {
                    mc.gameSettings.keyBindSneak.pressed = true;
                } else {
                    mc.gameSettings.keyBindSneak.pressed = isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode());
                }
            }


            if(mode.equals("Telly")) {
                if(ticks >= 3) {
                    mc.thePlayer.setSprinting(false);
                }
            }

            // Switch
            if (event.getType() == UpdateMotionEvent.Type.PRE) {
                if (reverseMovement.getValue()) {
                    getGameSettings().keyBindBack.pressed = isKeyDown(getGameSettings().keyBindForward.getKeyCode());
                    getGameSettings().keyBindForward.pressed = isKeyDown(getGameSettings().keyBindBack.getKeyCode());
                    getGameSettings().keyBindLeft.pressed = isKeyDown(getGameSettings().keyBindRight.getKeyCode());
                    getGameSettings().keyBindRight.pressed = isKeyDown(getGameSettings().keyBindLeft.getKeyCode());
                }

                switch(mode.getValue()) {
                    case "Telly":
                        if(mc.thePlayer.onGround) {
                            ticks = 0;

                            resumeWalk();

                            if(mc.thePlayer.isSprinting()) {
                                mc.gameSettings.keyBindJump.pressed = true;
                            } else {
                                mc.gameSettings.keyBindJump.pressed = isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
                            }
                        } else {
                            ticks++;
                        }
                        break;
                    case "Breezily":
                        if (!sprint.getValue()) {
                            mc.thePlayer.setSprinting(false);
                        }

                        if(mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown()) {
                            if (getBlockUnderPlayer(1F) == Blocks.air) {
                                breezilyDirection = !breezilyDirection;
                            }

                            if (breezilyDirection) {
                                mc.gameSettings.keyBindLeft.pressed = true;
                                mc.gameSettings.keyBindRight.pressed = false;
                            } else {
                                mc.gameSettings.keyBindLeft.pressed = false;
                                mc.gameSettings.keyBindRight.pressed = true;
                            }

                            if(getBlockUnderPlayer(1) == Blocks.air) {
                                breezilyTime.reset();
                                breezilyDirection = !breezilyDirection;
                            }
                        } else {
                            resumeWalk();
                        }
                        break;
                    default:
                        if (!sprint.getValue()) {
                            mc.thePlayer.setSprinting(false);
                        }
                        break;
                }

                if (itemSwitch.getValue()) {
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

            }

            boolean towering = mc.gameSettings.keyBindJump.isKeyDown() && !isMoving() && tower.getValue();

            if(towering) {
                if(rayTrace.getValue() && !RaytraceUtil.getOver(blockData.getEnumFacing(), blockData.getBlockPos(), strict.getValue(), 4.5f)) {
                    return;
                }

                switch(towerMode.getValue()) {
                    case "NCP":
                        if (!mc.thePlayer.isPotionActive(Potion.jump) && getBlockUnderPlayer(1F) == Blocks.air) {
                            mc.thePlayer.jump();
                        }
                        break;
                    case "Matrix":
                        if(mc.thePlayer.onGround) {
                            mc.thePlayer.jump();
                        } else {
                            if(placed) {
                                mc.thePlayer.motionY = 0;
                            }
                        }
                        break;
                    case "Verus":
                        mc.thePlayer.motionY = 0.7;
                        break;
                }
            }

            placed = false;

            switch(place.getValue()) {
                case "Pre":
                    if (event.getType() == UpdateMotionEvent.Type.PRE) {
                        this.place();
                    }
                    break;
                case "Mid":
                    if(event.getType() == UpdateMotionEvent.Type.MID) {
                        this.place();
                    }
                    break;
                case "Post":
                    if(event.getType() == UpdateMotionEvent.Type.POST) {
                        this.place();
                    }
                    break;

            }
        }
    }

    private Block getBlockUnderPlayer(float offsetY) {
        return getBlockUnderPlayer(getPlayer(), offsetY);
    }

    private Block getBlockUnderPlayer(EntityPlayer player, float offsetY) {
        return getWorld().getBlockState(new BlockPos(player.posX, player.posY - offsetY, player.posZ)).getBlock();
    }

    private void place() {
        if(rayTrace.getValue() && !RaytraceUtil.getOver(blockData.getEnumFacing(), blockData.getBlockPos(), strict.getValue(), 4.5f)) {
            return;
        }
        if(mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock && timeHelper.hasReached(delay.getValue().longValue())) {
            if(mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), blockData.getBlockPos(), blockData.getEnumFacing(), convertDataToVec3(blockData))) {
                mc.thePlayer.swingItem();
                places++;
                placed = true;
                timeHelper.reset();
            }
        }

    }

    private BlockData getBlockData(BlockPos pos) {
        List<Block> invalid = BlockUtil.invalid;

        BlockPos pos1 = pos.add(-1, 0, 0);
        BlockPos pos2 = pos.add(1, 0, 0);
        BlockPos pos3 = pos.add(0, 0, 1);
        BlockPos pos4 = pos.add(0, 0, -1);
        BlockPos pos5 = pos.add(0, -1, 0);

        // For Under
        BlockPos pos6 = pos5.add(1, 0, 0);
        BlockPos pos7 = pos5.add(-1, 0, 0);
        BlockPos pos8 = pos5.add(0, 0, 1);
        BlockPos pos9 = pos5.add(0, 0, -1);

        // Pos
        if (isValid(invalid, pos.add(0, -1, 0))) {
            return new BlockData(pos.add(0, -1, 0), EnumFacing.UP);
        }

        if (isValid(invalid, pos.add(-1, 0, 0))) {
            return new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST);
        }

        if (isValid(invalid, pos.add(1, 0, 0))) {
            return new BlockData(pos.add(1, 0, 0), EnumFacing.WEST);
        }

        if (isValid(invalid, pos.add(0, 0, 1))) {
            return new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH);
        }

        if (isValid(invalid, pos.add(0, 0, -1))) {
            return new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH);
        }

        // Pos 1
        if (isValid(invalid, pos1.add(0, -1, 0))) {
            return new BlockData(pos1.add(0, -1, 0), EnumFacing.UP);
        }

        if (isValid(invalid, pos1.add(-1, 0, 0))) {
            return new BlockData(pos1.add(-1, 0, 0), EnumFacing.EAST);
        }

        if (isValid(invalid, pos1.add(1, 0, 0))) {
            return new BlockData(pos1.add(1, 0, 0), EnumFacing.WEST);
        }

        if (isValid(invalid, pos1.add(0, 0, 1))) {
            return new BlockData(pos1.add(0, 0, 1), EnumFacing.NORTH);
        }

        if (isValid(invalid, pos1.add(0, 0, -1))) {
            return new BlockData(pos1.add(0, 0, -1), EnumFacing.SOUTH);
        }

        // Pos 2
        if (isValid(invalid, pos2.add(0, -1, 0))) {
            return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
        }

        if (isValid(invalid, pos2.add(-1, 0, 0))) {
            return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
        }

        if (isValid(invalid, pos2.add(1, 0, 0))) {
            return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
        }

        if (isValid(invalid, pos2.add(0, 0, 1))) {
            return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
        }

        if (isValid(invalid, pos2.add(0, 0, -1))) {
            return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
        }

        // Pos 3
        if (isValid(invalid, pos3.add(0, -1, 0))) {
            return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
        }

        if (isValid(invalid, pos3.add(-1, 0, 0))) {
            return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
        }

        if (isValid(invalid, pos3.add(1, 0, 0))) {
            return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
        }

        if (isValid(invalid, pos3.add(0, 0, 1))) {
            return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
        }

        if (isValid(invalid, pos3.add(0, 0, -1))) {
            return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
        }

        // Pos 4
        if (isValid(invalid, pos4.add(0, -1, 0))) {
            return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
        }

        if (isValid(invalid, pos4.add(-1, 0, 0))) {
            return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
        }

        if (isValid(invalid, pos4.add(1, 0, 0))) {
            return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
        }

        if (isValid(invalid, pos4.add(0, 0, 1))) {
            return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
        }

        if (isValid(invalid, pos4.add(0, 0, -1))) {
            return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
        }

        // Pos 5
        if (isValid(invalid, pos5.add(0, -1, 0))) {
            return new BlockData(pos5.add(0, -1, 0), EnumFacing.UP);
        }

        if (isValid(invalid, pos5.add(-1, 0, 0))) {
            return new BlockData(pos5.add(-1, 0, 0), EnumFacing.EAST);
        }

        if (isValid(invalid, pos5.add(1, 0, 0))) {
            return new BlockData(pos5.add(1, 0, 0), EnumFacing.WEST);
        }

        if (isValid(invalid, pos5.add(0, 0, 1))) {
            return new BlockData(pos5.add(0, 0, 1), EnumFacing.NORTH);
        }

        if (isValid(invalid, pos5.add(0, 0, -1))) {
            return new BlockData(pos5.add(0, 0, -1), EnumFacing.SOUTH);
        }

        // Pos 6
        if (isValid(invalid, pos6.add(0, -1, 0))) {
            return new BlockData(pos6.add(0, -1, 0), EnumFacing.UP);
        }

        if (isValid(invalid, pos6.add(-1, 0, 0))) {
            return new BlockData(pos6.add(-1, 0, 0), EnumFacing.EAST);
        }

        if (isValid(invalid, pos6.add(1, 0, 0))) {
            return new BlockData(pos6.add(1, 0, 0), EnumFacing.WEST);
        }

        if (isValid(invalid, pos6.add(0, 0, 1))) {
            return new BlockData(pos6.add(0, 0, 1), EnumFacing.NORTH);
        }

        if (isValid(invalid, pos6.add(0, 0, -1))) {
            return new BlockData(pos6.add(0, 0, -1), EnumFacing.SOUTH);
        }

        // Pos 7
        if (isValid(invalid, pos7.add(0, -1, 0))) {
            return new BlockData(pos7.add(0, -1, 0), EnumFacing.UP);
        }

        if (isValid(invalid, pos7.add(-1, 0, 0))) {
            return new BlockData(pos7.add(-1, 0, 0), EnumFacing.EAST);
        }

        if (isValid(invalid, pos7.add(1, 0, 0))) {
            return new BlockData(pos7.add(1, 0, 0), EnumFacing.WEST);
        }

        if (isValid(invalid, pos7.add(0, 0, 1))) {
            return new BlockData(pos7.add(0, 0, 1), EnumFacing.NORTH);
        }

        if (isValid(invalid, pos7.add(0, 0, -1))) {
            return new BlockData(pos7.add(0, 0, -1), EnumFacing.SOUTH);
        }

        // Pos 8
        if (isValid(invalid, pos8.add(0, -1, 0))) {
            return new BlockData(pos8.add(0, -1, 0), EnumFacing.UP);
        }

        if (isValid(invalid, pos8.add(-1, 0, 0))) {
            return new BlockData(pos8.add(-1, 0, 0), EnumFacing.EAST);
        }

        if (isValid(invalid, pos8.add(1, 0, 0))) {
            return new BlockData(pos8.add(1, 0, 0), EnumFacing.WEST);
        }

        if (isValid(invalid, pos8.add(0, 0, 1))) {
            return new BlockData(pos8.add(0, 0, 1), EnumFacing.NORTH);
        }

        if (isValid(invalid, pos8.add(0, 0, -1))) {
            return new BlockData(pos8.add(0, 0, -1), EnumFacing.SOUTH);
        }

        // Pos 9
        if (isValid(invalid, pos9.add(0, -1, 0))) {
            return new BlockData(pos9.add(0, -1, 0), EnumFacing.UP);
        }

        if (isValid(invalid, pos9.add(-1, 0, 0))) {
            return new BlockData(pos9.add(-1, 0, 0), EnumFacing.EAST);
        }

        if (isValid(invalid, pos9.add(1, 0, 0))) {
            return new BlockData(pos9.add(1, 0, 0), EnumFacing.WEST);
        }

        if (isValid(invalid, pos9.add(0, 0, 1))) {
            return new BlockData(pos9.add(0, 0, 1), EnumFacing.NORTH);
        }

        if (isValid(invalid, pos9.add(0, 0, -1))) {
            return new BlockData(pos9.add(0, 0, -1), EnumFacing.SOUTH);
        }

        return null;
    }

    private static boolean isValid(List<Block> invalid, BlockPos pos) {
        return !invalid.contains(mc.theWorld.getBlockState((pos)).getBlock());
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        if(this.lastItem != -1) {
            mc.thePlayer.inventory.currentItem = this.lastItem;
            this.lastItem = -1;
        }
        mc.gameSettings.keyBindSneak.pressed = false;
        resumeWalk();

        if(mode.equals("Telly")) {
            mc.gameSettings.keyBindJump.pressed = false;
        }
    }

    private Vec3 convertDataToVec3(BlockData data) {
        BlockPos pos = data.getBlockPos();
        EnumFacing face = data.getEnumFacing();

        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;

        x += (double) face.getFrontOffsetX() / 2;
        z += (double) face.getFrontOffsetZ() / 2;
        y += (double) face.getFrontOffsetY() / 2;

        return new Vec3(x, y, z);
    }

    private boolean canKeepY() {
        return (!isMoving() && mc.gameSettings.keyBindJump.isKeyDown()) || (mc.thePlayer.isCollidedVertically || mc.thePlayer.onGround);
    }

}

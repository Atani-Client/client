package tech.atani.client.feature.module.impl.movement;

import com.google.common.base.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.module.storage.ModuleStorage;
import tech.atani.client.feature.value.impl.CheckBoxValue;
import tech.atani.client.feature.value.impl.SliderValue;
import tech.atani.client.feature.value.impl.StringBoxValue;
import tech.atani.client.listener.event.minecraft.game.RunTickEvent;
import tech.atani.client.listener.event.minecraft.input.ClickingEvent;
import tech.atani.client.listener.event.minecraft.network.PacketEvent;
import tech.atani.client.listener.event.minecraft.player.movement.SafeWalkEvent;
import tech.atani.client.listener.event.minecraft.player.movement.UpdateEvent;
import tech.atani.client.listener.event.minecraft.player.rotation.RotationEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.utility.interfaces.Methods;
import tech.atani.client.utility.math.MathUtil;
import tech.atani.client.utility.math.random.RandomUtil;
import tech.atani.client.utility.math.time.TimeHelper;
import tech.atani.client.utility.player.PlayerHandler;
import tech.atani.client.utility.player.PlayerUtil;
import tech.atani.client.utility.player.movement.MoveUtil;
import tech.atani.client.utility.player.raytrace.RaytraceUtil;
import tech.atani.client.utility.player.rotation.RotationUtil;
import tech.atani.client.utility.world.WorldUtil;
import tech.atani.client.utility.world.block.BlockUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@ModuleData(name = "Scaffold", description = "Places blocks under you", category = Category.MOVEMENT)
public class Scaffold extends Module {
    private final CheckBoxValue intave = new CheckBoxValue("Intave", "Intave CPS Fix?", this, false);
    private final CheckBoxValue sprint = new CheckBoxValue("Sprint", "Allow sprinting?", this, false);
    private final StringBoxValue rotations = new StringBoxValue("Rotations", "How will the scaffold rotate?", this, new String[]{"Legit", "Bruteforce", "Reverse Simple", "Snap", "BlocksMC"});
    private final SliderValue<Long> delay = new SliderValue<>("Delay", "What will be the delay between placing?", this, 0L, 0L, 1000L, 0);
    private final SliderValue<Long> randomDelay = new SliderValue<>("Random Delay", "What will be the added delay between placing?", this, 0L, 0L, 1000L, 0);
    private final CheckBoxValue safeWalk = new CheckBoxValue("SafeWalk", "Safewalk?", this, false);
    private final CheckBoxValue slowDown = new CheckBoxValue("SlowDown", "Slowdown?", this, false);
    private final StringBoxValue rayTraceMode = new StringBoxValue("Ray-Trace Mode", "What will the scaffold raytrace mode be?", this, new String[]{"Normal", "Strict"}, new Supplier[]{() -> rotations.is("Bruteforce")});
    private final StringBoxValue sneakMode = new StringBoxValue("Sneak Mode", "When will scaffold sneak?", this, new String[]{"None", "Edge", "Always"});
    public SliderValue<Float> minYaw = new SliderValue<>("Minimum Yaw", "What will be the minimum yaw for rotating?", this, 40f, 0f, 180f, 0);
    public SliderValue<Float> maxYaw = new SliderValue<>("Maximum Yaw", "What will be the maximum yaw for rotating?", this, 40f, 0f, 180f, 0);
    public SliderValue<Float> minPitch = new SliderValue<>("Minimum Pitch", "What will be the minimum pitch for rotating?", this, 40f, 0f, 180f, 0);
    public SliderValue<Float> maxPitch = new SliderValue<>("Maximum Pitch", "What will be the maximum pitch for rotating?", this, 40f, 0f, 180f, 0);
    public SliderValue<Float> minStartYaw = new SliderValue<>("Minimum Start Yaw", "What will be the minimum yaw for rotating?", this, 4f, 0f, 180f, 0);
    public SliderValue<Float> maxStartYaw = new SliderValue<>("Maximum Start Yaw", "What will be the maximum yaw for rotating?", this, 5f, 0f, 180f, 0);
    public SliderValue<Float> minStartPitch = new SliderValue<>("Minimum Start Pitch", "What will be the minimum pitch for rotating?", this, 4f, 0f, 180f, 0);
    public SliderValue<Float> maxStartPitch = new SliderValue<>("Maximum Start Pitch", "What will be the maximum pitch for rotating?", this, 5f, 0f, 180f, 0);
    private final CheckBoxValue tower = new CheckBoxValue("Tower", "Tower?", this, false);
    private final CheckBoxValue unSneakTower = new CheckBoxValue("Tower unSneak", "Stop sneaking when towering?", this, false, new Supplier[]{() -> !sneakMode.is("None") && tower.getValue()});
    private final StringBoxValue towerMode = new StringBoxValue("Tower Mode", "How will the module tower?", this, new String[]{"Vanilla", "BlocksMC", "Verus", "NCP", "Karhu", "Matrix", "Intave", "MMC (TEST)", "Polar"}, new Supplier[]{() -> tower.getValue()});
    public SliderValue<Float> towerMultiplier = new SliderValue<>("Intave Tower Ground Multiplier", "How Much Faster Should Intave Tower Be While On Ground?", this, 1.1f, 1f, 1.3f, 5);
    private int lastItem = -1;
    private BlockPos blockPos;
    private boolean xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx;
    private float xxxxxxxxx;
    private int rotStage;
    private int ticks;
    private boolean starting;
    private double[] lastPos = new double[3];
    private final TimeHelper timeHelper = new TimeHelper(), unsneakTimeHelper = new TimeHelper(), startingTimeHelper = new TimeHelper();
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
            Blocks.command_block, Blocks.web, Blocks.soul_sand);

    @Listen
    public final void onTick(RunTickEvent tickEvent) {
        if(startingTimeHelper.hasReached(200, true) && starting) {
            starting = false;
        }
    }
    @Listen
    public final void onUpdate(UpdateEvent updateEvent) {
        BlockPos playerPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        boolean canTower = !mc.theWorld.isAirBlock(playerPos.down(2)) || !mc.theWorld.isAirBlock(playerPos.down(1)) || !mc.theWorld.isAirBlock(playerPos.down(0));

        if(tower.getValue() && mc.gameSettings.keyBindJump.pressed && canTower) {
            switch(towerMode.getValue()) {
                case "Polar":
                    if(mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                        mc.thePlayer.motionY = 0.39;
                    }
                    break;
                case "Vanilla":
                    mc.thePlayer.motionY = 0.3;
                    break;
                case "BlocksMC":
                    if(!isMoving()) {
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.motionY = 0.42;
                        }
                        if (mc.thePlayer.motionY < 0.23) {
                            //mc.thePlayer.setPosition(mc.thePlayer.posX,  mc.thePlayer.posY, mc.thePlayer.posZ);
                            mc.thePlayer.motionY = 0.42;
                            break;
                        }
                    } else {
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.motionY = 0.4;
                            ticks = 0;
                        }
                    }

                    if(!mc.thePlayer.onGround) {
                        float speed = 0;
                        if(isMoving())
                            speed = (float) MoveUtil.getSpeed();

                        if(mc.thePlayer.ticksExisted % 5 == 0)
                            speed *= 1.4F;

                        MoveUtil.strafe(speed);
                    }
                    break;
                case "Verus":
                    if(mc.thePlayer.ticksExisted % 3 == 0) {
                        mc.thePlayer.motionY = 0.42;
                    }
                    break;
                case "NCP":
                    if (isMoving())
                        return;

                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.motionY = 0.42;
                    }
                    if (mc.thePlayer.motionY < 0.23) {
                        //mc.thePlayer.setPosition(mc.thePlayer.posX,  mc.thePlayer.posY, mc.thePlayer.posZ);
                        mc.thePlayer.motionY = 0.42;
                        break;
                    }
                    break;
                case "Matrix":
                    if (mc.thePlayer.motionY <= 0.1) {
                        mc.thePlayer.motionY = 0;
                        mc.thePlayer.onGround = true;
                    }
                    break;
                case "Intave":
                    // useless
                    mc.timer.timerSpeed = 1.004F;
                    if(mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                        float multiplier = towerMultiplier.getValue();
                        mc.thePlayer.motionX *= multiplier;
                        mc.thePlayer.motionZ *= multiplier;
                        mc.thePlayer.motionY -= 0.01;
                    } else {
                        if(mc.thePlayer.ticksExisted % 3 == 0) {
                            mc.thePlayer.motionY -= 0.0025;
                        }
                    }
                    break;
                case "Karhu":
                    ticks = mc.thePlayer.onGround ? 0 : ticks + 1;

                    if(ticks == 3)
                        mc.thePlayer.motionY = isMoving() ? -9999 : -0.0980000019;
                    break;
                case "MMC (TEST)":
                    // Kinda usefull, i mean it makes it kinda faster so
                    mc.timer.timerSpeed = 1.004F;
                    if(isMoving() && !mc.thePlayer.onGround) {
                        getGameSettings().keyBindBack.pressed = isKeyDown(getGameSettings().keyBindForward.getKeyCode());
                        getGameSettings().keyBindForward.pressed = false;
                        MoveUtil.strafe(0.2499);
                    }
            }
        }

        if(tower.getValue() && !sneakMode.is("None") && unSneakTower.getValue() && mc.gameSettings.keyBindJump.pressed) {
            mc.thePlayer.setSneaking(false);
            return;
        }

        if(slowDown.getValue()) {
            mc.thePlayer.motionX *= 0.66;
            mc.thePlayer.motionZ *= 0.66;
        }

        switch (sneakMode.getValue()) {
            case "Edge":
                if (Methods.mc.theWorld.getBlockState(new BlockPos(Methods.mc.thePlayer.posX, Methods.mc.thePlayer.posY - 1.0, Methods.mc.thePlayer.posZ)).getBlock() instanceof BlockAir && Methods.mc.thePlayer.onGround) {
                    Methods.mc.gameSettings.keyBindSneak.pressed = true;
                } else {
                    Methods.mc.gameSettings.keyBindSneak.pressed = Keyboard.isKeyDown(Methods.mc.gameSettings.keyBindSneak.getKeyCode());
                }
                break;
            case "Always":
                mc.thePlayer.setSneaking(true);
                break;
        }


        if(!sprint.getValue()) {
            if(ModuleStorage.getInstance().getModule("Sprint").isEnabled())
                ModuleStorage.getInstance().getModule("Sprint").toggle();

            mc.thePlayer.setSprinting(false);
            mc.gameSettings.keyBindSprint.pressed = false;
        } else {
            mc.thePlayer.setSprinting(isMoving());
            mc.gameSettings.keyBindSprint.pressed = true;
        }

        ItemStack heldItem = Methods.mc.thePlayer.getHeldItem();

        if (heldItem == null || !(heldItem.getItem() instanceof ItemBlock)) {
            for (int i = 0; i < 9; i++) {
                ItemStack stack = Methods.mc.thePlayer.inventory.getStackInSlot(i);

                if (stack != null && stack.stackSize != 0 && stack.getItem() instanceof ItemBlock
                        && !invalidBlocks.contains(((ItemBlock) stack.getItem()).getBlock())) {
                    if (lastItem == -1)
                        lastItem = Methods.mc.thePlayer.inventory.currentItem;

                    Methods.mc.thePlayer.inventory.currentItem = i;
                    break;
                }
            }
        }

        if(intave.getValue())
            mc.gameSettings.keyBindUseItem.pressed = true;
    }

    @Listen
    public void onSafeWalkEvent(SafeWalkEvent event) {
        if(mc.thePlayer == null && mc.theWorld == null)
            return;

        if(safeWalk.isEnabled())
            event.setSafe(true);
    }

    @Listen
    public void onClicking(ClickingEvent clickingEvent) {
        if(Methods.mc.thePlayer == null || Methods.mc.theWorld == null)
            return;

        if(intave.getValue())
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

        if (this.timeHelper.hasReached(Math.round(this.delay.getValue() + Math.random() * randomDelay.getValue()))) {
            if (Methods.mc.playerController.onPlayerRightClick(Methods.mc.thePlayer, Methods.mc.theWorld, itemstack, blockpos, objectOver.sideHit, objectOver.hitVec)) {
                Methods.mc.thePlayer.swingItem();
            }
            if (itemstack != null && itemstack.stackSize == 0) {
                Methods.mc.thePlayer.inventory.mainInventory[Methods.mc.thePlayer.inventory.currentItem] = null;
            }

            Methods.mc.sendClickBlockToController(Methods.mc.currentScreen == null && Methods.mc.gameSettings.keyBindAttack.isKeyDown() && Methods.mc.inGameHasFocus);
            timeHelper.reset();
        }
    }

    @Listen
    public final void onPacket(PacketEvent packetEvent) {

    }

    private float[] getRotations() {
        switch (this.rotations.getValue()) {
            case "Legit":
                float thingy = mc.gameSettings.keyBindJump.pressed ? 90 : (float) (81F + Math.random());
                return new float[]{mc.thePlayer.rotationYaw + 180, thingy};
            case "Bruteforce":
                for (float possibleYaw = mc.thePlayer.rotationYaw - 180 + 0; possibleYaw <= mc.thePlayer.rotationYaw + 360 - 180 ; possibleYaw += 45) {
                    for (float possiblePitch = 90; possiblePitch > 30 ; possiblePitch -= possiblePitch > (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 60 : 80) ? 1 : 10) {
                        if(RaytraceUtil.getOver(getEnumFacing(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ())), blockPos, !rayTraceMode.is("Normal"), 5, possibleYaw, possiblePitch)) {
                            return new float[]{possibleYaw, possiblePitch};
                        }
                    }
                }

                return RotationUtil.getRotation(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
            case "Reverse Simple":
                return new float[] {mc.thePlayer.rotationYaw + 180, 80.34f};
            case "BlocksMC":
                if (Methods.mc.theWorld.getBlockState(new BlockPos(Methods.mc.thePlayer.posX, Methods.mc.thePlayer.posY - 1.0, Methods.mc.thePlayer.posZ)).getBlock() instanceof BlockAir && Methods.mc.thePlayer.onGround) {
                    for (float possibleYaw = mc.thePlayer.rotationYaw - 180 + 0; possibleYaw <= mc.thePlayer.rotationYaw + 360 - 180 ; possibleYaw += 45) {
                        for (float possiblePitch = 90; possiblePitch > 30 ; possiblePitch -= possiblePitch > (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 60 : 80) ? 1 : 10) {
                            if(RaytraceUtil.getOver(getEnumFacing(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ())), blockPos, !rayTraceMode.is("Normal"), 5, possibleYaw, possiblePitch)) {
                                return new float[]{possibleYaw, possiblePitch};
                            }
                        }
                    }
                } else {
                    if(sprint.getValue()) {
                        mc.thePlayer.setSprinting(false);
                        mc.gameSettings.keyBindSprint.pressed = false;
                    }

                    return new float[] {mc.thePlayer.rotationYaw, mc.gameSettings.keyBindJump.pressed ? 90 : mc.thePlayer.rotationPitch};
                }
            case "Snap":
                if (Methods.mc.theWorld.getBlockState(new BlockPos(Methods.mc.thePlayer.posX, Methods.mc.thePlayer.posY - 1.0, Methods.mc.thePlayer.posZ)).getBlock() instanceof BlockAir && Methods.mc.thePlayer.onGround) {
                    for (float possibleYaw = mc.thePlayer.rotationYaw - 180 + 0; possibleYaw <= mc.thePlayer.rotationYaw + 360 - 180 ; possibleYaw += 45) {
                        for (float possiblePitch = 90; possiblePitch > 30 ; possiblePitch -= possiblePitch > (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 60 : 80) ? 1 : 10) {
                            if(RaytraceUtil.getOver(getEnumFacing(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ())), blockPos, !rayTraceMode.is("Normal"), 5, possibleYaw, possiblePitch)) {
                                return new float[]{possibleYaw, possiblePitch};
                            }
                        }
                    }
                } else {
                    return new float[] {mc.thePlayer.rotationYaw, mc.gameSettings.keyBindJump.pressed ? 90 : mc.thePlayer.rotationPitch};
                }
            default: {
                final float[] angles = {PlayerHandler.yaw, PlayerHandler.pitch};
                final BlockPos playerPos = new BlockPos(Methods.mc.thePlayer.posX, Methods.mc.thePlayer.posY - 0.5, Methods.mc.thePlayer.posZ);

                if (this.starting) {
                    angles[1] = 80.34f;
                    angles[0] = Methods.mc.thePlayer.rotationYaw - 180;
                    return angles;
                }

                float yaw = Methods.mc.thePlayer.rotationYaw - 180.0f;
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

                if (!(x > maxX || x < minX || z > maxZ || z < minZ)) {
                    angles[1] = PlayerHandler.pitch;
                    return angles;
                }

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
                MovingObjectPosition nearestBlock = hitBlockList.isEmpty() ? null : hitBlockList.get(0);

                if (nearestBlock != null) {
                    angles[0] = yaw;
                    pitchList.sort(Comparator.comparingDouble(RotationUtil::getDistanceToLastPitch));

                    if (!pitchList.isEmpty()) {
                        angles[1] = pitchList.get(0);
                    }
                }

                return angles;
            }
        }
    }

    @Listen
    public final void onRotation(RotationEvent rotationEvent) {
        this.blockPos = BlockUtil.getAimBlockPos();
        if (this.blockPos != null) {
            float[] rotations = this.getRotations();
            float yawSpeed;
            float pitchSpeed;
            if (starting) {
                yawSpeed = (float) RandomUtil.randomBetween(minStartYaw.getValue(), maxStartYaw.getValue());
                pitchSpeed = (float) RandomUtil.randomBetween(minStartPitch.getValue(), maxStartPitch.getValue());
            } else {
                yawSpeed = (float) RandomUtil.randomBetween(minYaw.getValue(), maxYaw.getValue());
                pitchSpeed = (float) RandomUtil.randomBetween(minPitch.getValue(), maxPitch.getValue());
            }
            final float deltaYaw = (((rotations[0] - PlayerHandler.yaw) + 540) % 360) - 180;
            final float deltaPitch = rotations[1] - PlayerHandler.pitch;

            // Ensure that deltaYaw is within the valid range of yaw values (-180 to 180)
            final float clampedDeltaYaw = MathHelper.clamp_float(deltaYaw, -180, 180);
            final float yawDistance = MathHelper.clamp_float(clampedDeltaYaw, -yawSpeed, yawSpeed);
            final float pitchDistance = MathHelper.clamp_float(deltaPitch, -pitchSpeed, pitchSpeed);

            final float targetYaw = PlayerHandler.yaw + yawDistance;
            final float targetPitch = PlayerHandler.pitch + pitchDistance;
            rotations = RotationUtil.applyMouseFix(targetYaw, targetPitch);
            rotationEvent.setYaw(rotations[0]);
            rotationEvent.setPitch(rotations[1]);
        }
    }


    /*
    @Listen
    public final void onRotation(RotationEvent rotationEvent) {
        switch(rotations.getValue()) {
            case "Random":
                // Pitch: 83.4 - 79.9
                // Yaw (90): 74.7 - 67
                rotationEvent.setPitch((float) RandomUtil.randomBetween(79.9F, 83.4F));
                double lol = RandomUtil.randomBetween(-15.3, 23);
                rotationEvent.setYaw((float) (mc.thePlayer.rotationYaw + 180 + lol));
                break;
            case "Funny":
                xxxxxxxxx += (float) (7.5 + (Math.random()));

                rotationEvent.setPitch(82);
                rotationEvent.setYaw(mc.thePlayer.rotationYaw + 180 + xxxxxxxxx);
                break;
            case "Static":
                rotationEvent.setPitch(81.943275F);
                rotationEvent.setYaw(mc.thePlayer.rotationYaw + 180);
                break;
            case "Simple":
                rotationEvent.setPitch(82);
                rotationEvent.setYaw(mc.thePlayer.rotationYaw + 180);
                break;
            case "90":
                rotationEvent.setPitch(90);
                break;
            case "Reverse":
                rotationEvent.setPitch((float) (82F + Math.random() * 4));
                rotationEvent.setYaw(mc.thePlayer.rotationYaw + 180);
                break;
            case "Snap":
                if (Methods.mc.theWorld.getBlockState(new BlockPos(Methods.mc.thePlayer.posX, Methods.mc.thePlayer.posY - 1.0, Methods.mc.thePlayer.posZ)).getBlock() instanceof BlockAir && Methods.mc.thePlayer.onGround) {
                    rotationEvent.setPitch((float) (81.943275 + Math.random() * 3));
                    rotationEvent.setYaw((float) (mc.thePlayer.rotationYaw + 180 + Math.random() * 8));
                }
                break;
            case "Grim Snap":
                if (Methods.mc.theWorld.getBlockState(new BlockPos(Methods.mc.thePlayer.posX, Methods.mc.thePlayer.posY - 1.0, Methods.mc.thePlayer.posZ)).getBlock() instanceof BlockAir && Methods.mc.thePlayer.onGround) {
                    rotationEvent.setPitch(81.943275F);
                    rotationEvent.setYaw(mc.thePlayer.rotationYaw + 180);
                    mc.thePlayer.setSprinting(false);
                }
                break;
            case "360 Bob Bridge":
                    if (Methods.mc.theWorld.getBlockState(new BlockPos(Methods.mc.thePlayer.posX, Methods.mc.thePlayer.posY - 1.0, Methods.mc.thePlayer.posZ)).getBlock() instanceof BlockAir && Methods.mc.thePlayer.onGround) {
                        if(rotStage == 0)
                            rotStage = 1;

                        rotationEvent.setPitch(81.943275F);
                    }

                    switch (rotStage) {
                        case 4:
                            rotationEvent.setYaw(mc.thePlayer.rotationYaw + 180);
                            rotStage = 0;
                            break;
                        case 3:
                            rotationEvent.setYaw(mc.thePlayer.rotationYaw + 90);
                            rotStage += 1;
                            break;
                        case 2:
                            rotationEvent.setYaw(mc.thePlayer.rotationYaw);
                            rotStage += 1;
                            break;
                        case 1:
                            rotationEvent.setYaw(mc.thePlayer.rotationYaw - 90);
                            rotStage += 1;
                            break;
                    }
                    break;
            case "Bruteforce":
                for (float possibleYaw = mc.thePlayer.rotationYaw - 180 + 0; possibleYaw <= mc.thePlayer.rotationYaw + 360 - 180 ; possibleYaw += 45) {
                    for (float possiblePitch = 90; possiblePitch > 30 ; possiblePitch -= possiblePitch > (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 60 : 80) ? 1 : 10) {
                        if(RaytraceUtil.getOver(getEnumFacing(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ())), new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ()), !rayTraceMode.is("Normal"), 5, possibleYaw, possiblePitch)) {
                            rotationEvent.setPitch(possiblePitch);
                            rotationEvent.setYaw(possibleYaw);
                        }
                    }
                }
                break;

        }

        if(mc.gameSettings.keyBindJump.pressed && !mc.thePlayer.onGround && !isMoving()) {
            rotationEvent.setPitch((float) (89 + Math.random()));
        }
    }
     */

    public EnumFacing getEnumFacing(final Vec3 position) {
        for (int coord : new int[]{-1, 1}) {
            if (!(WorldUtil.getBlock(position.xCoord + coord, position.yCoord, position.zCoord) instanceof BlockAir)) {
                return coord > 0 ? EnumFacing.WEST : EnumFacing.EAST;
            }
            if (!(WorldUtil.getBlock(position.xCoord, position.yCoord + coord, position.zCoord) instanceof BlockAir)) {
                return coord < 0 ? EnumFacing.UP : null;
            }
            if (!(WorldUtil.getBlock(position.xCoord, position.yCoord, position.zCoord + coord) instanceof BlockAir)) {
                return coord < 0 ? EnumFacing.SOUTH : EnumFacing.NORTH;
            }
        }
        return null;
    }


    @Override
    public void onEnable() {
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
}

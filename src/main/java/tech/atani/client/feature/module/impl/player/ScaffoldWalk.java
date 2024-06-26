package tech.atani.client.feature.module.impl.player;

import com.google.common.base.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.value.impl.CheckBoxValue;
import tech.atani.client.feature.value.impl.SliderValue;
import tech.atani.client.feature.value.impl.StringBoxValue;
import tech.atani.client.listener.event.minecraft.game.RunTickEvent;
import tech.atani.client.listener.event.minecraft.input.ClickingEvent;
import tech.atani.client.listener.event.minecraft.player.movement.DirectionSprintCheckEvent;
import tech.atani.client.listener.event.minecraft.player.movement.SafeWalkEvent;
import tech.atani.client.listener.event.minecraft.player.movement.SilentMoveEvent;
import tech.atani.client.listener.event.minecraft.player.movement.UpdateEvent;
import tech.atani.client.listener.event.minecraft.player.rotation.RotationEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.utility.interfaces.Methods;
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

@ModuleData(name = "ScaffoldWalk", description = "Bridging automatically", category = Category.PLAYER)
public class ScaffoldWalk extends Module {
    // bypasses shit acs
    private final StringBoxValue rotations = new StringBoxValue("Rotations", "How will the scaffold rotate?", this, new String[]{"Reverse Advanced", "Reverse Simple", "Bruteforce", "Legit"});
    private final StringBoxValue rayTraceMode = new StringBoxValue("Ray-Trace Mode", "What will the scaffold raytrace mode be?", this, new String[]{"Normal", "Strict"}, new Supplier[]{() -> rotations.is("Bruteforce")});
    public SliderValue<Float> minYaw = new SliderValue<>("Minimum Yaw", "What will be the minimum yaw for rotating?", this, 40f, 0f, 180f, 0);
    public SliderValue<Float> maxYaw = new SliderValue<>("Maximum Yaw", "What will be the maximum yaw for rotating?", this, 40f, 0f, 180f, 0);
    public SliderValue<Float> minPitch = new SliderValue<>("Minimum Pitch", "What will be the minimum pitch for rotating?", this, 40f, 0f, 180f, 0);
    public SliderValue<Float> maxPitch = new SliderValue<>("Maximum Pitch", "What will be the maximum pitch for rotating?", this, 40f, 0f, 180f, 0);    private final CheckBoxValue swinging = new CheckBoxValue("Swing Client-Side", "Swing client-side when placing blocks?", this, true);
    private final SliderValue<Float> timerSpeed = new SliderValue<>("Timer", "What will the timer be while using scaffold?", this, 1f, 0.1f, 5.0f, 3);
    public SliderValue<Float> minStartYaw = new SliderValue<>("Minimum Start Yaw", "What will be the minimum yaw for rotating?", this, 4f, 0f, 180f, 0);
    public SliderValue<Float> maxStartYaw = new SliderValue<>("Maximum Start Yaw", "What will be the maximum yaw for rotating?", this, 5f, 0f, 180f, 0);
    public SliderValue<Float> minStartPitch = new SliderValue<>("Minimum Start Pitch", "What will be the minimum pitch for rotating?", this, 4f, 0f, 180f, 0);
    public SliderValue<Float> maxStartPitch = new SliderValue<>("Maximum Start Pitch", "What will be the maximum pitch for rotating?", this, 5f, 0f, 180f, 0);
    private final CheckBoxValue intave = new CheckBoxValue("Intave", "Intave CPS Fix?", this, false);
    private final CheckBoxValue sprint = new CheckBoxValue("Sprint", "Allow sprinting?", this, false);
    private final CheckBoxValue switchItems = new CheckBoxValue("Switch Items", "Switch to blocks?", this, true);
    private final CheckBoxValue reverseMovement = new CheckBoxValue("Reverse Movement", "Reverse your movement?", this, false);
    private final CheckBoxValue addStrafe = new CheckBoxValue("Add Strafe", "Strafe a little?", this, false);
    private final SliderValue<Long> delay = new SliderValue<>("Delay", "What will be the delay between placing?", this, 0L, 0L, 1000L, 0);
    private final CheckBoxValue sneak = new CheckBoxValue("Sneak", "Sneak?", this, false);
    private final CheckBoxValue safeWalk = new CheckBoxValue("SafeWalk", "Safewalk?", this, false);
    private final StringBoxValue sneakMode = new StringBoxValue("Sneak Mode", "When will the module sneak?", this, new String[]{"Edge", "Constant"}, new Supplier[]{() -> sneak.getValue()});
    private final CheckBoxValue tower = new CheckBoxValue("Tower", "Tower?", this, false);
    private final CheckBoxValue unSneakTower = new CheckBoxValue("Tower unSneak", "Stop sneaking when towering?", this, false, new Supplier[]{() -> sneak.getValue() && tower.getValue()});
    private final StringBoxValue towerMode = new StringBoxValue("Tower Mode", "How will the module tower?", this, new String[]{"Vanilla", "Verus", "NCP", "Karhu", "Matrix", "Intave", "MMC (TEST)"}, new Supplier[]{() -> tower.getValue()});
    private final SliderValue<Long> unSneakDelay = new SliderValue<Long>("Unsneak delay", "What will be the delay between unsneaking?", this, 0L, 0L, 1000L, 0, new Supplier[]{() -> sneak.getValue() && sneakMode.is("Edge")});
    private final CheckBoxValue verusBoost = new CheckBoxValue("Verus Speed Boost", "Add speed boost?", this, false);
    private final CheckBoxValue intaveBoost = new CheckBoxValue("Intave Speed Boost", "Add speed boost?", this, false);
    private final CheckBoxValue intaveBoost2 = new CheckBoxValue("Intave Speed Boost 2", "Add speed boost 2?", this, false);
    public SliderValue<Float> towerMultiplier = new SliderValue<>("Intave Tower Ground Multiplier", "How Much Faster Should Intave Tower Be While On Ground?", this, 1.1f, 1f, 1.3f, 5);
    private final TimeHelper timeHelper = new TimeHelper(), unsneakTimeHelper = new TimeHelper(), startingTimeHelper = new TimeHelper();
    private double[] lastPos = new double[3];
    private int lastItem = -1;
    private BlockPos blockPos;
    private boolean starting;
    private int verusTicks;
    private boolean placed;
    private int ticks;

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

        mc.thePlayer.speedInAir = 0.02F;
        mc.timer.timerSpeed = timerSpeed.getValue();

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
            float yawSpeed;
            float pitchSpeed;
            if(starting) {
                yawSpeed = (float) RandomUtil.randomBetween(minStartYaw.getValue(), maxStartYaw.getValue());
                pitchSpeed = (float) RandomUtil.randomBetween(minStartPitch.getValue(), maxStartPitch.getValue());
            } else {
                yawSpeed = (float) RandomUtil.randomBetween(minYaw.getValue(), maxYaw.getValue());
                pitchSpeed = (float) RandomUtil.randomBetween(minPitch.getValue(), maxPitch.getValue());
            }
            final float deltaYaw = (((rotations[0] - PlayerHandler.yaw) + 540) % 360) - 180;
            final float deltaPitch = rotations[1] - PlayerHandler.pitch;
            final float yawDistance = MathHelper.clamp_float(deltaYaw, -yawSpeed, yawSpeed);
            final float pitchDistance = MathHelper.clamp_float(deltaPitch, -pitchSpeed, pitchSpeed);
            final float targetYaw = PlayerHandler.yaw + yawDistance, targetPitch = PlayerHandler.pitch + pitchDistance;
            rotations = RotationUtil.applyMouseFix(targetYaw, targetPitch);
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
            Blocks.command_block, Blocks.web, Blocks.soul_sand);

    @Listen
    public final void onUpdate(UpdateEvent updateEvent) {
        if(mc.gameSettings.limitFramerate < 145)
            mc.gameSettings.limitFramerate = 145;

        if(intave.getValue())
            mc.gameSettings.keyBindUseItem.pressed = true;

        if(verusBoost.getValue()) {
            if(!isMoving())
                return;
            
            if(mc.thePlayer.onGround) {
                verusTicks = 0;
                mc.thePlayer.jump();
            } else {
                verusTicks++;

                if(verusTicks > 2)
                    return;

                mc.thePlayer.motionY = 0;
                MoveUtil.strafe(0.475);
                mc.thePlayer.onGround = true;
            }
        }

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

        getPlayer().setSprinting(sprint.getValue());

        if(intaveBoost2.getValue()) {
            if(mc.thePlayer.isSneaking()) {
                mc.thePlayer.motionX *= 1.01F;
                mc.thePlayer.motionZ *= 1.01F;
            }

            if(mc.thePlayer.ticksExisted % 3 == 0) {
                mc.thePlayer.motionX *= 1.005F;
                mc.thePlayer.motionZ *= 1.005F;
                mc.timer.timerSpeed = 1.17F;
            } else {
                mc.timer.timerSpeed = 1;
            }
        }

        if (reverseMovement.getValue()) {
            getGameSettings().keyBindBack.pressed = isKeyDown(getGameSettings().keyBindForward.getKeyCode());
            getGameSettings().keyBindForward.pressed = false;
        }

        if(tower.getValue() && mc.gameSettings.keyBindJump.pressed && mc.thePlayer.fallDistance < 1.5) {
            switch(towerMode.getValue()) {
                case "Vanilla":
                    mc.thePlayer.motionY = 0.3;
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
                        mc.thePlayer.setPosition(mc.thePlayer.posX, (int)mc.thePlayer.posY, mc.thePlayer.posZ);
                        mc.thePlayer.motionY = 0.42;
                        break;
                    }
                    break;
                case "Matrix":
                    if (mc.thePlayer.motionY < 0.2) {
                        mc.thePlayer.motionY = 0.42F;
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
                    if(ticks == 3 && !isMoving())
                        mc.thePlayer.motionY = -0.0980000019;
                    else if(ticks == 3 && isMoving())
                        mc.thePlayer.motionY = -9999;
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

        if(tower.getValue() && sneak.getValue() && unSneakTower.getValue() && mc.gameSettings.keyBindJump.pressed) {
            mc.thePlayer.setSneaking(false);
            return;
        }
        if(sneak.getValue() && sneakMode.is("Edge")) {
            if (unSneakDelay.getValue() == 0 || unsneakTimeHelper.hasReached(unSneakDelay.getValue())) {
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

            placed = true;
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
        if(mc.thePlayer.onGround && intaveBoost.getValue()) {
            mc.thePlayer.setSprinting(true);
            mc.thePlayer.jump();
            mc.thePlayer.jump();
            mc.thePlayer.setSprinting(true);
            /*
            mc.thePlayer.jump();
            mc.thePlayer.jump();
            mc.timer.timerSpeed = 1.089F;
            intaveBoosted += 1;
            PlayerUtil.addChatMessgae("BOOSTED. Boost times: " + intaveBoosted, true);
             */
        }
        timeHelper.reset();
        startingTimeHelper.reset();
        starting = true;
    }

    @Override
    public void onDisable() {
        if(Methods.mc.thePlayer == null || Methods.mc.theWorld == null) {
            return;
        }

        mc.thePlayer.speedInAir = 0.02F;
        mc.timer.timerSpeed = 1;

        if(this.lastItem != -1) {
            Methods.mc.thePlayer.inventory.currentItem = this.lastItem;
            this.lastItem = -1;
        }

        getGameSettings().keyBindSneak.pressed = isKeyDown(getGameSettings().keyBindSneak.getKeyCode());
        getGameSettings().keyBindBack.pressed = isKeyDown(getGameSettings().keyBindBack.getKeyCode());
        getGameSettings().keyBindForward.pressed = isKeyDown(getGameSettings().keyBindForward.getKeyCode());
    }

    public EnumFacing getEnumFacing(final Vec3 position) {
        for (int x2 = -1; x2 <= 1; x2 += 2) {
            if (!(WorldUtil.getBlock(position.xCoord + x2, position.yCoord, position.zCoord) instanceof BlockAir)) {
                if (x2 > 0) {
                    return EnumFacing.WEST;
                } else {
                    return EnumFacing.EAST;
                }
            }
        }

        for (int y2 = -1; y2 <= 1; y2 += 2) {
            if (!(WorldUtil.getBlock(position.xCoord, position.yCoord + y2, position.zCoord) instanceof BlockAir)) {
                if (y2 < 0) {
                    return EnumFacing.UP;
                }
            }
        }

        for (int z2 = -1; z2 <= 1; z2 += 2) {
            if (!(WorldUtil.getBlock(position.xCoord, position.yCoord, position.zCoord + z2) instanceof BlockAir)) {
                if (z2 < 0) {
                    return EnumFacing.SOUTH;
                } else {
                    return EnumFacing.NORTH;
                }
            }
        }

        return null;
    }

    private float[] getRotations() {
        switch (this.rotations.getValue()) {
            case "Legit":
                float thingy = (float) (77.5F + Math.random());
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
            default: {
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
    }

}

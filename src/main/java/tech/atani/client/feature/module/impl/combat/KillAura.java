package tech.atani.client.feature.module.impl.combat;

import com.google.common.base.Supplier;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Keyboard;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.listener.event.minecraft.game.PostTickEvent;
import tech.atani.client.utility.interfaces.Methods;
import tech.atani.client.feature.value.impl.CheckBoxValue;
import tech.atani.client.feature.value.impl.SliderValue;
import tech.atani.client.feature.value.impl.StringBoxValue;
import tech.atani.client.listener.event.minecraft.input.ClickingEvent;
import tech.atani.client.listener.event.minecraft.player.rotation.RayTraceRangeEvent;
import tech.atani.client.listener.event.minecraft.player.rotation.RotationEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.utility.player.PlayerHandler;
import tech.atani.client.utility.player.combat.FightUtil;
import tech.atani.client.utility.math.time.TimeHelper;
import tech.atani.client.utility.player.rotation.RotationUtil;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ModuleData(name = "KillAura", description = "Attacks people", category = Category.COMBAT, key = Keyboard.KEY_R)
public class KillAura extends Module {

    public SliderValue<Float> findRange = new SliderValue<>("Search Range", "What'll be the range for searching for targets?", this, 4f, 3f, 10f, 1);
    public StringBoxValue targetMode = new StringBoxValue("Target Mode", "How will the aura search for targets?", this, new String[]{"Single", "Hybrid", "Switch", "Multi"});
    public StringBoxValue priority = new StringBoxValue("Priority", "How will the aura sort targets?", this, new String[]{"Health", "Distance"});
    public SliderValue<Long> switchDelay = new SliderValue<Long>("Switch Delay", "How long will it take to switch between targets?", this, 300L, 0L, 1000L, 0, new Supplier[]{() -> targetMode.is("Switch")});
    public CheckBoxValue players = new CheckBoxValue("Players", "Attack Players?", this, true);
    public CheckBoxValue animals = new CheckBoxValue("Animals", "Attack Animals?", this, true);
    public CheckBoxValue monsters = new CheckBoxValue("Monsters", "Attack Monsters?", this, true);
    public CheckBoxValue invisible = new CheckBoxValue("Invisibles", "Attack Invisibles?", this, true);
    public CheckBoxValue walls = new CheckBoxValue("Walls", "Check for walls?", this, true);
    public CheckBoxValue autoBlock = new CheckBoxValue("Auto Block", "Should the aura block on hit?", this, true);
    public StringBoxValue autoBlockMode = new StringBoxValue("Auto Block Mode", "Which mode should the autoblock use?", this, new String[] {"Vanilla", "NCP", "AAC", "GrimAC", "MosPixel", "Intave", "Matrix", "Hold"}, new Supplier[]{() -> autoBlock.getValue()});
    public SliderValue<Integer> fov = new SliderValue<>("FOV", "What'll the be fov for allowing targets?", this, 90, 0, 180, 0);
    public SliderValue<Float> attackRange = new SliderValue<>("Attack Range", "What'll be the range for Attacking?", this, 3f, 3f, 6f, 1);
    public CheckBoxValue advancedRange = new CheckBoxValue("Advanced Range", "Make attack range more advanced?", this, true);
    public SliderValue<Float> groundRange = new SliderValue<>("Ground Attack Range", "What'll be the range for Attacking while on ground?", this, 3f, 3f, 6f, 1, new Supplier[]{() -> advancedRange.getValue()});
    public SliderValue<Float> airRange = new SliderValue<>("Air Attack Range", "What'll be the range for Attacking while in air?", this, 3f, 3f, 6f, 1, new Supplier[]{() -> advancedRange.getValue()});
    public SliderValue<Float> sprintRange = new SliderValue<>("Sprint Attack Range", "What'll be the range for Attacking while sprinting?", this, 3f, 3f, 6f, 1, new Supplier[]{() -> advancedRange.getValue()});
    public CheckBoxValue fixServersSideMisplace = new CheckBoxValue("Fix Misplace", "Fix Server-Side Misplace?", this, true);
    public CheckBoxValue waitBeforeAttack = new CheckBoxValue("Delay Clicks", "Wait before attacking the target?", this, true);
    public StringBoxValue waitMode = new StringBoxValue("Wait for", "For what will the module wait before attacking?", this, new String[]{"CPS", "1.9"}, new Supplier[]{() -> waitBeforeAttack.getValue()});
    public SliderValue<Float> cps = new SliderValue<Float>("CPS", "How much will the killaura click every second?", this, 12f, 0f, 20f, 1, new Supplier[]{() -> waitBeforeAttack.getValue() && waitMode.is("CPS")});
    public SliderValue<Float> timerSpeed = new SliderValue<Float>("Attack Timer Speed", "What will the timer be while attacking?", this, 1.0f, 0.5f, 5f, 3, new Supplier[]{() -> waitBeforeAttack.getValue() && waitMode.is("CPS")});
    public StringBoxValue attackMode = new StringBoxValue("Attack Mode", "How should KillAura attack?", this, new String[] {"Normal", "Packet"});
    public CheckBoxValue randomizeCps = new CheckBoxValue("Randomize CPS", "Randomize CPS Value to bypass anticheats?", this, true, new Supplier[]{() -> waitBeforeAttack.getValue() && waitMode.is("CPS")});
    public CheckBoxValue lockView = new CheckBoxValue("Lock-view", "Rotate non-silently?", this, false);
    public CheckBoxValue snapYaw = new CheckBoxValue("Snap Yaw", "Skip smoothing out yaw rotations?", this, false);
    public CheckBoxValue snapPitch = new CheckBoxValue("Snap Pitch", "Skip smoothing out pitch rotations?", this, false);
    public StringBoxValue aimVector = new StringBoxValue("Aim Vector", "Where to aim?", this, new String[]{"Perfect", "Bruteforce", "Head", "Torso", "Feet", "Custom", "Random"});
    public SliderValue<Float> heightDivisor = new SliderValue<Float>("Height Divisor", "By what amount to divide the height?", this, 2f, 1f, 10f, 1, new Supplier[]{() -> aimVector.getValue().equalsIgnoreCase("Custom")});
    public CheckBoxValue skipUnnecessaryRotations = new CheckBoxValue("Necessary Rotations", "Rotate only if necessary?", this, false);
    public StringBoxValue unnecessaryRotations = new StringBoxValue("Necessary Mode", "What rotations to skip?", this, new String[]{"Both", "Yaw", "Pitch"}, new Supplier[]{() -> skipUnnecessaryRotations.getValue()});
    public CheckBoxValue skipIfNear = new CheckBoxValue("Skip If Near", "Rotate only if far enough?", this, true, new Supplier[]{() -> skipUnnecessaryRotations.getValue()});
    public SliderValue<Float> nearDistance = new SliderValue<Float>("Near Distance", "How near to skip rotations?", this, 0.5F, 0F, 0.5F, 2, new Supplier[]{() -> skipUnnecessaryRotations.getValue() && skipIfNear.getValue()});
    public SliderValue<Float> minYaw = new SliderValue<>("Minimum Yaw", "What will be the minimum yaw for rotating?", this, 40f, 0f, 180f, 0);
    public SliderValue<Float> maxYaw = new SliderValue<>("Maximum Yaw", "What will be the maximum yaw for rotating?", this, 40f, 0f, 180f, 0);
    public SliderValue<Float> minPitch = new SliderValue<>("Minimum Pitch", "What will be the minimum pitch for rotating?", this, 40f, 0f, 180f, 0);
    public SliderValue<Float> maxPitch = new SliderValue<>("Maximum Pitch", "What will be the maximum pitch for rotating?", this, 40f, 0f, 180f, 0);
    public CheckBoxValue mouseFix = new CheckBoxValue("Mouse Fix", "Simulate mouse movements in rotations?", this, true);
    public CheckBoxValue heuristics = new CheckBoxValue("Heuristics", "Bypass heuristics checks?", this, true);
    public SliderValue<Float> minRandomYaw = new SliderValue<>("Min Random Yaw", "What'll be the minimum randomization for yaw?", this, 0f, -1f, 0f, 2);
    public SliderValue<Float> maxRandomYaw = new SliderValue<>("Max Random Yaw", "What'll be the maximum randomization for yaw?", this, 0f, 0f, 1f, 2);
    public SliderValue<Float> minRandomPitch = new SliderValue<>("Min Random Pitch", "What'll be the minimum randomization for pitch?", this, 0f, -1f, 0f, 2);
    public SliderValue<Float> maxRandomPitch = new SliderValue<>("Max Random Pitch", "What'll be the maximum randomization for pitch?", this, 0f, 0f, 1f, 2);
    public CheckBoxValue prediction = new CheckBoxValue("Prediction", "Predict the players position?", this, false);

    // Targets
    public static EntityLivingBase curEntity;
    private int currentIndex;
    private TimeHelper switchTimer = new TimeHelper();

    // Range
    private double correctedRange = 0D;

    // Clicking
    private TimeHelper attackTimer = new TimeHelper();
    private double cpsDelay = 0;

    // Rotations
    private float yawRot, pitchRot;

    // AutoBlock
    private boolean wasHolding;

    @Override
    public String getSuffix() {
    	return targetMode.getValue();
    }

    private final class AttackRangeSorter implements Comparator<EntityLivingBase> {
        public int compare(EntityLivingBase o1, EntityLivingBase o2) {
            return Double.compare(FightUtil.getRange(o1) <= attackRange.getValue() ? 0 : 1,
                    FightUtil.getRange(o2) <= attackRange.getValue() ? 0 : 1);
        }
    }

    private final class HealthSorter implements Comparator<EntityLivingBase> {
        public int compare(EntityLivingBase o1, EntityLivingBase o2) {
            return Double.compare(FightUtil.getEffectiveHealth(o1), FightUtil.getEffectiveHealth(o2));
        }
    }

    private final class DistanceSorter implements Comparator<EntityLivingBase> {
        public int compare(EntityLivingBase o1, EntityLivingBase o2) {
            return Double.compare(Methods.mc.thePlayer.getDistanceToEntity(o1), Methods.mc.thePlayer.getDistanceToEntity(o2));
        }
    }

    @Listen
    public void onRayTrace(RayTraceRangeEvent rayTraceRangeEvent) {
        if (curEntity != null) {
            double range2 = mc.thePlayer.isSprinting() ? sprintRange.getValue() : mc.thePlayer.onGround ? groundRange.getValue() : airRange.getValue();
            correctedRange = (advancedRange.getValue() ? range2 : attackRange.getValue()) + 0.00256f;

            if (fixServersSideMisplace.getValue() && (Methods.mc.thePlayer.getHorizontalFacing() == EnumFacing.NORTH || Methods.mc.thePlayer.getHorizontalFacing() == EnumFacing.WEST))
                correctedRange += 0.010625f * 2.0f;

            rayTraceRangeEvent.setRange((float) correctedRange);
            rayTraceRangeEvent.setBlockReachDistance((float) Math.max(Methods.mc.playerController.getBlockReachDistance(), correctedRange));
        }
    }

    @Listen
    public void onPostTickEvent(PostTickEvent postTickEvent) {
        if (curEntity != null) {
            mc.timer.timerSpeed = timerSpeed.getValue();
        } else {
            mc.timer.timerSpeed = 1;
        }

        if(autoBlock.getValue() && autoBlockMode.is("MosPixel") && curEntity != null) {
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-4, -7, -10), EnumFacing.SOUTH));
            Methods.mc.playerController.interactWithEntitySendPacket(Methods.mc.thePlayer, curEntity);
        }

        if (mc.thePlayer == null)
            return;

        boolean entityIsValid = FightUtil.isValid(curEntity, findRange.getValue(), players.getValue(), animals.getValue(), monsters.getValue(), invisible.getValue());

        List<EntityLivingBase> targets = FightUtil.getMultipleTargets(findRange.getValue(), players.getValue(), animals.getValue(), walls.getValue(), monsters.getValue(), invisible.getValue());
        int targetsSize = targets.size();

        Map<String, Comparator<EntityLivingBase>> comparatorMap = new HashMap<>();
        comparatorMap.put("Distance", new DistanceSorter());
        comparatorMap.put("Health", new HealthSorter());

        targets.sort(comparatorMap.getOrDefault(this.priority.getValue(), new AttackRangeSorter()));
        targets.sort(new AttackRangeSorter());

        if (targets.isEmpty() || (curEntity != null && !targets.contains(curEntity))) {
            cpsDelay = 0L;
            curEntity = null;
            return;
        }

        switch (targetMode.getValue()) {
            case "Hybrid":
                curEntity = targets.get(0);
                break;
            case "Single":
                if (curEntity == null || !entityIsValid) {
                    curEntity = targets.get(0);
                }
                break;
            case "Multi":
            case "Switch":
                long switchDelay = targetMode.is("Multi") ? 0 : this.switchDelay.getValue();
                if (!this.switchTimer.hasReached(switchDelay)) {
                    if (curEntity == null || !entityIsValid) {
                        curEntity = targets.get(0);
                    }
                    return;
                }

                if (curEntity != null && entityIsValid && targetsSize == 1) {
                    return;
                } else if (curEntity == null) {
                    curEntity = targets.get(0);
                } else if (targetsSize > 1) {
                    int maxIndex = targetsSize - 1;
                    if (this.currentIndex >= maxIndex) {
                        this.currentIndex = 0;
                    } else {
                        this.currentIndex += 1;
                    }

                    if (targets.get(currentIndex) != null && targets.get(currentIndex) != curEntity) {
                        curEntity = targets.get(currentIndex);
                        this.switchTimer.reset();
                    }
                } else {
                    curEntity = null;
                }
                break;
        }
    }

    @Listen
    public final void onRotation(RotationEvent rotationEvent) {
        if (curEntity == null && autoBlockMode.is("Hold") && wasHolding) {
            mc.gameSettings.keyBindUseItem.pressed = false;
            wasHolding = false;
            return;
        }

        if (curEntity != null) {
            float[] rotations = RotationUtil.getRotation(curEntity, aimVector.getValue(), heightDivisor.getValue(), mouseFix.getValue(), heuristics.getValue(), minRandomYaw.getValue(), maxRandomYaw.getValue(), minRandomPitch.getValue(), maxRandomPitch.getValue(), this.prediction.getValue(), this.minYaw.getValue(), this.maxYaw.getValue(), this.minPitch.getValue(), this.maxPitch.getValue(), snapYaw.getValue(), snapPitch.getValue());

            if (skipUnnecessaryRotations.getValue()) {
                MovingObjectPosition movingObjectPosition = mc.objectMouseOver;
                if (movingObjectPosition == null || (movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY || movingObjectPosition.entityHit == curEntity) || (skipIfNear.getValue() && FightUtil.getRange(curEntity) <= nearDistance.getValue())) {
                    if (unnecessaryRotations.getValue().equalsIgnoreCase("Yaw") || unnecessaryRotations.getValue().equalsIgnoreCase("Both"))
                        yawRot = PlayerHandler.yaw;
                    if (unnecessaryRotations.getValue().equalsIgnoreCase("Pitch") || unnecessaryRotations.getValue().equalsIgnoreCase("Both"))
                        pitchRot = PlayerHandler.pitch;
                } else {
                    yawRot = rotations[0];
                    pitchRot = rotations[1];
                }
            } else {
                yawRot = rotations[0];
                pitchRot = rotations[1];
            }

            rotationEvent.setYaw(yawRot);
            rotationEvent.setPitch(pitchRot);

            if (this.lockView.getValue()) {
                Methods.mc.thePlayer.rotationYaw = yawRot;
                Methods.mc.thePlayer.rotationPitch = pitchRot;
            }
        }
    }

    @Listen
    public final void onClick(ClickingEvent clickingEvent) {
        if (curEntity != null) {
            switch (this.waitMode.getValue()) {
                case "1.9":
                    cpsDelay = getAttackSpeed(Methods.mc.thePlayer.getHeldItem(), true);
                    break;
            }

            if (!this.waitBeforeAttack.getValue() || this.attackTimer.hasReached(cpsDelay)) {
                switch (this.waitMode.getValue()) {
                    case "CPS":
                        double cps = this.cps.getValue() > 10 ? this.cps.getValue() + 5 : this.cps.getValue();
                        long calcCPS = (long) (1000 / cps);
                        if (this.randomizeCps.getValue()) {
                            try {
                                calcCPS += SecureRandom.getInstanceStrong().nextGaussian() * 50;
                            } catch (NoSuchAlgorithmException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        cpsDelay = calcCPS;
                        break;
                }

                MovingObjectPosition objectPosition = Methods.mc.objectMouseOver;
                if (objectPosition != null && objectPosition.entityHit != null && objectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                    if (autoBlock.getValue()) {
                        ItemStack currentItem = Methods.mc.thePlayer.getHeldItem();
                        if (currentItem != null && currentItem.getItem() instanceof ItemSword) {
                            switch (autoBlockMode.getValue()) {
                                case "Vanilla":
                                    Methods.mc.playerController.sendUseItem(Methods.mc.thePlayer, Methods.mc.theWorld, currentItem);
                                    break;
                                case "NCP":
                                    Methods.mc.thePlayer.setItemInUse(currentItem, 32767);
                                    break;
                                case "AAC":
                                case "GrimAC":
                                case "Intave":
                                case "Matrix":
                                    Methods.mc.playerController.interactWithEntitySendPacket(Methods.mc.thePlayer, objectPosition.entityHit);
                                    Methods.mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(currentItem));
                                    break;
                                case "Hold":
                                    if (!wasHolding)
                                        wasHolding = true;
                                    mc.gameSettings.keyBindUseItem.pressed = true;
                                    break;
                            }
                        }
                    }

                    if(mc.pointedEntity == null)
                        return;

                    Methods.mc.thePlayer.swingItem();
                    switch (attackMode.getValue()) {
                        case "Normal":
                            Methods.mc.playerController.attackEntity(Methods.mc.thePlayer, objectPosition.entityHit);
                            break;
                        case "Packet":
                            mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(curEntity, C02PacketUseEntity.Action.ATTACK));
                            break;
                    }
                }
                this.attackTimer.reset();
            }
        } else {
            cpsDelay = 0L;
        }
    }

    public static long getAttackSpeed(final ItemStack itemStack, final boolean responsive) {
        double baseSpeed = 250;

        if (!responsive || itemStack == null || !(itemStack.getItem() instanceof ItemTool || itemStack.getItem() instanceof ItemSword || itemStack.getItem() instanceof ItemHoe)) {
            return Long.MAX_VALUE;
        }

        Item item = itemStack.getItem();

        if (item instanceof ItemSword) {
            baseSpeed = 625;
        } else if (item instanceof ItemSpade) {
            baseSpeed = 1000;
        } else if (item instanceof ItemPickaxe) {
            baseSpeed = 833.333333333333333;
        } else if (item instanceof ItemAxe) {
            switch (item.getUnlocalizedName()) {
                case "item.wooden_axe":
                case "item.stone_axe":
                    baseSpeed = 1250;
                    break;
                case "item.iron_axe":
                    baseSpeed = 1111.111111111111111;
                    break;
                case "item.diamond_axe":
                case "item.golden_axe":
                    baseSpeed = 1000;
                    break;
            }
        } else if (item instanceof ItemHoe) {
            switch (item.getUnlocalizedName()) {
                case "item.wooden_hoe":
                    baseSpeed = 1000;
                    break;
                case "item.stone_hoe":
                    baseSpeed = 500;
                    break;
                case "item.iron_hoe":
                    baseSpeed = 333.333333333333333;
                    break;
                case "item.diamond_hoe":
                    baseSpeed = 250;
                    break;
                case "item.golden_hoe":
                    baseSpeed = 1000;
                    break;
            }
        }

        if (Methods.mc.thePlayer.isPotionActive(Potion.digSpeed)) {
            baseSpeed *= 1.0 + 0.1 * (Methods.mc.thePlayer.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1);
        }

        return Math.round(baseSpeed);
    }

    @Override
    public void onEnable() { curEntity = null; }

    @Override
    public void onDisable() {
        if(Methods.mc.thePlayer == null || Methods.mc.theWorld == null) {
            return;
        }

        if(autoBlock.getValue() && autoBlockMode.is("Hold") && wasHolding) {
            Methods.mc.gameSettings.keyBindUseItem.pressed = false;
        }
        curEntity = null;
        wasHolding = false;
    }
}

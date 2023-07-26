package wtf.atani.module.impl.combat;

import com.google.common.base.Supplier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Keyboard;
import wtf.atani.event.events.ClickingEvent;
import wtf.atani.event.events.RotationEvent;
import wtf.atani.event.events.UpdateMotionEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.utils.combat.FightUtil;
import wtf.atani.utils.math.random.RandomUtil;
import wtf.atani.utils.math.time.TimeHelper;
import wtf.atani.utils.player.PlayerHandler;
import wtf.atani.utils.player.RotationUtil;
import wtf.atani.utils.player.rayTrace.RaytraceUtil;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.SliderValue;
import wtf.atani.value.impl.StringBoxValue;

import java.security.SecureRandom;
import java.util.List;

@ModuleInfo(name = "KillAura", description = "Attacks people", category = Category.COMBAT, key = Keyboard.KEY_R)
public class KillAura extends Module {

    public SliderValue<Float> findRange = new SliderValue<>("Search Range", "What'll be the range for searching for targets?", this, 4f, 3f, 10f, 1);
    public StringBoxValue targetMode = new StringBoxValue("Target Mode", "How will the aura search for targets?", this, new String[]{"Single", "Hybrid", "Switch", "Multi"});
    public SliderValue<Long> switchDelay = new SliderValue<>("Switch Delay", "How long will it take to switch between targets?", this, 300L, 0L, 1000L, 0, new Supplier[]{() -> targetMode.getValue().equalsIgnoreCase("Switch")});
    public CheckBoxValue players = new CheckBoxValue("Players", "Attack Players?", this, true);
    public CheckBoxValue animals = new CheckBoxValue("Animals", "Attack Animals", this, true);
    public CheckBoxValue monsters = new CheckBoxValue("Monsters", "Attack Monsters", this, true);
    public CheckBoxValue invisible = new CheckBoxValue("Invisibles", "Attack Invisibles?", this, true);
    public CheckBoxValue walls = new CheckBoxValue("Walls", "Check for walls?", this, true);
    public SliderValue<Float> rotationRange = new SliderValue<>("Rotation Range", "What'll be the range for rotating?", this, 4f, 3f, 10f, 1);
    public SliderValue<Float> minYaw = new SliderValue<>("Minimum Yaw", "How much will be the minimum of randomized Yaw limit?", this, 180F, 0F, 180F, 1);
    public SliderValue<Float> maxYaw = new SliderValue<>("Maximum Yaw", "How much will be the maximum of randomized Yaw limit?", this, 180F, 0F, 180F, 1);
    public SliderValue<Float> minPitch = new SliderValue<>("Minimum Pitch", "How much will be the minimum of randomized Pitch limit?", this, 180F, 0F, 180F, 1);
    public SliderValue<Float> maxPitch = new SliderValue<>("Maximum Pitch", "How much will be the maximum of randomized Pitch limit?", this, 180F, 0F, 180F, 1);
    public CheckBoxValue mouseFix = new CheckBoxValue("Mouse Fix", "Apply GCD Fix to rotations?", this, true);
    public CheckBoxValue heuristics = new CheckBoxValue("Heuristics", "Apply Heuristics bypass to rotations?", this, true);
    public CheckBoxValue prediction = new CheckBoxValue("Prediction", "Predict players position?", this, false);
    public CheckBoxValue necessaryRotations = new CheckBoxValue("Necessary Rotations", "Rotate only if necessary?", this, false);
    public StringBoxValue necessaryMode = new StringBoxValue("Necessary Mode", "What rotations will rotate only if necessary?", this, new String[]{"Pitch", "Yaw", "Both"}, new Supplier[]{() -> necessaryRotations.getValue()});
    public CheckBoxValue nearRotate = new CheckBoxValue("Near Rotate", "Don't rotate if near to the entity?", this, false, new Supplier[]{() -> necessaryRotations.getValue()});
    public SliderValue<Float> nearDistance = new SliderValue<>("Near Distance", "What will be the distance to stop rotating?", this, 0.5f, 0f, 0.5f, 1, new Supplier[]{() -> necessaryRotations.getValue() && nearRotate.getValue()});
    public CheckBoxValue resetRotations = new CheckBoxValue("Reset Rotations", "Reset Rotations properly?", this, true);
    public StringBoxValue resetMode = new StringBoxValue("Reset Mode", "How will the rotations reset?", this, new String[]{"Silent", "Locked"}, new Supplier[]{resetRotations::getValue});
    public CheckBoxValue rayTrace = new CheckBoxValue("Ray Trace", "Ray Trace?",this, true);
    public SliderValue<Float> attackRange = new SliderValue<>("Attack Range", "What'll be the range for Attacking?", this, 3f, 3f, 6f, 1);
    public SliderValue<Float> minCps = new SliderValue<>("Min CPS", "Minimum CPS", this, 10f, 0f, 20f, 1);
    public SliderValue<Float> maxCps = new SliderValue<>("Max CPS", "Maximum CPS", this, 12f, 0f, 20f, 1);

    // Targets
    public static EntityLivingBase curEntity;
    private int currentIndex;
    private TimeHelper switchTimer = new TimeHelper();

    // Rotations
    float curYaw, curPitch;
    boolean hasSilentRotations;

    // Attacking
    private TimeHelper attackTimer = new TimeHelper();
    private double cpsDelay = 0;
    private TimeHelper cpsTimeHelper = new TimeHelper();
    private boolean wasCPSDrop = false;

    @Listen
    public final void onMotion(UpdateMotionEvent updateMotionEvent) {
        targetFinding: {
            List<EntityLivingBase> targets = FightUtil.getMultipleTargets(findRange.getValue(), players.getValue(), animals.getValue(), walls.getValue(), monsters.getValue(), invisible.getValue());

            if (targets.isEmpty() || (curEntity != null && !targets.contains(curEntity))) {
                curEntity = null;
                return;
            }
            if (targetMode.getValue().equalsIgnoreCase("single") || !this.switchTimer.hasReached(switchDelay.getValue())) {
                if (curEntity != null && FightUtil.isValid(curEntity, findRange.getValue(), players.getValue(), animals.getValue(), monsters.getValue(), invisible.getValue())) {
                    break targetFinding;
                } else {
                    curEntity = targets.get(0);
                }
            } else if (targetMode.getValue().equalsIgnoreCase("switch")) {
                if (curEntity != null && FightUtil.isValid(curEntity, findRange.getValue(), players.getValue(), animals.getValue(), monsters.getValue(), invisible.getValue()) && targets.size() == 1) {
                    break targetFinding;
                } else if (curEntity == null) {
                    curEntity = targets.get(0);
                } else if (targets.size() > 1) {
                    int maxIndex = targets.size() - 1;
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
            }
        }
    }

    @Listen
    public final void onRotation(RotationEvent rotationEvent) {
        if(curEntity != null) {

            float[] rots = null;
            if (FightUtil.getRange(curEntity) <= this.rotationRange.getValue().doubleValue()) {
                rots = RotationUtil.getRotation(curEntity, mouseFix.getValue(), heuristics.getValue(), prediction.getValue(), this.minYaw.getValue().floatValue(), this.maxYaw.getValue(), this.minPitch.getValue(), this.maxPitch.getValue());
            }

            if (rots != null) {
                boolean necessary = !necessaryRotations.getValue() || (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) || (FightUtil.getRange(curEntity) >= nearDistance.getValue().floatValue() && nearRotate.getValue());
                boolean yaw = necessaryMode.getValue().equalsIgnoreCase("Yaw");
                boolean pitch = necessaryMode.getValue().equalsIgnoreCase("Pitch");
                boolean both = necessaryMode.getValue().equalsIgnoreCase("Both");
                if (both) {
                    yaw = true;
                    pitch = true;
                }
                if (!necessaryRotations.getValue() || necessary || !yaw)
                    curYaw = rots[0];
                if (!necessaryRotations.getValue() || necessary || !pitch)
                    curPitch = rots[1];
            }

            rotationEvent.setYaw(curYaw);
            rotationEvent.setPitch(curPitch);
            hasSilentRotations = true;
        } else {
            if (hasSilentRotations && resetRotations.getValue()) {
                RotationUtil.resetRotations(getYaw(), getPitch(), resetMode.getValue().equalsIgnoreCase("Silent"));
                hasSilentRotations = false;
            }
            curPitch = getPlayer().rotationPitch;
            curYaw = getPlayer().rotationYaw;
        }
    }

    @Listen
    public final void onClick(ClickingEvent clickingEvent) {
        if(this.curEntity != null) {
            Entity rayTracedEntity = RaytraceUtil.rayCast(this.attackRange.getValue() + 1, PlayerHandler.yaw, PlayerHandler.pitch);
            if(!this.rayTrace.getValue() || rayTracedEntity != null) {
                Entity attackEntity = rayTrace.getValue() ? rayTracedEntity : curEntity;
                if(attackEntity != null && FightUtil.getRange(attackEntity) <= this.attackRange.getValue()) {
                    if(this.attackTimer.hasReached(cpsDelay)) {
                        mc.thePlayer.swingItem();
                        mc.playerController.attackEntity(mc.thePlayer, attackEntity);
                        calculateCps();
                    }
                }
            }
        } else {
            cpsDelay = 0;
        }
    }

    private void calculateCps() {
        double cps = RandomUtil.randomBetween(this.minCps.getValue().floatValue(), this.maxCps.getValue().floatValue());
        final SecureRandom random = new SecureRandom();
        final double maxCPS = cps + 1;
        cpsDelay = cps + (random.nextInt() * (maxCPS - cps));

        if (cpsTimeHelper.hasReached((long) (1000.0 / RandomUtil.randomBetween(cps, this.maxCps.getValue().floatValue())), true)) {
            wasCPSDrop = !wasCPSDrop;
        }

        final double cur = System.currentTimeMillis() * random.nextInt(220);

        double timeCovert = Math.max(cpsDelay, cur) / 3;
        if (wasCPSDrop) {
            cpsDelay = (int) timeCovert;
        } else {
            cpsDelay = (int) (cps + (random.nextInt() * (maxCPS - cps)) / timeCovert);
        }
    }

    @Override
    public void onEnable() {
        curYaw = getPlayer().rotationYaw;
        curPitch = getPlayer().rotationPitch;
    }

    @Override
    public void onDisable() {
        if (hasSilentRotations && resetRotations.getValue())
            RotationUtil.resetRotations(getYaw(), getPitch(), resetMode.getValue().equalsIgnoreCase("Silent"));
        hasSilentRotations = false;
    }

}
package wtf.atani.module.impl.combat;

import com.google.common.base.Supplier;
import javafx.scene.control.Slider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;
import wtf.atani.event.events.ClickingEvent;
import wtf.atani.event.events.Render3DEvent;
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
import wtf.atani.utils.player.PlayerUtil;
import wtf.atani.utils.player.RotationUtil;
import wtf.atani.utils.player.rayTrace.RaytraceUtil;
import wtf.atani.utils.render.RenderUtil;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.SliderValue;
import wtf.atani.value.impl.StringBoxValue;

import java.awt.*;
import java.util.Comparator;
import java.util.List;

@ModuleInfo(name = "KillAura", description = "Attacks people", category = Category.COMBAT, key = Keyboard.KEY_R)
public class KillAura extends Module {

    public SliderValue<Float> findRange = new SliderValue<>("Search Range", "What'll be the range for searching for targets?", this, 4f, 3f, 10f, 1);
    public StringBoxValue targetMode = new StringBoxValue("Target Mode", "How will the aura search for targets?", this, new String[]{"Single", "Hybrid", "Switch", "Multi"});
    public StringBoxValue priority = new StringBoxValue("Priority", "How will the aura sort targets?", this, new String[]{"Health", "Distance"});

    public SliderValue<Long> switchDelay = new SliderValue<>("Switch Delay", "How long will it take to switch between targets?", this, 300L, 0L, 1000L, 0, new Supplier[]{() -> targetMode.getValue().equalsIgnoreCase("Switch")});
    public CheckBoxValue players = new CheckBoxValue("Players", "Attack Players?", this, true);
    public CheckBoxValue animals = new CheckBoxValue("Animals", "Attack Animals", this, true);
    public CheckBoxValue monsters = new CheckBoxValue("Monsters", "Attack Monsters", this, true);
    public CheckBoxValue invisible = new CheckBoxValue("Invisibles", "Attack Invisibles?", this, true);
    public CheckBoxValue walls = new CheckBoxValue("Walls", "Check for walls?", this, true);
    public SliderValue<Float> rotationRange = new SliderValue<>("Rotation Range", "What'll be the range for rotating?", this, 4f, 3f, 10f, 1);
    public CheckBoxValue snapYaw = new CheckBoxValue("Snap Yaw", "Do not smooth out yaw?", this, false);
    public CheckBoxValue snapPitch = new CheckBoxValue("Snap Pitch", "Do not smooth out pitch?", this, false);
    public SliderValue<Float> minYaw = new SliderValue<>("Minimum Yaw", "How much will be the minimum of randomized Yaw limit?", this, 180F, 0F, 180F, 1, new Supplier[]{() -> !snapYaw.getValue()});
    public SliderValue<Float> maxYaw = new SliderValue<>("Maximum Yaw", "How much will be the maximum of randomized Yaw limit?", this, 180F, 0F, 180F, 1, new Supplier[]{() -> !snapYaw.getValue()});
    public SliderValue<Float> minPitch = new SliderValue<>("Minimum Pitch", "How much will be the minimum of randomized Pitch limit?", this, 180F, 0F, 180F, 1, new Supplier[]{() -> !snapPitch.getValue()});
    public SliderValue<Float> maxPitch = new SliderValue<>("Maximum Pitch", "How much will be the maximum of randomized Pitch limit?", this, 180F, 0F, 180F, 1, new Supplier[]{() -> !snapPitch.getValue()});
    public CheckBoxValue mouseFix = new CheckBoxValue("Mouse Fix", "Apply GCD Fix to rotations?", this, true);
    public CheckBoxValue heuristics = new CheckBoxValue("Heuristics", "Apply Heuristics bypass to rotations?", this, true);
    public SliderValue<Float> minYawRandom = new SliderValue<>("Minimum Yaw Random", "What will be the minimum value for randomizing yaw?", this, 0F, -2F, 2F, 2);
    public SliderValue<Float> maxYawRandom = new SliderValue<>("Maximum Yaw Random", "What will be the maximum value for randomizing yaw?", this, 0F, -10F, 10F, 2);
    public SliderValue<Float> minPitchRandom = new SliderValue<>("Minimum Pitch Random", "What will be the minimum value for randomizing pitch?", this, 0F, -2F, 2F, 2);
    public SliderValue<Float> maxPitchRandom = new SliderValue<>("Maximum Pitch Random", "What will be the maximum value for randomizing pitch?", this, 0F, -10F, 10F, 2);
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
    public CheckBoxValue targetESP = new CheckBoxValue("Target ESP", "Show which entity you're attacking?", this, true);
    public CheckBoxValue pointer = new CheckBoxValue("Pointer", "Show where you're looking at?", this, true);

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

    private final class HealthSorter implements Comparator<EntityLivingBase> {
        public int compare(EntityLivingBase o1, EntityLivingBase o2) {
            return Double.compare(FightUtil.getEffectiveHealth(o1), FightUtil.getEffectiveHealth(o2));
        }
    }

    private final class DistanceSorter implements Comparator<EntityLivingBase> {
        public int compare(EntityLivingBase o1, EntityLivingBase o2) {
            return Double.compare(mc.thePlayer.getDistanceToEntity(o1), mc.thePlayer.getDistanceToEntity(o2));
        }
    }

    @Listen
    public final void on3D(Render3DEvent render3DEvent) {
        if(curEntity != null && targetESP.getValue()) {
            double x = this.curEntity.lastTickPosX + (this.curEntity.posX - this.curEntity.lastTickPosX) * render3DEvent.getPartialTicks() - (mc.getRenderManager()).renderPosX;
            double y = this.curEntity.lastTickPosY + (this.curEntity.posY - this.curEntity.lastTickPosY) * render3DEvent.getPartialTicks() - (mc.getRenderManager()).renderPosY;
            double z = this.curEntity.lastTickPosZ + (this.curEntity.posZ - this.curEntity.lastTickPosZ) * render3DEvent.getPartialTicks() - (mc.getRenderManager()).renderPosZ;
            double width = 0.17D;
            double height = 0.25D;
            double thickness = 0.08D;
            AxisAlignedBB entityBox = this.curEntity.getEntityBoundingBox();
            AxisAlignedBB espBox = new AxisAlignedBB(entityBox.minX - this.curEntity.posX + x - width, entityBox.maxY - this.curEntity.posY + y + height, entityBox.minZ - this.curEntity.posZ + z - width, entityBox.maxX - this.curEntity.posX + x + width, entityBox.maxY - this.curEntity.posY + y + height + thickness, entityBox.maxZ - this.curEntity.posZ + z + width);
            RenderUtil.renderESP(curEntity, true, espBox, false, true, curEntity.hurtTime > 0 ? new Color(255, 0, 0, 150) : new Color(255, 255, 255, 150));
        }
        if(curEntity != null && pointer.getValue()) {
            Vec3 aimPoint = RotationUtil.getVectorForRotation(PlayerHandler.yaw, PlayerHandler.pitch);
            Vec3 vec = RotationUtil.getBestVector(mc.thePlayer.getPositionEyes(1F), curEntity.getEntityBoundingBox());
            double dist = PlayerUtil.getDistance(vec.xCoord, vec.yCoord, vec.zCoord);
            aimPoint.xCoord *= dist;
            aimPoint.yCoord *= dist;
            aimPoint.zCoord *= dist;
            aimPoint.yCoord += mc.thePlayer.getEyeHeight();
            AxisAlignedBB aimBB = new AxisAlignedBB(aimPoint.xCoord - 0.1, aimPoint.yCoord - 0.1, aimPoint.zCoord - 0.1, aimPoint.xCoord + 0.1, aimPoint.yCoord + 0.1, aimPoint.zCoord + 0.1);
            RenderUtil.renderESP(curEntity, true, aimBB, false, true, curEntity.hurtTime > 0 ? new Color(255, 0, 0, 150) : new Color(255, 255, 255, 150));
        }
    }

    @Listen
    public final void onMotion(UpdateMotionEvent updateMotionEvent) {
        targetFinding: {
            List<EntityLivingBase> targets = FightUtil.getMultipleTargets(findRange.getValue(), players.getValue(), animals.getValue(), walls.getValue(), monsters.getValue(), invisible.getValue());
            switch (this.priority.getValue()){
                case "Distance":
                    targets.sort(new DistanceSorter());
                    break;
                case "Health":
                    targets.sort(new HealthSorter());
                    break;
            }
            if (targets.isEmpty() || (curEntity != null && !targets.contains(curEntity))) {
                curEntity = null;
                return;
            }
            switch (this.targetMode.getValue()) {
                case "Hybrid":
                    curEntity = targets.get(0);
                    break;
                case "Single":
                    if(curEntity == null || !FightUtil.isValid(curEntity, findRange.getValue(), players.getValue(), animals.getValue(), monsters.getValue(), invisible.getValue()))
                        curEntity = targets.get(0);
                    break;
                case "Multi":
                case "Switch":
                    long switchDelay = this.targetMode.getValue().equalsIgnoreCase("Multi") ? 0 : this.switchDelay.getValue();
                    if(!this.switchTimer.hasReached(switchDelay)) {
                        if(curEntity == null || !FightUtil.isValid(curEntity, findRange.getValue(), players.getValue(), animals.getValue(), monsters.getValue(), invisible.getValue()))
                            curEntity = targets.get(0);
                        return;
                    }
                    if (curEntity != null && FightUtil.isValid(curEntity, findRange.getValue(), players.getValue(), animals.getValue(), monsters.getValue(), invisible.getValue()) && targets.size() == 1) {
                        return;
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
                    break;
            }
        }
    }

    @Listen
    public final void onRotation(RotationEvent rotationEvent) {
        if(curEntity != null) {

            float[] rots = null;
            if (FightUtil.getRange(curEntity) <= this.rotationRange.getValue().doubleValue()) {
                rots = RotationUtil.getRotation(curEntity, mouseFix.getValue(), heuristics.getValue(), minYawRandom.getValue(), maxYawRandom.getValue(), minPitchRandom.getValue(), maxPitchRandom.getValue(), prediction.getValue(), this.minYaw.getValue().floatValue(), this.maxYaw.getValue(), this.minPitch.getValue(), this.maxPitch.getValue(), this.snapYaw.getValue(), this.snapPitch.getValue());
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
        if(this.curEntity != null && FightUtil.isValid(curEntity, attackRange.getMaximum(), invisible.getValue(), players.getValue(), animals.getValue(), monsters.getValue())) {
            MovingObjectPosition movingObjectPosition = RaytraceUtil.rayCast(1.0F, new float[] {PlayerHandler.yaw, PlayerHandler.pitch}, this.attackRange.getValue().floatValue(), 0.10000000149011612);
            if(!this.rayTrace.getValue() || (movingObjectPosition != null && movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY)) {
                Entity attackEntity = rayTrace.getValue() ? movingObjectPosition.entityHit : curEntity;
                if(attackEntity != null && attackEntity instanceof EntityLivingBase && FightUtil.isValid((EntityLivingBase) attackEntity, attackRange.getMaximum(), invisible.getValue(), players.getValue(), animals.getValue(), monsters.getValue()) && FightUtil.getRange(attackEntity) <= this.attackRange.getValue()) {
                    if(this.attackTimer.hasReached(cpsDelay)) {
                        mc.thePlayer.swingItem();
                        mc.playerController.attackEntity(mc.thePlayer, attackEntity);
                        calculateCps();
                        this.attackTimer.reset();
                    }
                }
            }
        } else {
            cpsDelay = 0;
        }
    }

    private void calculateCps() {
        final int maxValue = (int) ((this.minCps.getMaximum() - this.maxCps.getValue()) * 20);
        final int minValue = (int) ((this.minCps.getMaximum() - this.minCps.getValue()) * 20);

        cpsDelay = (int) (RandomUtil.randomBetween(minValue, maxValue) - RandomUtil.secureRandom.nextInt(10) + RandomUtil.secureRandom.nextInt(10));
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

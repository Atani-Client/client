package wtf.atani.module.impl.combat;

import com.google.common.base.Supplier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;
import wtf.atani.event.events.*;
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
import wtf.atani.utils.render.RenderUtil;
import wtf.atani.utils.render.color.ColorUtil;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.SliderValue;
import wtf.atani.value.impl.StringBoxValue;

import java.awt.*;
import java.util.Calendar;
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
    public CheckBoxValue nearRotate = new CheckBoxValue("Stop if Near", "Don't rotate if near to the entity?", this, false, new Supplier[]{() -> necessaryRotations.getValue()});
    public SliderValue<Float> nearDistance = new SliderValue<>("Near Distance", "What will be the distance to stop rotating?", this, 0.5f, 0f, 0.5f, 1, new Supplier[]{() -> necessaryRotations.getValue() && nearRotate.getValue()});
    public CheckBoxValue resetRotations = new CheckBoxValue("Reset Rotations", "Reset Rotations properly?", this, true);
    public StringBoxValue resetMode = new StringBoxValue("Reset Mode", "How will the rotations reset?", this, new String[]{"Silent", "Locked"}, new Supplier[]{resetRotations::getValue});
    public CheckBoxValue rayTrace = new CheckBoxValue("Ray Trace", "Ray Trace?",this, true);
    public SliderValue<Float> attackRange = new SliderValue<>("Attack Range", "What'll be the range for Attacking?", this, 3f, 3f, 6f, 1);
    public CheckBoxValue fixServersSideMisplace = new CheckBoxValue("Fix Server-Side Misplace", "Fix Server-Side Misplace?", this, true);
    public SliderValue<Float> minCps = new SliderValue<>("Min CPS", "Minimum CPS", this, 10f, 0f, 20f, 1);
    public SliderValue<Float> maxCps = new SliderValue<>("Max CPS", "Maximum CPS", this, 12f, 0f, 20f, 1);
    public CheckBoxValue targetESP = new CheckBoxValue("Target ESP", "Show which entity you're attacking?", this, true);
    public CheckBoxValue box = new CheckBoxValue("Box", "Display little box above the target?", this, false, new Supplier[]{() -> targetESP.getValue()});
    public StringBoxValue boxMode = new StringBoxValue("Box Mode", "What box wil be rendered?", this, new String[]{"Above", "Full"});
    public CheckBoxValue circle = new CheckBoxValue("Ring", "Display little Ring around the target?", this, false, new Supplier[]{() -> targetESP.getValue()});
    public CheckBoxValue pointer = new CheckBoxValue("Pointer", "Show where you're looking at?", this, true, new Supplier[]{() -> targetESP.getValue()});
    private StringBoxValue customColorMode = new StringBoxValue("Color Mode", "How will the esp be colored?", this, new String[]{"Static", "Fade", "Gradient", "Rainbow", "Astolfo Sky"}, new Supplier[]{() -> targetESP.getValue()});
    private CheckBoxValue changeOnHurt = new CheckBoxValue("Change Color on hurt", "Change the ESP colour to red if the target is being hurt?", this, false);
    private SliderValue<Integer> red = new SliderValue<>("Red", "What'll be the red of the color?", this, 255, 0, 255, 0, new Supplier[]{() -> targetESP.getValue() && customColorMode.getValue().equalsIgnoreCase("Static") || customColorMode.getValue().equalsIgnoreCase("Random") || customColorMode.getValue().equalsIgnoreCase("Fade") || customColorMode.getValue().equalsIgnoreCase("Gradient")});
    private SliderValue<Integer> green = new SliderValue<>("Green", "What'll be the green of the color?", this, 255, 0, 255, 0, new Supplier[]{() -> targetESP.getValue() && customColorMode.getValue().equalsIgnoreCase("Static") || customColorMode.getValue().equalsIgnoreCase("Random") || customColorMode.getValue().equalsIgnoreCase("Fade") || customColorMode.getValue().equalsIgnoreCase("Gradient")});
    private SliderValue<Integer> blue = new SliderValue<>("Blue", "What'll be the blue of the color?", this, 255, 0, 255, 0, new Supplier[]{() -> targetESP.getValue() && customColorMode.getValue().equalsIgnoreCase("Static") || customColorMode.getValue().equalsIgnoreCase("Random") || customColorMode.getValue().equalsIgnoreCase("Fade") || customColorMode.getValue().equalsIgnoreCase("Gradient")});
    private SliderValue<Integer> red2 = new SliderValue<>("Second Red", "What'll be the red of the second color?", this, 255, 0, 255, 0, new Supplier[]{() -> targetESP.getValue() && customColorMode.getValue().equalsIgnoreCase("Gradient")});
    private SliderValue<Integer> green2 = new SliderValue<>("Second Green", "What'll be the green of the second color?", this, 255, 0, 255, 0, new Supplier[]{() -> targetESP.getValue() && customColorMode.getValue().equalsIgnoreCase("Gradient")});
    private SliderValue<Integer> blue2 = new SliderValue<>("Second Blue", "What'll be the blue of the second color?", this, 255, 0, 255, 0, new Supplier[]{() -> targetESP.getValue() && customColorMode.getValue().equalsIgnoreCase("Gradient")});
    private SliderValue<Float> darkFactor = new SliderValue<>("Dark Factor", "How much will the color be darkened?", this, 0.49F, 0F, 1F, 2, new Supplier[]{() -> targetESP.getValue() && customColorMode.getValue().equalsIgnoreCase("Fade")});

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
    
    // Range
    private double correctedRange = 0D;

    @Override
    public String getSuffix() {
    	return targetMode.getValue();
    }
    
    private final class AttackRangeSorter implements Comparator<EntityLivingBase> {
        public int compare(EntityLivingBase o1, EntityLivingBase o2) {
            int first = FightUtil.getRange(o1) <= attackRange.getValue() ? 0 : 1;
            int second = FightUtil.getRange(o2) <= attackRange.getValue() ? 0 : 1;
            return Double.compare(first, second);
        }
    }

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
    public void onRayTrace(RayTraceRangeEvent rayTraceRangeEvent) {
        if(curEntity != null) {
            correctedRange = this.attackRange.getValue() + 0.00256f;
            if (this.fixServersSideMisplace.getValue()) {
                final float n = 0.010625f;
                if (mc.thePlayer.getHorizontalFacing() == EnumFacing.NORTH || mc.thePlayer.getHorizontalFacing() == EnumFacing.WEST) {
                    correctedRange += n * 2.0f;
                }
            }
            rayTraceRangeEvent.setRange((float) correctedRange);
            rayTraceRangeEvent.setBlockReachDistance((float) Math.max(mc.playerController.getBlockReachDistance(), correctedRange));
        }
    }

    final Calendar calendar = Calendar.getInstance();

    @Listen
    public final void on3D(Render3DEvent render3DEvent) {
        if(curEntity != null) {
            int color = 0;
            final int counter = 1;
            switch (this.customColorMode.getValue()) {
                case "Static":
                    color = new Color(red.getValue(), green.getValue(), blue.getValue()).getRGB();
                    break;
                case "Fade": {
                    int firstColor = new Color(red.getValue(), green.getValue(), blue.getValue()).getRGB();
                    color = ColorUtil.fadeBetween(firstColor, ColorUtil.darken(firstColor, darkFactor.getValue()), counter * 150L);
                    break;
                }
                case "Gradient": {
                    int firstColor = new Color(red.getValue(), green.getValue(), blue.getValue()).getRGB();
                    int secondColor = new Color(red2.getValue(), green2.getValue(), blue2.getValue()).getRGB();
                    color = ColorUtil.fadeBetween(firstColor, secondColor, counter * 150L);
                    break;
                }
                case "Rainbow":
                    color = ColorUtil.getRainbow(3000, (int) (counter * 150L));
                    break;
                case "Astolfo Sky":
                    color = ColorUtil.blendRainbowColours(counter * 150L);
                    break;
            }
            if(calendar.get(Calendar.DAY_OF_MONTH) == 28 && calendar.get(Calendar.MONTH) == Calendar.OCTOBER) {
                color = ColorUtil.blendCzechiaColours(counter * 150L);
            }
            if(calendar.get(Calendar.DAY_OF_MONTH) == 3 && calendar.get(Calendar.MONTH) == Calendar.OCTOBER) {
                color = ColorUtil.blendGermanColours(counter * 150L);
            }
            if(this.changeOnHurt.getValue() && curEntity.hurtTime > 0)
                color = Color.red.getRGB();
            if(targetESP.getValue() && box.getValue()) {
                double x = this.curEntity.lastTickPosX + (this.curEntity.posX - this.curEntity.lastTickPosX) * render3DEvent.getPartialTicks() - (mc.getRenderManager()).renderPosX;
                double y = this.curEntity.lastTickPosY + (this.curEntity.posY - this.curEntity.lastTickPosY) * render3DEvent.getPartialTicks() - (mc.getRenderManager()).renderPosY;
                double z = this.curEntity.lastTickPosZ + (this.curEntity.posZ - this.curEntity.lastTickPosZ) * render3DEvent.getPartialTicks() - (mc.getRenderManager()).renderPosZ;
                double width = 0.17D;
                double height = 0.25D;
                double thickness = 0.08D;
                if(this.boxMode.getValue().equalsIgnoreCase("Full")) {
                    thickness -= curEntity.height + 0.25D * 2 + thickness;
                }
                AxisAlignedBB entityBox = this.curEntity.getEntityBoundingBox();
                AxisAlignedBB espBox = new AxisAlignedBB(entityBox.minX - this.curEntity.posX + x - width, entityBox.maxY - this.curEntity.posY + y + height, entityBox.minZ - this.curEntity.posZ + z - width, entityBox.maxX - this.curEntity.posX + x + width, entityBox.maxY - this.curEntity.posY + y + height + thickness, entityBox.maxZ - this.curEntity.posZ + z + width);
                RenderUtil.renderESP(espBox, false, true, ColorUtil.setAlpha(new Color(color), 150));
            }
            if(targetESP.getValue() && pointer.getValue()) {
                Vec3 aimPoint = RotationUtil.getVectorForRotation(PlayerHandler.yaw, PlayerHandler.pitch);
                Vec3 vec = RotationUtil.getBestVector(mc.thePlayer.getPositionEyes(1F), curEntity.getEntityBoundingBox());
                double dist = PlayerUtil.getDistance(vec.xCoord, vec.yCoord, vec.zCoord);
                aimPoint.xCoord *= dist;
                aimPoint.yCoord *= dist;
                aimPoint.zCoord *= dist;
                aimPoint.yCoord += mc.thePlayer.getEyeHeight();
                AxisAlignedBB aimBB = new AxisAlignedBB(aimPoint.xCoord - 0.1, aimPoint.yCoord - 0.1, aimPoint.zCoord - 0.1, aimPoint.xCoord + 0.1, aimPoint.yCoord + 0.1, aimPoint.zCoord + 0.1);
                RenderUtil.renderESP(aimBB, false, true, ColorUtil.setAlpha(new Color(color), 150));
            }
            if(targetESP.getValue() && circle.getValue()) {
                RenderUtil.renderRing(curEntity, new Color(color));
            }
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
            targets.sort(new AttackRangeSorter());
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
            MovingObjectPosition movingObjectPosition = mc.objectMouseOver;
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

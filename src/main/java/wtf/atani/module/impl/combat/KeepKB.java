package wtf.atani.module.impl.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.MovingObjectPosition;
import wtf.atani.event.events.ClickingEvent;
import wtf.atani.event.events.UpdateMotionEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.module.storage.ModuleStorage;
import wtf.atani.utils.combat.FightUtil;
import wtf.atani.utils.math.random.RandomUtil;
import wtf.atani.utils.math.time.TimeHelper;
import wtf.atani.utils.player.MoveUtil;
import wtf.atani.utils.player.PlayerHandler;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.SliderValue;
import wtf.atani.value.impl.StringBoxValue;

@ModuleInfo(name = "KeepKB", description = "Helps you get people in combos", category = Category.COMBAT)
public class KeepKB extends Module {

    public StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[]{"Sprint Reset", "Legit", "Legit Fast", "Single Packet", "Normal Packet", "Double Packet"});
    public CheckBoxValue auraOnly = new CheckBoxValue("KillAura Only", "Operate only if the target is attacked by KillAura?", this, true);
    public SliderValue<Long> minDelay = new SliderValue<>("Minimum Delay", "What'll be the minimum delay between unsprinting?", this, 50L, 0L, 100L, 0);
    public SliderValue<Long> maxDelay = new SliderValue<>("Maximum Delay", "What'll be the maximum delay between unsprinting?", this, 60L, 0L, 100L, 0);

    private EntityLivingBase target;

    private boolean isHit = false;
    private TimeHelper attackTimer = new TimeHelper();

    private long delay = 0L;

    @Override
    public String getSuffix() {
    	return mode.getValue();
    }
    
    @Listen
    public final void onMotion(UpdateMotionEvent updateMotionEvent) {
        switch (mode.getValue()) {
            case "Legit":
                getGameSettings().keyBindSprint.pressed = true;

                if (isHit && attackTimer.hasReached(delay / 2)) {
                    isHit = false;
                    mc.thePlayer.setSprinting(false);
                }
                break;
            case "Legit Fast":
                if (isHit) {
                    mc.thePlayer.sprintingTicksLeft = 0;
                }
                break;
        }
    }

    @Listen
    public final void onAttack(ClickingEvent clickingEvent) {
        KillAura killAura = ModuleStorage.getInstance().getByClass(KillAura.class);
        boolean setTarget = false;
        if(!auraOnly.getValue() && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && mc.objectMouseOver.entityHit != null && mc.objectMouseOver.entityHit instanceof EntityLivingBase) {
            target = (EntityLivingBase) mc.objectMouseOver.entityHit;
            setTarget = true;
        }
        if(killAura.isEnabled() && KillAura.curEntity != null && FightUtil.getRange(KillAura.curEntity) <= ModuleStorage.getInstance().getByClass(KillAura.class).attackRange.getValue().floatValue()) {
            target = killAura.curEntity;
            setTarget = true;
        }
        if(!setTarget)
            target = null;
        if(target != null) {
            switch (mode.getValue()) {
                case "Legit":
                case "Legit Fast":
                    if (!isHit) {
                        isHit = true;
                        attackTimer.reset();
                        delay = (long) RandomUtil.randomBetween(minDelay.getValue().longValue(), maxDelay.getValue().longValue());
                    }
                    break;
                case "Sprint Reset":
                    PlayerHandler.shouldSprintReset = true;
                    break;
                case "Single Packet":
                    if (mc.thePlayer.isSprinting()) {
                        mc.thePlayer.setSprinting(false);
                    }
                    mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                    mc.thePlayer.serverSprintState = true;
                    break;
                case "Normal Packet":
                    mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                    mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                    mc.thePlayer.serverSprintState = true;
                    break;
                case "Double Packet":
                    mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                    mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                    mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                    mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                    mc.thePlayer.serverSprintState = true;
                    break;
            }
        }
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}

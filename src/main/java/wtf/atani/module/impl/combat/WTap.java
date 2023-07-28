package wtf.atani.module.impl.combat;

import net.minecraft.network.play.client.C0BPacketEntityAction;
import wtf.atani.event.events.ClickingEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.module.storage.ModuleStorage;
import wtf.atani.utils.combat.FightUtil;
import wtf.atani.utils.player.PlayerHandler;
import wtf.atani.value.impl.StringBoxValue;

@ModuleInfo(name = "W-Tap", description = "Helps you give people more knockback", category = Category.COMBAT)
public class WTap extends Module {

    public StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[]{"Sprint Reset", "Single Packet", "Normal Packet", "Double Packet"});

    @Listen
    public final void onAttack(ClickingEvent clickingEvent) {
        if(ModuleStorage.getInstance().getByClass(KillAura.class).isEnabled() && KillAura.curEntity != null && FightUtil.getRange(KillAura.curEntity) <= ModuleStorage.getInstance().getByClass(KillAura.class).attackRange.getValue().floatValue()) {
            switch (mode.getValue()) {
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

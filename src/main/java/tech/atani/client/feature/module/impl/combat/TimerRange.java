package tech.atani.client.feature.module.impl.combat;

import cn.muyang.nativeobfuscator.Native;
import com.google.common.base.Supplier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.value.impl.SliderValue;
import tech.atani.client.feature.value.impl.StringBoxValue;
import tech.atani.client.listener.event.minecraft.network.PacketEvent;
import tech.atani.client.listener.event.minecraft.player.movement.SilentMoveEvent;
import tech.atani.client.listener.event.minecraft.player.movement.UpdateEvent;
import tech.atani.client.listener.event.minecraft.player.movement.UpdateMotionEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.utility.interfaces.Methods;
import tech.atani.client.utility.math.time.TimeHelper;
import tech.atani.client.utility.player.PlayerUtil;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Native
@ModuleData(name = "TimerRange", description = "Uses timer to get better hits", category = Category.COMBAT)
public class TimerRange extends Module {

    private final SliderValue<Integer> boostTicks = new SliderValue<Integer>("Boost Ticks", "For how many ticks will TimerRange boost?", this, 5, 1, 20, 0);
    private final SliderValue<Integer> timer = new SliderValue<Integer>("Timer", "With what timer will TimerBoost boost?", this, 2, 1, 5, 0);
    boolean boost;
    private Entity lastEntity;
    TimeHelper timeHelper = new TimeHelper();
    @Listen
    public void onUpdateMotionEvent(UpdateMotionEvent event) {
        if(Methods.mc.thePlayer == null || Methods.mc.theWorld == null) {
            return;
        }

        if(KillAura.curEntity != null && KillAura.curEntity != lastEntity) {
            boost = true;
        }

        if(boost) {
            if(timeHelper.hasReached(Math.round(boostTicks.getValue() * 20))) {
                PlayerUtil.addChatMessgae("Stop boost", true);
                boost = false;
                lastEntity = KillAura.curEntity;
            }
            mc.timer.timerSpeed = timer.getValue();
        } else {
            mc.timer.timerSpeed = 1;
        }
    }

    @Listen
    public final void onUpdate(UpdateEvent updateEvent) {

    }

    @Listen
    public final void onPacket(PacketEvent packetEvent) {
        if(Methods.mc.thePlayer == null || Methods.mc.theWorld == null) {
            return;
        }


    }

    @Listen
    public final void onSilent(SilentMoveEvent silentMoveEvent) {

    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        if(Methods.mc.thePlayer == null || Methods.mc.theWorld == null) {
            return;
        }
    }
}

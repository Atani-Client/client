package tech.atani.client.feature.module.impl.combat;

import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.EnumFacing;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.value.impl.CheckBoxValue;
import tech.atani.client.feature.value.impl.SliderValue;
import tech.atani.client.feature.value.impl.StringBoxValue;
import tech.atani.client.listener.event.minecraft.network.PacketEvent;
import tech.atani.client.listener.event.minecraft.player.movement.UpdateMotionEvent;
import tech.atani.client.listener.event.minecraft.player.rotation.RayTraceRangeEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.utility.player.PlayerUtil;

import java.util.ArrayList;
import java.util.Iterator;

@ModuleData(name = "BlinkRange", description = "Allows you to hit from further distance", category = Category.COMBAT)
public class BlinkRange extends Module {
    private final StringBoxValue targetMode = new StringBoxValue("Target Mode", "Which target mode will the module use?", this, new String[]{"KillAura", "PointedEntity"});
    public SliderValue<Float> maxRange = new SliderValue<>("Max Range", "What'll be the range for Attacking?", this, 3.2f, 3f, 6f, 1);
    public SliderValue<Float> minRange = new SliderValue<>("Min Range", "What'll be the range for Attacking?", this, 3f, 3f, 6f, 1);
    private final ArrayList<Packet<INetHandler>> packets = new ArrayList<>();
    private boolean blink;
    private final Entity target = targetMode.is("KillAura") ? KillAura.curEntity : mc.pointedEntity;

    @Listen
    public final void onPacket(PacketEvent packetEvent) {
        if(blink && packetEvent.getPacket() instanceof C03PacketPlayer) {
            PlayerUtil.addChatMessgae("add packet. size: " + packets.size(), true);
            packets.add(packetEvent.getPacket());
            packetEvent.setCancelled(true);
        }
    }

    @Listen
    public void onUpdateMotion(UpdateMotionEvent motionEvent) {
        if(target != null && mc.thePlayer.getDistanceToEntity(target) > minRange.getValue() && mc.thePlayer.getDistanceToEntity(target) < maxRange.getValue()) {
            blink = true;
            PlayerUtil.addChatMessgae("BLINK", true);
        } else if(blink) {
            blink = false;
            send();
        }
    }

    private void send() {
        Iterator<Packet<INetHandler>> iterator = packets.iterator();
        while (iterator.hasNext()) {
            Packet<INetHandler> packet = iterator.next();
            mc.thePlayer.sendQueue.addToSendQueue(packet);
            iterator.remove();
            PlayerUtil.addChatMessgae("remove packet. size: " + packets.size(), true);
        }
    }


    @Override
    public void onEnable() {
        blink = false;
    }

    @Override
    public void onDisable() {}
}

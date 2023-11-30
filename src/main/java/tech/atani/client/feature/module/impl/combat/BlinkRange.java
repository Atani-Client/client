package tech.atani.client.feature.module.impl.combat;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.EnumFacing;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.value.impl.CheckBoxValue;
import tech.atani.client.feature.value.impl.SliderValue;
import tech.atani.client.listener.event.minecraft.network.PacketEvent;
import tech.atani.client.listener.event.minecraft.player.movement.UpdateMotionEvent;
import tech.atani.client.listener.event.minecraft.player.rotation.RayTraceRangeEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.utility.player.PlayerUtil;

import java.util.ArrayList;

@ModuleData(name = "Reach", description = "Allows you to hit further", category = Category.COMBAT)
public class BlinkRange extends Module {

    public SliderValue<Float> attackRange = new SliderValue<>("Attack Range", "What'll be the range for Attacking?", this, 3f, 3f, 6f, 1);
    public CheckBoxValue fixServersSideMisplace = new CheckBoxValue("Fix Server-Side Misplace", "Fix Server-Side Misplace?", this, true);
    private final ArrayList<Packet<INetHandler>> packets = new ArrayList<>();
    private boolean blink;
    private int packetsLeft = packets.size();

    @Listen
    public final void onPacket(PacketEvent packetEvent) {
        if(blink && packetEvent.getPacket() instanceof C03PacketPlayer) {
            PlayerUtil.addChatMessgae("add packet. size: " + packetsLeft, true);
            packets.add(packetEvent.getPacket());
            packetEvent.setCancelled(true);
        }
    }

    @Listen
    public void onUpdateMotion(UpdateMotionEvent motionEvent) {
        
    }

    private void send() {
        for(int i = 0; i<packets.size(); i++) {
            mc.thePlayer.sendQueue.addToSendQueue(packets.get(i));
            packets.remove(i);
            PlayerUtil.addChatMessgae("remove packet. size: " + packetsLeft, true);
        }
    }

    @Override
    public void onEnable() {
        blink = false;
    }

    @Override
    public void onDisable() {}
}

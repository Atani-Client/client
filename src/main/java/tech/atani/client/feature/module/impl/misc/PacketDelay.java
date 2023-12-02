package tech.atani.client.feature.module.impl.misc;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.status.client.C01PacketPing;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.value.impl.SliderValue;
import tech.atani.client.listener.event.minecraft.network.PacketEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.utility.interfaces.Methods;

@ModuleData(name = "PacketDelay", description = "Delays Some Packets", category = Category.MISCELLANEOUS)
public class PacketDelay extends Module {
    public SliderValue<Float> delay = new SliderValue<>("Delay", "Whats the packet delay?", this, 150f, 0f, 2000f, 0);
    private float delay2 = delay.getValue();
    @Listen
    public void onPacket(PacketEvent packetEvent) {
        if(Methods.mc.thePlayer == null || Methods.mc.theWorld == null)
            return;
        if(packetEvent.getType() == PacketEvent.Type.OUTGOING) {
            final Packet<?> packet = packetEvent.getPacket();
            if (packet instanceof C00PacketKeepAlive || packet instanceof C01PacketPing || packet instanceof C0FPacketConfirmTransaction) {
                packetEvent.setCancelled(true);
                new Thread(() -> {
                    try {
                        Thread.sleep((long) delay2);
                    }
                    catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    Methods.mc.getNetHandler().getNetworkManager().channel.writeAndFlush(packet);
                }).start();
            }
        }
    }


    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}

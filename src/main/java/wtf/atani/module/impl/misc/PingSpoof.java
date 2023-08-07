package wtf.atani.module.impl.misc;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.status.client.C01PacketPing;
import wtf.atani.event.events.PacketEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.utils.math.random.RandomUtil;
import wtf.atani.value.impl.SliderValue;

@ModuleInfo(name = "PingSpoof", description = "Spoofs your ping", category = Category.MISCELLANEOUS)
public class PingSpoof extends Module {

    private SliderValue<Long> minDelay = new SliderValue<>("Minimum Delay", "What'll be the minimum delay for freezing packets?", this, 1500L, 0L, 5000L, 0);
    private SliderValue<Long> maxDelay = new SliderValue<>("Maximum Delay", "What'll be the maximum delay for freezing packets?", this, 2000L, 0L, 5000L, 0);

    @Listen
    public void onPacket(PacketEvent packetEvent) {
    	if(mc.thePlayer == null || mc.theWorld == null)
    		return;
        if(packetEvent.getType() == PacketEvent.Type.OUTGOING) {
            final Packet packet = packetEvent.getPacket();
            if (packet instanceof C00PacketKeepAlive || packet instanceof C01PacketPing || packet instanceof C0FPacketConfirmTransaction) {
                packetEvent.setCancelled(true);
                new Thread(() -> {
                    try {
                        Thread.sleep((long) RandomUtil.randomBetween(minDelay.getValue(), maxDelay.getValue()));
                    }
                    catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    mc.getNetHandler().getNetworkManager().channel.writeAndFlush(packet);
                }).start();
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

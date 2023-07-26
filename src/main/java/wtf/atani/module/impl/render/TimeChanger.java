package wtf.atani.module.impl.render;

import net.minecraft.network.play.server.S03PacketTimeUpdate;
import wtf.atani.event.events.PacketEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.SliderValue;

@ModuleInfo(name = "TimeChanger", description = "Changes world time client side", category = Category.RENDER)
public class TimeChanger extends Module {
    private final SliderValue time = new SliderValue("Time", "What should the time be?", this, 160, 0, 250, 0);

    @Listen
    public void onPacket(PacketEvent packetEvent) {
        if(packetEvent.getPacket() instanceof S03PacketTimeUpdate) {
            mc.theWorld.setWorldTime((int)time.getValue() * 100L);
            packetEvent.setCancelled(true);
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}

package wtf.atani.module.impl.hud;

import net.minecraft.network.play.server.S37PacketStatistics;
import wtf.atani.event.events.PacketEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;

@ModuleInfo(name = "NoAchievements", description = "Removes the annoying achievements.", category = Category.HUD)
public class NoAchievements extends Module {

    @Listen
    public void onPacketEvent(PacketEvent event) {
        if (event.getType() == PacketEvent.Type.INCOMING) {
            if(event.getPacket() instanceof S37PacketStatistics) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

}

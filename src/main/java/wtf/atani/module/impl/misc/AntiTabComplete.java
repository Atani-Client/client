package wtf.atani.module.impl.misc;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.server.S3APacketTabComplete;
import wtf.atani.event.events.PacketEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;

@ModuleInfo(name = "AntiTabComplete", description = "Prevent people from 'tabing' your name", category = Category.MISCELLANEOUS)
public class AntiTabComplete extends Module {

    @Listen
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof C14PacketTabComplete || event.getPacket() instanceof S3APacketTabComplete) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
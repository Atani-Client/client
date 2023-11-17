package tech.atani.client.feature.module.impl.misc;

import net.minecraft.network.play.client.C03PacketPlayer;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.listener.event.minecraft.network.PacketEvent;
import tech.atani.client.listener.radbus.Listen;

@ModuleData(name = "NoC03", description = "Deletes C03 Packet", category = Category.MISCELLANEOUS, alwaysRegistered = true)
public class NoC03 extends Module {
    private boolean active;
    @Listen
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof C03PacketPlayer && active)
            event.setCancelled(true);
    }

    @Override
    public void onEnable() {
        active = true;
    }

    @Override
    public void onDisable() {
        active = false;
    }

}
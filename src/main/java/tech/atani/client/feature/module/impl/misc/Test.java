package tech.atani.client.feature.module.impl.misc;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.listener.event.minecraft.network.PacketEvent;
import tech.atani.client.listener.event.minecraft.player.rotation.RotationEvent;
import tech.atani.client.listener.radbus.Listen;

@ModuleData(name = "Test", description = "test", category = Category.MISCELLANEOUS)
public class Test extends Module {

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Listen
    public final void onRotationEvent(RotationEvent event) {
        event.setPitch(0);
        event.setYaw(0);
    }

    @Listen
    public final void onPacketEvent(PacketEvent event) {
        Packet <?> packet = event.getPacket();

        if (packet instanceof C03PacketPlayer.C05PacketPlayerLook) {
        //    event.setCancelled(true);
        }
    }
}

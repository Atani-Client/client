package tech.atani.client.feature.module.impl.player;

import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import tech.atani.client.listener.event.events.minecraft.network.PacketEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;

@ModuleData(name = "NoRotate", description = "Stops flags from setting your yaw and pitch", category = Category.PLAYER)
public class NoRotate extends Module {

    @Listen
    public void onPacket(PacketEvent packetEvent) {
        if(packetEvent.getPacket() instanceof S08PacketPlayerPosLook) {
            if(mc.thePlayer.rotationYaw != -180 && mc.thePlayer.rotationPitch != 0) {
                S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) packetEvent.getPacket();

                packet.setYaw(mc.thePlayer.rotationYaw);
                packet.setPitch(mc.thePlayer.rotationPitch);
            }
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
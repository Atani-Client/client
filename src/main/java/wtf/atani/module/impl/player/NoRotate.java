package wtf.atani.module.impl.player;

import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import wtf.atani.event.events.PacketEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;

@ModuleInfo(name = "NoRotate", description = "Stops flags from setting your yaw and pitch", category = Category.PLAYER)
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

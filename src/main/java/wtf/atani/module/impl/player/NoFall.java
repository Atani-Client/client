package wtf.atani.module.impl.player;

import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.atani.event.events.PacketEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.StringBoxValue;

@ModuleInfo(name = "No Fall", description = "Reduces fall damage", category = Category.PLAYER)
public class NoFall extends Module {
    private final StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[] {"Edit", "Vulcan"});

    @Listen
    public void onPacket(PacketEvent packetEvent) {
        switch(mode.getValue()) {
            case "Edit":
                if(packetEvent.getPacket() instanceof C03PacketPlayer && mc.thePlayer.fallDistance > 3) {
                    ((C03PacketPlayer) packetEvent.getPacket()).setOnGround(true);
                    mc.thePlayer.fallDistance = 0;
                }
                break;
            case "Vulcan":
                if(packetEvent.getPacket() instanceof C03PacketPlayer && mc.thePlayer.fallDistance > 3) {
                    C03PacketPlayer packet = (C03PacketPlayer) packetEvent.getPacket();

                    packet.setY(packet.getPositionY() + 0.07);
                    packet.setOnGround(true);
                    mc.thePlayer.motionY = -0.07;
                    mc.thePlayer.fallDistance = 0;
                }
                break;
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

}

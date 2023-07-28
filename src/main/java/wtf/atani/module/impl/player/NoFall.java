package wtf.atani.module.impl.player;

import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.atani.event.events.PacketEvent;
import wtf.atani.event.events.TickEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.utils.math.time.TickHelper;
import wtf.atani.value.impl.StringBoxValue;

@ModuleInfo(name = "NoFall", description = "Reduces fall damage", category = Category.PLAYER)
public class NoFall extends Module {
    private final StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[] {"Edit", "Vulcan", "Verus", "Spartan"});

    private final TickHelper spartanTimer = new TickHelper();

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
            case "Verus":
                if(mc.thePlayer.fallDistance - mc.thePlayer.motionY > 3) {
                    mc.thePlayer.motionY = 0.0;
                    mc.thePlayer.fallDistance = 0.0f;
                    mc.thePlayer.motionX *= 0.6;
                    mc.thePlayer.motionZ *= 0.6;
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                }
                break;

        }
    }

    @Listen
    public void onTickEvent(TickEvent tickEvent) {
        if(mode.getValue().equals("Spartan")) {
            spartanTimer.update();

            if(mc.thePlayer.fallDistance > 1.5 && spartanTimer.hasReached(10)) {
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 10, mc.thePlayer.posZ, true));
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 10, mc.thePlayer.posZ, true));
                spartanTimer.reset();
            }
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

}

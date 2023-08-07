package wtf.atani.module.impl.player;

import org.apache.commons.codec.digest.Md5Crypt;

import com.google.common.base.Supplier;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import wtf.atani.event.events.PacketEvent;
import wtf.atani.event.events.TickEvent;
import wtf.atani.event.events.UpdateEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.utils.math.time.TickHelper;
import wtf.atani.utils.math.time.TimeHelper;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.SliderValue;
import wtf.atani.value.impl.StringBoxValue;
import com.google.common.base.Supplier;

@ModuleInfo(name = "NoFall", description = "Reduces fall damage", category = Category.PLAYER)
public class NoFall extends Module {
    private final StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[] {"Edit", "Vulcan", "Verus", "Spartan"});
    private final CheckBoxValue modulo = new CheckBoxValue("Modulo", "Set on ground only every 3 blocks?", this, true, new Supplier[] {() -> mode.getValue().equalsIgnoreCase("Edit")});
    private final StringBoxValue vulcanMode = new StringBoxValue("Vulcan Mode", "Which mode will the vulcan mode use?", this, new String[] {"Instant Motion"});

    private final TickHelper spartanTimer = new TickHelper();

    @Listen
    public void onPacket(PacketEvent packetEvent) {
        if(mc.thePlayer != null && mc.theWorld != null) {
            float modulo =  mc.thePlayer.fallDistance % 3;
            boolean correctModulo = modulo < 1f && mc.thePlayer.fallDistance > 3;
            boolean editGround = this.modulo.getValue() ? correctModulo : mc.thePlayer.fallDistance > 3;
            switch(mode.getValue()) {
                case "Edit":
                    if(mc.thePlayer.fallDistance > 3) {
                        sendMessage(modulo + " " + mc.thePlayer.fallDistance + " " + correctModulo);
                    }
                    if(packetEvent.getPacket() instanceof C03PacketPlayer && editGround) {
                        ((C03PacketPlayer) packetEvent.getPacket()).setOnGround(true);
                    }
                    break;
                case "Vulcan":
                    if(packetEvent.getPacket() instanceof C03PacketPlayer) {
                        C03PacketPlayer packet = (C03PacketPlayer) packetEvent.getPacket();
                        if(correctModulo) {
                            switch(this.vulcanMode.getValue()) {
                            case "Instant Motion":
                                mc.thePlayer.motionY = -500;
                                packet.setOnGround(true);
                            	break;
                            }
                        } else {
                        	mc.timer.timerSpeed = 1f;
                        }
                    }
                    break;
                case "Verus":
                    if(mc.thePlayer.fallDistance - mc.thePlayer.motionY > 3) {
                        mc.thePlayer.motionY = 0.0;
                        mc.thePlayer.motionX *= 0.6;
                        mc.thePlayer.motionZ *= 0.6;
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                    }
                    break;

            }
        }
    }

    @Listen
    public void onTickEvent(TickEvent tickEvent) {
    	switch(mode.getValue()) {
    	case "Spartan":
            spartanTimer.update();

            if(mc.thePlayer.fallDistance > 1.5 && spartanTimer.hasReached(10)) {
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 10, mc.thePlayer.posZ, true));
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 10, mc.thePlayer.posZ, true));
                spartanTimer.reset();
            }
    		break;
    	case "Vulcan":
    		break;
    	}
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

}

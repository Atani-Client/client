
package wtf.atani.module.impl.misc;

import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.atani.event.events.UpdateEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.SliderValue;

@ModuleInfo(name = "Regen", description = "Regenerates health faster", category = Category.MISCELLANEOUS)
public class Regen extends Module {
    private final SliderValue health = new SliderValue("Health", "At what health should regen work?", this, 15, 1, 19, 0);
    private final SliderValue packets = new SliderValue("Packets", "How much packets should be sent?", this, 10, 1, 100, 0);

    @Listen
    public void onUpdate(UpdateEvent updateEvent) {
        if(mc.thePlayer.getHealth() < health.getValue().floatValue() && mc.thePlayer.getHealth() > 0) {
            for(int i = 0; i < packets.getValue().intValue(); i++) {
                this.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.onGround));
            }
        }

    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
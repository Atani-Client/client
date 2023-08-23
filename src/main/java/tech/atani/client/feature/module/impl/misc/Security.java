package tech.atani.client.feature.module.impl.misc;

import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import tech.atani.client.listener.event.minecraft.network.PacketEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.value.impl.CheckBoxValue;

@ModuleData(name = "Security", description = "Patches common exploits", category = Category.MISCELLANEOUS)
public class Security extends Module {
    public final CheckBoxValue antiResourcePackExploit = new CheckBoxValue("Anti Resource Pack Exploit", "Prevent servers using the resource pack exploit to scan your files?", this, true);

    @Listen
    public void onUpdate(PacketEvent event) {
    	if(mc.thePlayer == null || mc.theWorld == null)
    		return;
        if(antiResourcePackExploit.getValue()) {
            if (event.getType() == PacketEvent.Type.INCOMING) {
                if (event.getPacket() instanceof S48PacketResourcePackSend) {
                    mc.thePlayer.sendQueue.addToSendQueue(
                            new C19PacketResourcePackStatus(((S48PacketResourcePackSend) event.getPacket()).getHash(),
                                    C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD)
                    );
                }
            }
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

}
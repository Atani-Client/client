package wtf.atani.module.impl.misc;

import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import wtf.atani.event.events.PacketEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.CheckBoxValue;

@ModuleInfo(name = "Security", description = "Patches common exploits", category = Category.MISCELLANEOUS)
public class Security extends Module {

    public final CheckBoxValue antiResourcePackExploit = new CheckBoxValue("Anti Resource Pack Exploit", "Prevent servers using the resource pack exploit to scan your files?", this, true);

    @Listen
    public void onUpdate(PacketEvent event) {
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
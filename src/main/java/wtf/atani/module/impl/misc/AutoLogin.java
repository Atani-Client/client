package wtf.atani.module.impl.misc;

import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.EnumChatFormatting;
import wtf.atani.event.events.PacketEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.CheckBoxValue;

@ModuleInfo(name = "AutoLogin", description = "Automatically logins for you", category = Category.MISCELLANEOUS)
public class AutoLogin extends Module {

    private final CheckBoxValue login = new CheckBoxValue("Login", "Should the module automatically login for you?", this, true);
    private final CheckBoxValue register = new CheckBoxValue("Register", "Should the module automatically register for you?", this, true);
    private final CheckBoxValue notify = new CheckBoxValue("Notify", "Should the module notify you when logging in?", this, true);

    @Listen
    public void onPacketEvent(PacketEvent packetEvent) {
        if (packetEvent.getType() == PacketEvent.Type.INCOMING && packetEvent.getPacket() instanceof S02PacketChat) {
            S02PacketChat s02 = (S02PacketChat) packetEvent.getPacket();
            String text = s02.getChatComponent().getUnformattedText();

            if (login.getValue()) {
                if (text.contains("/login") || text.contains("/login password") || text.contains("/login <password>")) {
                    mc.thePlayer.sendChatMessage("/login ligma123");
                    if (notify.getValue())
                        sendMessage("Logged in with the password: " + EnumChatFormatting.GOLD + "ligma123", true);
                }
            }
            if (register.getValue()) {
                if (text.contains("/register") || text.contains("/register password password") || text.contains("/register <password> <password>") || text.contains("/register <password> <repeat_password>")) {
                    mc.thePlayer.sendChatMessage("/register ligma123 ligma123");
                    if (notify.getValue())
                        sendMessage("Registered with the password: " + EnumChatFormatting.GOLD + " ligma123", true);
                }
            }
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

}
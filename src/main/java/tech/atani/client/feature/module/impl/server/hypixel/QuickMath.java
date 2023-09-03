package tech.atani.client.feature.module.impl.server.hypixel;

import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.EnumChatFormatting;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.listener.event.minecraft.network.PacketEvent;
import tech.atani.client.listener.radbus.Listen;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

@ModuleData(name = "QuickMath", identifier = "mc.hypixel.net QuickMath", description = "Automatically solves the math games.", category = Category.SERVER, supportedIPs = {"mc.hypixel.net"})
public class QuickMath extends Module {

    @Listen
    public void onPacketEvent(PacketEvent event) {
        if(event.getPacket() instanceof S02PacketChat) {
            String unformatted = EnumChatFormatting.getTextWithoutFormattingCodes(((S02PacketChat)event.getPacket()).getChatComponent().getUnformattedText()), text = unformatted.replace(" ", "");
            if(unformatted.contains("QUICK MATHS! Solve: ")) {
                String[] array = ((S02PacketChat) event.getPacket()).getChatComponent().getUnformattedText().split("Solve: ");
                ScriptEngineManager mgr = new ScriptEngineManager();
                ScriptEngine engine = mgr.getEngineByName("JavaScript");

                try {
                    sendPacketUnlogged(new C01PacketChatMessage(engine.eval(array[1].replace("x", "*")).toString()));
                } catch (ScriptException he) {
                    he.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

}

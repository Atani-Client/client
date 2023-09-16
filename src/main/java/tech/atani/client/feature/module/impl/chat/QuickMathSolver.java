package tech.atani.client.feature.module.impl.chat;

import cn.muyang.nativeobfuscator.Native;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.EnumChatFormatting;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.value.impl.StringBoxValue;
import tech.atani.client.listener.event.minecraft.network.PacketEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.utility.interfaces.Methods;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
@Native
@ModuleData(name = "QuickMathSolver", description = "Automatically solves quick maths", category = Category.CHAT)
public class QuickMathSolver extends Module {

    private final StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[]{"Hypixel", "Quickmaths"});

    @Listen
    public void onPacket(PacketEvent event) {
        if(Methods.mc.thePlayer == null || Methods.mc.theWorld == null)
            return;

        if (event.getPacket() instanceof S02PacketChat) {
            String unformatted = EnumChatFormatting.getTextWithoutFormattingCodes(((S02PacketChat) event.getPacket()).getChatComponent().getUnformattedText()), text = unformatted.replace(" ", "");

            if (mode.is("Hypixel") || mode.is("Quickmaths")) {
                if (unformatted.contains("QUICK MATHS! Solve: ") || unformatted.contains("[QM] QuickMaths: ")) {
                    String[] array = ((S02PacketChat) event.getPacket()).getChatComponent().getUnformattedText().split(mode.is("Hypixel") ? "Solve: " : "QuickMaths: ");
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

    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}

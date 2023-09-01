package tech.atani.client.feature.module.impl.server.hypixel;

import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.EnumChatFormatting;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.value.impl.SliderValue;
import tech.atani.client.listener.event.minecraft.network.PacketEvent;
import tech.atani.client.listener.event.minecraft.player.movement.UpdateEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.utility.math.time.TimeHelper;

@ModuleData(name = "AutoGG", identifier = "mc.hypixel.net AutoGG", description = "Automatically types gg", category = Category.SERVER, supportedIPs = {"mc.hypixel.net"})
public class AutoGG extends Module {

    public SliderValue<Long> delay = new SliderValue<>("Delay", "What will be the delay between gg's?", this, 1000L, 0L, 5000L, 0);

    private final TimeHelper timeHelper = new TimeHelper();

    @Listen
    public void onPacketEvent(PacketEvent event) {
        if(event.getPacket() instanceof S02PacketChat) {
            String unformatted = EnumChatFormatting.getTextWithoutFormattingCodes(((S02PacketChat)event.getPacket()).getChatComponent().getUnformattedText()), text = unformatted.replace(" ", "");
            if (text.startsWith("1stKiller")) {
               timeHelper.reset();
            }
        }
    }

    @Listen
    public void onUpdate(UpdateEvent event) {
        if(timeHelper != null && timeHelper.hasReached(delay.getValue())) {
            mc.thePlayer.sendChatMessage("gg");
            timeHelper.reset();
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

}

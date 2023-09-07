package tech.atani.client.feature.module.impl.server.hypixel;

import com.google.common.base.Supplier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.EnumChatFormatting;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.value.impl.CheckBoxValue;
import tech.atani.client.feature.value.impl.SliderValue;
import tech.atani.client.listener.event.minecraft.network.PacketEvent;
import tech.atani.client.listener.event.minecraft.player.movement.UpdateEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.utility.interfaces.Methods;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ModuleData(name = "AutoPit", identifier = "mc.hypixel.net AutoPit", description = "Improves QOL on the Hypixel Pit gamemode", category = Category.SERVER, supportedIPs = {"mc.hypixel.net"})
public class AutoPit extends Module {

    private final CheckBoxValue quickMath = new CheckBoxValue("Quick Math Solver", "Should the module solve quick math games?", this, true);
    private final CheckBoxValue autoBounty = new CheckBoxValue("Auto Bounty", "Should the module highlight players with high bounties?", this, true);

    private final SliderValue<Integer> minBounty = new SliderValue<>("Minimum Bounty", "What should be the minimum bounty?", this, 500, 50, 5000, 0, new Supplier[]{autoBounty::isEnabled});

    private final List<Entity> entities = new CopyOnWriteArrayList<>();

    @Listen
    public void onUpdate(UpdateEvent event) {
        if(autoBounty.getValue()) {
            entities.removeIf(player -> !mc.theWorld.playerEntities.contains(player));

            double maxDistanceSq = 200 * 200;

            for (Entity entity : mc.theWorld.loadedEntityList) {
                if (entity != mc.thePlayer && entity instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) entity;

                    double distanceSq = mc.thePlayer.getDistanceSqToEntity(player);

                    if (distanceSq <= maxDistanceSq) {
                        String display = player.getDisplayName().getUnformattedText();
                        String name = player.getCommandSenderName();

                        if (display.contains("§6§l")) {
                            String[] split = display.split(" ");
                            if (split.length > 2) {
                                int bounty = Integer.parseInt(
                                        split[split.length - 1]
                                                .replace("§6§1l", "")
                                                .replace("g", "")
                                );
                                if (bounty >= minBounty.getValue()) {
                                    if (!entities.contains(player)) {
                                        entities.add(player);
                                        sendMessage(name + " has " + bounty + "g on him!");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Listen
    public void onPacket(PacketEvent event) {
        if(Methods.mc.thePlayer == null || Methods.mc.theWorld == null)
            return;

        if(event.getPacket() instanceof S02PacketChat && quickMath.getValue()) {
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

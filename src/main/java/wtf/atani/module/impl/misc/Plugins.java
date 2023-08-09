package wtf.atani.module.impl.misc;

import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.util.EnumChatFormatting;
import wtf.atani.event.events.PacketEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.utils.math.time.TimeHelper;

import java.util.*;
import java.util.stream.Collectors;

@ModuleInfo(name = "Plugins", description = "Shows current server plugins", category = Category.MISCELLANEOUS)
public class Plugins extends Module {

    private final TimeHelper timer = new TimeHelper();

    @Listen
    public void onPacketEvent(PacketEvent event) {
        if (event.getType() == PacketEvent.Type.INCOMING && event.getPacket() instanceof S3APacketTabComplete && timer.hasReached(200L)) {
            String[] pluginFound = ((S3APacketTabComplete) event.getPacket()).getMatches();

            Map<String, String> commandToPluginMap = new HashMap<>();
            notPlugins.forEach((command, plugin) -> commandToPluginMap.put(command, plugin));

            List<String> filteredPlugins = Arrays.stream(pluginFound)
                    .filter(commandToPluginMap::containsKey)
                    .map(commandToPluginMap::get)
                    .distinct()
                    .collect(Collectors.toList());

            sendMessage("Found " + EnumChatFormatting.GOLD + filteredPlugins.size() + EnumChatFormatting.GRAY + " plugin(s): " + EnumChatFormatting.GOLD + Arrays.toString(filteredPlugins.toArray()) + EnumChatFormatting.GOLD, true);
            setEnabled(false);
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    private final Map<String, String> notPlugins = new HashMap<String, String>() {{
        // Minecraft
        put("/?", "Minecraft");
        put("/help", "Minecraft");
        put("/kill", "Minecraft");
        put("/msg", "Minecraft");
        put("/tell", "Minecraft");
        put("/whisper", "Minecraft");
        put("/w", "Minecraft");
        put("/m", "Minecraft");
        put("/t", "Minecraft");
        put("/clear", "Minecraft");
        put("/give", "Minecraft");
        put("/enchant", "Minecraft");
        put("/gamemode", "Minecraft");

        // Diona
        put("/set", "Diona");
        put("/settings", "Diona");
        put("/potion", "Diona");
        put("/lang", "Diona");
        put("/language", "Diona");
        put("/ac", "Diona");
        put("/anticheat", "Diona");
        put("/gameprotocol", "Diona");
        put("/damagetick", "Diona");
        put("/menu", "Diona");

        // Custom Chat Commands
        put("/discord", "Custom Chat Commands");

        // AntiCheats
        put("/ncp", "NoCheatPlus");
        put("/oncp", "Old NoCheatPlus");
        put("/nocheatplus", "NoCheatPlus");
        put("/vulcan", "Vulcan");
        put("/matrix", "Matrix");
        put("/grim", "Grim");
        put("/grimac", "Grim");
        put("/aac", "Advanced Anti Cheat");
        put("/polar", "Polar");
        put("/intave", "Intave");
        put("/gurei", "Gurei");
        put("/verus", "Verus");
        put("/agc", "AntiGamingChair");
        put("/spartan", "Spartan");
        put("/karhu", "Karhu");
        put("/alice", "Alice");
        put("/watchdog", "WatchDog");
        put("/wdr", "WatchDog");
        put("/atlas", "WatchDog");
        put("/wd", "WatchDog");
        put("/hypixel", "WatchDog");
        put("/incognito", "Incognito");

        // Basic anticheat commands
        put("/alerts", "AntiCheat");
        put("/logs", "AntiCheat");

        // EssentialsX
        put("/heal", "EssentialsX");
        put("/item", "EssentialsX");
        put("/back", "EssentialsX");
        put("/tpa", "EssentialsX");
        put("/tpahere", "EssentialsX");
        put("/tpdeny", "EssentialsX");
        put("/warp", "EssentialsX");
        put("/spawn", "EssentialsXSpawn");
        put("/fly", "EssentialsX");
        put("/gm", "EssentialsX");
        put("/r", "EssentialsX");
        put("/reply", "EssentialsX");
    }};

}
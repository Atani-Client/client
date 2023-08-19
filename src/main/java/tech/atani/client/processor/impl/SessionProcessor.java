package tech.atani.client.processor.impl;

import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.StringUtils;
import tech.atani.client.listener.event.events.minecraft.network.PacketEvent;
import tech.atani.client.listener.event.events.minecraft.game.RunTickEvent;
import tech.atani.client.listener.event.events.minecraft.world.WorldLoadEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.processor.Processor;
import tech.atani.client.processor.data.ProcessorInfo;
import tech.atani.client.utility.interfaces.Methods;

@ProcessorInfo(name = "SessionProcessor")
public class SessionProcessor extends Processor {

    private static SessionProcessor instance;

    public SessionProcessor() {
        this.instance = this;
    }

    private final long totalPlayTimeStart = System.currentTimeMillis();
    private long serverPlayTimeStart = -1;

    public int kills = 0;
    public int deaths = 0;
    public int wins = 0;
    private long totalPlayTime = 0;
    private long serverPlayTime = 0;

    private final String[] killMessages = {
        "was killed by %player%",
            "was crashed by %player%",
            "was alt+f4'd by %player%",
            "was hacked by %player%",
            "was deleted by %player%",
            "was thrown into the void by %player%"
    };

    private final String[] winMessages = {
            "you won",
            "winner: %player%"
    };

    private final String[] deathMessages = {
            "you died"
    };
    private SaveMode saveMode = SaveMode.NEVER;

    @Listen
    public void onLoadWorld(WorldLoadEvent worldLoadEvent) {
        this.serverPlayTimeStart = System.currentTimeMillis();
    }

    @Listen
    public void onTick(RunTickEvent runTickEvent) {
        if((!Methods.mc.isSingleplayer() && Methods.mc.getCurrentServerData() == null) || Methods.mc.theWorld == null || Methods.mc.thePlayer == null)
            this.serverPlayTimeStart = -1;
        this.totalPlayTime = System.currentTimeMillis() - totalPlayTimeStart;
        if(this.serverPlayTimeStart != -1)
            this.serverPlayTime = System.currentTimeMillis() - this.serverPlayTimeStart;
    }

    @Listen
    public void onPacket(PacketEvent packetEvent) {
        if(Methods.mc.thePlayer == null || Methods.mc.theWorld == null)
            return;
        if(packetEvent.getType() == PacketEvent.Type.INCOMING) {
            if(packetEvent.getPacket() instanceof S02PacketChat) {
                S02PacketChat s02PacketChat = (S02PacketChat) packetEvent.getPacket();
                String message = StringUtils.stripControlCodes(s02PacketChat.getChatComponent().getUnformattedText()).toLowerCase();
                for(String killMessageUnfinished : killMessages) {
                    String killMessage = killMessageUnfinished.replace("%player%", Methods.mc.thePlayer.getCommandSenderName());
                    if(message.contains(killMessage))
                        kills++;
                }
                for(String deathMessageUnfinished : deathMessages) {
                    String deathMessage = deathMessageUnfinished.replace("%player%", Methods.mc.thePlayer.getCommandSenderName());
                    if(message.contains(deathMessage))
                        deaths++;
                }
                for(String winMessageUnfinished : winMessages) {
                    String winMessage = winMessageUnfinished.replace("%player%", Methods.mc.thePlayer.getCommandSenderName());
                    if(message.contains(winMessage))
                        wins++;
                }
            }
        }
    }

    public boolean shouldSave() {
        return saveMode == SaveMode.ON_CLOSE;
    }

    public void setSaveMode(String saveMode) {
        for(SaveMode saveEnum : SaveMode.values())
            if(saveEnum.name().equalsIgnoreCase(saveMode))
                this.saveMode = saveEnum;
    }

    public enum SaveMode {
        ON_KICK, ON_CLOSE, NEVER;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getWins() {
        return wins;
    }

    public long getTotalPlayTime() {
        return totalPlayTime;
    }

    public long getServerPlayTime() {
        return serverPlayTime;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public static SessionProcessor getInstance() {
        return instance;
    }
}

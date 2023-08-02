package wtf.atani.module.impl.combat;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S41PacketServerDifficulty;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.world.WorldSettings;
import wtf.atani.combat.CombatManager;
import wtf.atani.event.events.PacketEvent;
import wtf.atani.event.events.UpdateEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.StringBoxValue;

import java.util.ArrayList;

@ModuleInfo(name = "AntiBot", description = "Prevents you from attacking bots in your game", category = Category.COMBAT)
public class AntiBot extends Module {

    private final ArrayList<Entity> bots = new ArrayList<>();

    public final StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[]{"Watchdog", "Matrix"});

    private boolean wasAdded = false;
    private String name;

    @Listen
    public final void onPacket(PacketEvent packetEvent) {
        if(mc.thePlayer == null || mc.theWorld == null) {
            return;
        }
        switch (mode.getValue()) {
            case "Matrix":
                Packet<?> packet = packetEvent.getPacket();

                if (packet instanceof S41PacketServerDifficulty) {
                    wasAdded = false;
                }

                if (packet instanceof S38PacketPlayerListItem) {
                    S38PacketPlayerListItem packetListItem = (S38PacketPlayerListItem) packet;
                    S38PacketPlayerListItem.AddPlayerData data = packetListItem.getPlayers().get(0);

                    if (data.getProfile() != null && data.getProfile().getName() != null) {
                        name = data.getProfile().getName();

                        if (!wasAdded) {
                            wasAdded = name.equals(mc.thePlayer.getCommandSenderName());
                        } else if (!mc.thePlayer.isSpectator() && !mc.thePlayer.capabilities.allowFlying &&
                                (data.getPing() != 0) &&
                                (data.getGameMode() != WorldSettings.GameType.NOT_SET)) {
                            packetEvent.setCancelled(true);
                        }
                    }
                }
                break;
        }
    }


    @Listen
    public final void onUpdate(UpdateEvent updateEvent) {
        switch (mode.getValue()) {
            case "Watchdog":
                mc.theWorld.playerEntities.forEach(player -> {
                    final NetworkPlayerInfo info = mc.getNetHandler().getPlayerInfo(player.getUniqueID());
                    if (info == null) {
                        CombatManager.getInstance().addBot(player);
                    } else {
                        CombatManager.getInstance().removeBot(player);
                    }
                });
                break;
        }
        for(Entity entity : bots) {
            if(!CombatManager.getInstance().hasBot(entity))
                CombatManager.getInstance().addBot(entity);
        }
        for(Entity entity : CombatManager.getInstance().getBots()) {
            if(!this.bots.contains(entity)) {
                CombatManager.getInstance().removeBot(entity);
            }
        }
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        for(Entity entity : bots) {
            if(CombatManager.getInstance().hasBot(entity))
                CombatManager.getInstance().removeBot(entity);
        }
    }
}

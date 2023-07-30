package wtf.atani.module.impl.combat;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import wtf.atani.combat.CombatManager;
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

    public final StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[]{"Watchdog"});

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

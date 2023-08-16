package wtf.atani.combat;

import net.minecraft.entity.Entity;
import wtf.atani.combat.interfaces.IgnoreList;
import wtf.atani.event.events.PostTickEvent;
import wtf.atani.event.events.TickEvent;
import wtf.atani.event.events.UpdateEvent;
import wtf.atani.event.handling.EventHandling;
import wtf.atani.event.radbus.Listen;
import wtf.atani.utils.interfaces.Methods;
import wtf.atani.utils.storage.EntityStorage;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CombatManager implements Methods {

    private static CombatManager instance;
    private final List<Entity> ignored = new ArrayList<>();
    private final List<IgnoreList> ignoreLists = new ArrayList<>();

    public CombatManager() {
        instance = this;
        EventHandling.getInstance().registerListener(this);
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        this.ignored.clear();

        if(mc.theWorld == null || mc.thePlayer == null)
            return;

        for(Entity entity : mc.theWorld.loadedEntityList) {
            for(IgnoreList ignoreList : ignoreLists) {
                if(ignoreList.getIgnored().stream().filter(entity1 -> entity1.getEntityId() == entity.getEntityId()).findFirst().orElse(null) != null) {
                    this.ignored.add(entity);
                }
            }
        }
    }

    public void addIgnoreList(IgnoreList ignoreList) {
        this.ignoreLists.add(ignoreList);
    }

    public final boolean isIgnored(Entity entity) {
        return ignored.stream().filter(entity1 -> entity1.getEntityId() == entity.getEntityId()).findFirst().orElse(null) != null;
    }

    public final List<Entity> getIgnored() {
        return ignored;
    }

    public static CombatManager getInstance() {
        return instance;
    }
}

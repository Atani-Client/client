package wtf.atani.combat;

import de.florianmichael.rclasses.storage.Storage;
import net.minecraft.entity.Entity;
import wtf.atani.utils.storage.EntityStorage;

import java.util.ArrayList;
import java.util.List;

public class CombatManager {

    private static CombatManager instance;

    public CombatManager() {
        instance = this;
    }

    private final EntityStorage bots = new EntityStorage(true);

    public final void addBot(Entity entity) {
        bots.add(entity);
    }

    public final void removeBot(Entity entity) {
        bots.add(entity);
    }

    public final boolean hasBot(Entity entity) {
        bots.getList().contains(entity);
    }

    public final List<Entity> getBots() {
        return bots.getList();
    }

    public static CombatManager getInstance() {
        return instance;
    }
}

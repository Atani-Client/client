package wtf.atani.utils.storage;

import de.florianmichael.rclasses.storage.Storage;
import net.minecraft.entity.Entity;
import wtf.atani.event.events.WorldLoadEvent;
import wtf.atani.event.handling.EventHandling;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;

public class EntityStorage extends Storage<Entity> {

    private final boolean resetOnWorld;

    public EntityStorage(boolean resetOnWorld) {
        this.resetOnWorld = resetOnWorld;
    }

    @Override
    public void init() {
        EventHandling.getInstance().registerListener(this);
    }

    public Entity getEntity(String name) {
        return this.getList().stream().filter(entity -> entity.getCommandSenderName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    @Listen
    public void onWorldLoad(WorldLoadEvent worldLoadEvent) {
        if(this.resetOnWorld) {
            this.getList().clear();
        }
    }

}

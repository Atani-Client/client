package wtf.atani.event.events;

import net.minecraft.entity.Entity;
import wtf.atani.event.Event;

public class EntityRendererEvent extends Event {
    Entity entity;

    public EntityRendererEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
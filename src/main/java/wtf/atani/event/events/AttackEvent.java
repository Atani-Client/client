package wtf.atani.event.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import wtf.atani.event.Event;

public class AttackEvent extends Event {
    private final Entity entity;

    public AttackEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}

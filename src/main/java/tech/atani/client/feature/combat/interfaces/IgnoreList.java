package tech.atani.client.feature.combat.interfaces;

import net.minecraft.entity.Entity;

import java.util.List;

public interface IgnoreList {

    boolean shouldSkipEntity(Entity entity);

}

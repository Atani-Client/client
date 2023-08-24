package tech.atani.client.feature.module.impl.movement;

import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.listener.event.minecraft.player.movement.SafeWalkEvent;
import tech.atani.client.listener.radbus.Listen;

@ModuleData(name = "SafeWalk", description = "Prevents you from falling off edges", category = Category.MOVEMENT)
public class SafeWalk extends Module {

    @Listen
    public void onSafeWalkEvent(SafeWalkEvent event) {
        if(mc.thePlayer == null && mc.theWorld == null)
            return;

        event.setSafe(true);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}

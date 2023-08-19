package tech.atani.client.feature.module.impl.movement;

import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;

//Hooked in Entity class
@ModuleData(name = "SafeWalk", description = "Prevents you from falling off edges", category = Category.MOVEMENT)
public class SafeWalk extends Module {

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}

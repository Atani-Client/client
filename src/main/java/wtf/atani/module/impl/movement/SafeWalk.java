package wtf.atani.module.impl.movement;

import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;

@ModuleInfo(name = "SafeWalk", description = "Prevents you from falling off edges", category = Category.MOVEMENT)
public class SafeWalk extends Module {

    //Hooked in Entity class

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}

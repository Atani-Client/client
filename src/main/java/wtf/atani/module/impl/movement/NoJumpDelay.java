package wtf.atani.module.impl.movement;

import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;

// Hooked in EntityLivingBase.java
@ModuleInfo(name = "NoJumpDelay", description = "Removes jump delays when holding jump key", category = Category.PLAYER)
public class NoJumpDelay extends Module {

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
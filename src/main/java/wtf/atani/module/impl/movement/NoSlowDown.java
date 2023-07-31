package wtf.atani.module.impl.movement;

import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;

@ModuleInfo(name = "NoSlowDown", description = "Removes the blocking & eating slowdown", category = Category.MOVEMENT)
public class NoSlowDown extends Module {

    //Hooked in EntityLivingBase class & EntityPlayerSP class

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}

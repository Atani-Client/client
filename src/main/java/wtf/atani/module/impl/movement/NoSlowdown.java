package wtf.atani.module.impl.movement;

import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;

@ModuleInfo(name = "NoSlowdown", description = "Removes the blocking & eating slowdown", category = Category.MOVEMENT)
public class NoSlowdown extends Module {

    //Hooked in EntityLivingBase class & EntityPlayerSP class

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}

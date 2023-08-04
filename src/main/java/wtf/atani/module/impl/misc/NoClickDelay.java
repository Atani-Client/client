package wtf.atani.module.impl.misc;

import wtf.atani.event.events.UpdateEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;

@ModuleInfo(name = "NoClickDelay", description = "Removes clicking delay", category = Category.MISCELLANEOUS)
public class NoClickDelay extends Module {

    @Listen
    public void onUpdate(UpdateEvent updateEvent) {
        mc.leftClickCounter = 0;
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

}
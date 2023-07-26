package wtf.atani.module.impl.misc;

import wtf.atani.event.events.UpdateEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;

@ModuleInfo(name = "FastPlace", description = "Removes placing delay while holding use key", category = Category.MISCELLANEOUS)
public class FastPlace extends Module {

    @Listen
    public void onUpdate(UpdateEvent updateEvent) {
        mc.rightClickDelayTimer = 0;
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {
        mc.rightClickDelayTimer = 6;
    }

}
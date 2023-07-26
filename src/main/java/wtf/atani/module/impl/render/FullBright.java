package wtf.atani.module.impl.render;

import wtf.atani.event.events.UpdateEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;

@ModuleInfo(name = "FullBright", description = "Increases brightness to the maximum", category = Category.RENDER)
public class FullBright extends Module {
    private float oldGamma;

    @Listen
    public void onUpdate(UpdateEvent updateEvent) {
        mc.gameSettings.gammaSetting = 100F;
    }

    @Override
    public void onEnable() {
        this.oldGamma = mc.gameSettings.gammaSetting;
    }

    @Override
    public void onDisable() {
        mc.gameSettings.gammaSetting = this.oldGamma;
    }

}

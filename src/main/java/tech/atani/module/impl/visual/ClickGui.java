package tech.atani.module.impl.visual;

import tech.atani.gui.imgui.ImGuiScreen;
import tech.atani.module.Module;

@Module.Info(name = "Click Gui", category = Module.Category.VISUAL, keyBind = 54)
public class ClickGui extends Module {

    @Override
    protected void onEnable() {
        mc.display(new ImGuiScreen());
        toggle();
    }
}

package wtf.atani.module.impl.hud;

import com.google.common.base.Supplier;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.StringBoxValue;

// Hooked in GuiPlayerTabOverlay class
@ModuleInfo(name = "CustomTabList", description = "Improves the minecraft default tab list.", category = Category.HUD)
public class CustomTabList extends Module {

    public final StringBoxValue background = new StringBoxValue("Background", "Which background should the tab list use?", this, new String[] {"Normal", "Off"});
    public final CheckBoxValue customFont = new CheckBoxValue("Custom Font", "Should the tab list have a custom font?", this, false);
    public final CheckBoxValue backgroundBlur = new CheckBoxValue("Blur Background", "Should the tab list have a blur?", this, false, new Supplier[]{() -> background.is("Normal")});

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

}

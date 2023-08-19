package wtf.atani.module.impl.hud;

import com.google.common.base.Supplier;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.StringBoxValue;

// Hooked in GuiIngame class
@ModuleInfo(name = "CustomScoreboard", description = "Improves the minecraft default scoreboard.", category = Category.HUD)
public class CustomScoreboard extends Module {

    public final StringBoxValue background = new StringBoxValue("Background", "Which background should the scoreboard use?", this, new String[] {"Normal", "Off"});
    public final CheckBoxValue customFont = new CheckBoxValue("Custom Font", "Should the scoreboard have a custom font?", this, false);
    public final CheckBoxValue backgroundBlur = new CheckBoxValue("Blur Background", "Should the scoreboard have a blur?", this, false, new Supplier[]{() -> background.is("Normal")});

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

}
